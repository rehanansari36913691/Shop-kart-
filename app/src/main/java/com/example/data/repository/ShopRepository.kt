package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AddressEntity
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.CartItemEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RecentlyViewedEntity
import com.example.data.local.entities.ReviewEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.WishlistItemEntity
import com.example.data.model.CartItemDetail
import com.example.data.model.OrderPricingSummary
import com.example.data.model.OrderedItem
import com.example.data.model.SearchFilterState
import com.example.data.model.SortOption
import com.example.data.util.SecurityUtils
import com.example.data.util.TelegramService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class ShopRepository(
    private val db: AppDatabase,
    private val telegramService: TelegramService = TelegramService()
) {
    // -------------------------------------------------------------
    // USERS & AUTHENTICATION
    // -------------------------------------------------------------
    suspend fun getUserById(id: Long): UserEntity? = db.userDao().getUserById(id)

    suspend fun authenticateUser(identifier: String, plainPassword: String): Result<UserEntity> {
        val user = db.userDao().findByIdentifier(identifier.trim())
            ?: return Result.failure(Exception("No account found with this email or phone number."))

        val isPasswordCorrect = SecurityUtils.verifyPassword(plainPassword, user.passwordHash)
        return if (isPasswordCorrect) {
            Result.success(user)
        } else {
            Result.failure(Exception("Incorrect password. Please try again."))
        }
    }

    suspend fun registerUser(fullName: String, email: String, phone: String, plainPassword: String): Result<UserEntity> {
        if (fullName.isBlank()) return Result.failure(Exception("Full name is required"))
        if (email.isBlank() || !email.contains("@")) return Result.failure(Exception("Valid email address is required"))
        if (phone.isBlank() || phone.length < 10) return Result.failure(Exception("Valid 10-digit mobile number is required"))
        if (plainPassword.length < 6) return Result.failure(Exception("Password must be at least 6 characters long"))

        val existingByEmail = db.userDao().getUserByEmail(email.trim())
        if (existingByEmail != null) return Result.failure(Exception("An account with this email already exists."))

        val existingByPhone = db.userDao().getUserByPhone(phone.trim())
        if (existingByPhone != null) return Result.failure(Exception("An account with this phone number already exists."))

        val newUser = UserEntity(
            fullName = fullName.trim(),
            email = email.trim().lowercase(Locale.ROOT),
            phone = phone.trim(),
            passwordHash = SecurityUtils.hashPassword(plainPassword),
            role = "CUSTOMER"
        )
        val id = db.userDao().insertUser(newUser)
        return Result.success(newUser.copy(id = id))
    }

    fun getAllCustomers(): Flow<List<UserEntity>> = db.userDao().getAllCustomers()
    fun getCustomerCount(): Flow<Int> = db.userDao().getCustomerCount()

    // -------------------------------------------------------------
    // ADDRESS MANAGEMENT
    // -------------------------------------------------------------
    fun getAddressesForUser(userId: Long): Flow<List<AddressEntity>> =
        db.addressDao().getAddressesForUser(userId)

    suspend fun getDefaultAddress(userId: Long): AddressEntity? =
        db.addressDao().getDefaultAddress(userId)

    suspend fun saveAddress(address: AddressEntity): Long {
        if (address.isDefault) {
            db.addressDao().clearDefaultFlags(address.userId)
        }
        return if (address.id == 0L) {
            db.addressDao().insertAddress(address)
        } else {
            db.addressDao().updateAddress(address)
            address.id
        }
    }

    suspend fun deleteAddress(address: AddressEntity) {
        db.addressDao().deleteAddress(address)
    }

    // -------------------------------------------------------------
    // PRODUCTS & SMART SEARCH
    // -------------------------------------------------------------
    fun getAllActiveProducts(): Flow<List<ProductEntity>> = db.productDao().getAllActiveProducts()
    fun getAllProductsForAdmin(): Flow<List<ProductEntity>> = db.productDao().getAllProductsForAdmin()
    fun getDeals(): Flow<List<ProductEntity>> = db.productDao().getDeals()
    fun getBestSellers(): Flow<List<ProductEntity>> = db.productDao().getBestSellers()
    fun getProductCount(): Flow<Int> = db.productDao().getProductCount()
    fun getProductByIdFlow(id: Long): Flow<ProductEntity?> = db.productDao().getProductByIdFlow(id)

    suspend fun getProductById(id: Long): ProductEntity? = db.productDao().getProductById(id)
    suspend fun getProductsByIds(ids: List<Long>): List<ProductEntity> = db.productDao().getProductsByIds(ids)

    suspend fun saveProduct(product: ProductEntity): Long {
        return if (product.id == 0L) {
            db.productDao().insertProduct(product)
        } else {
            db.productDao().updateProduct(product)
            product.id
        }
    }

    suspend fun deleteProduct(product: ProductEntity) {
        db.productDao().deleteProduct(product)
    }

    suspend fun setProductHidden(id: Long, hidden: Boolean) {
        db.productDao().setProductHidden(id, hidden)
    }

    suspend fun updateProductStock(id: Long, newStock: Int) {
        db.productDao().updateStock(id, newStock)
    }

    /**
     * Search with Natural Language, Synonyms, and Automatic Filters.
     * Supports:
     * - "juta", "joota", "shoes", "shoe", "sneakers" -> Footwear/Shoes
     * - "mobile", "phone", "smartphone" -> Mobiles
     * - "mobile under 10000", "shoes below 2000" -> Extracts price constraints
     * - "red t shirt", "black shoes" -> Color + category matching
     */
    fun searchProducts(filterState: SearchFilterState): Flow<List<ProductEntity>> {
        return db.productDao().getAllActiveProducts().map { allProducts ->
            var query = filterState.query.trim().lowercase(Locale.ROOT)
            var effectiveMinPrice = filterState.minPrice
            var effectiveMaxPrice = filterState.maxPrice

            // Parse price intent from query if present e.g. "under 10000" / "below 2000"
            val underRegex = Regex("""(?:under|below|less than|upto|up to)\s*(?:rs\.?|inr|₹)?\s*(\d+)""", RegexOption.IGNORE_CASE)
            val underMatch = underRegex.find(query)
            if (underMatch != null) {
                val parsedLimit = underMatch.groupValues[1].toDoubleOrNull()
                if (parsedLimit != null && effectiveMaxPrice == null) {
                    effectiveMaxPrice = parsedLimit
                }
                query = query.replace(underMatch.value, "").trim()
            }

            val aboveRegex = Regex("""(?:above|over|more than)\s*(?:rs\.?|inr|₹)?\s*(\d+)""", RegexOption.IGNORE_CASE)
            val aboveMatch = aboveRegex.find(query)
            if (aboveMatch != null) {
                val parsedMin = aboveMatch.groupValues[1].toDoubleOrNull()
                if (parsedMin != null && effectiveMinPrice == null) {
                    effectiveMinPrice = parsedMin
                }
                query = query.replace(aboveMatch.value, "").trim()
            }

            // Expand synonyms
            val searchTokens = query.split(" ").filter { it.isNotBlank() }
            val expandedTokens = mutableListOf<String>()
            for (token in searchTokens) {
                expandedTokens.add(token)
                when (token) {
                    "juta", "joota", "jutey", "joote", "shoe", "shoes" -> {
                        expandedTokens.addAll(listOf("footwear", "shoe", "shoes", "sneakers", "running"))
                    }
                    "phone", "fon", "mobile", "cellphone" -> {
                        expandedTokens.addAll(listOf("mobile", "smartphone", "5g", "phone"))
                    }
                    "kapda", "kapde", "tshirt", "t-shirt", "t-shirts", "tees" -> {
                        expandedTokens.addAll(listOf("clothing", "t-shirt", "shirt", "tshirt", "fashion"))
                    }
                    "watch", "ghadi" -> {
                        expandedTokens.addAll(listOf("smartwatch", "watch", "fitness"))
                    }
                    "earbuds", "airpods", "earphone", "earphones" -> {
                        expandedTokens.addAll(listOf("earbuds", "audio", "tws", "wireless", "headphones"))
                    }
                }
            }

            allProducts.filter { product ->
                // Category Filter
                if (filterState.selectedCategory != "All" && !product.category.equals(filterState.selectedCategory, ignoreCase = true)) {
                    return@filter false
                }

                // Brand Filter
                if (filterState.selectedBrand != "All" && !product.brand.equals(filterState.selectedBrand, ignoreCase = true)) {
                    return@filter false
                }

                // Price Filters
                if (effectiveMinPrice != null && product.price < effectiveMinPrice) {
                    return@filter false
                }
                if (effectiveMaxPrice != null && product.price > effectiveMaxPrice) {
                    return@filter false
                }

                // Rating Filter
                if (product.rating < filterState.minRating) {
                    return@filter false
                }

                // Discount Filter
                if (product.discountPercent < filterState.minDiscount) {
                    return@filter false
                }

                // Stock Filter
                if (filterState.inStockOnly && product.stock <= 0) {
                    return@filter false
                }

                // Query match score
                if (expandedTokens.isNotEmpty()) {
                    val searchableBlob = "${product.name} ${product.brand} ${product.category} ${product.subcategory} ${product.description} ${product.keywords} ${product.colorsJson}".lowercase(Locale.ROOT)
                    expandedTokens.any { token -> searchableBlob.contains(token) }
                } else {
                    true
                }
            }.let { filtered ->
                // Apply Sorting
                when (filterState.sortBy) {
                    SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.price }
                    SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.price }
                    SortOption.RATING -> filtered.sortedByDescending { it.rating }
                    SortOption.NEWEST -> filtered.sortedByDescending { it.createdAt }
                    SortOption.DISCOUNT -> filtered.sortedByDescending { it.discountPercent }
                    SortOption.RELEVANCE -> {
                        if (expandedTokens.isEmpty()) {
                            filtered.sortedByDescending { it.isBestSeller }
                        } else {
                            filtered.sortedByDescending { product ->
                                var score = 0
                                val nameLower = product.name.lowercase(Locale.ROOT)
                                val keywordsLower = product.keywords.lowercase(Locale.ROOT)
                                val brandLower = product.brand.lowercase(Locale.ROOT)

                                for (token in expandedTokens) {
                                    if (nameLower.contains(token)) score += 10
                                    if (keywordsLower.contains(token)) score += 8
                                    if (brandLower.contains(token)) score += 5
                                }
                                if (product.isBestSeller) score += 4
                                if (product.isDeal) score += 2
                                score
                            }
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // CART & WISHLIST
    // -------------------------------------------------------------
    fun getCartDetails(userId: Long): Flow<List<CartItemDetail>> {
        return combine(
            db.cartDao().getActiveCartItems(userId),
            db.productDao().getAllActiveProducts()
        ) { cartItems, products ->
            val productMap = products.associateBy { it.id }
            cartItems.mapNotNull { cartItem ->
                productMap[cartItem.productId]?.let { product ->
                    CartItemDetail(
                        cartItemId = cartItem.id,
                        product = product,
                        quantity = cartItem.quantity,
                        selectedSize = cartItem.selectedSize,
                        selectedColor = cartItem.selectedColor,
                        isSavedForLater = false
                    )
                }
            }
        }
    }

    fun getSavedForLaterDetails(userId: Long): Flow<List<CartItemDetail>> {
        return combine(
            db.cartDao().getSavedForLaterItems(userId),
            db.productDao().getAllActiveProducts()
        ) { savedItems, products ->
            val productMap = products.associateBy { it.id }
            savedItems.mapNotNull { item ->
                productMap[item.productId]?.let { product ->
                    CartItemDetail(
                        cartItemId = item.id,
                        product = product,
                        quantity = item.quantity,
                        selectedSize = item.selectedSize,
                        selectedColor = item.selectedColor,
                        isSavedForLater = true
                    )
                }
            }
        }
    }

    suspend fun addToCart(
        userId: Long,
        productId: Long,
        quantity: Int = 1,
        selectedSize: String = "",
        selectedColor: String = ""
    ) {
        val existing = db.cartDao().getCartItem(userId, productId)
        if (existing != null) {
            db.cartDao().updateQuantity(existing.id, existing.quantity + quantity)
        } else {
            db.cartDao().insertCartItem(
                CartItemEntity(
                    userId = userId,
                    productId = productId,
                    quantity = quantity,
                    selectedSize = selectedSize,
                    selectedColor = selectedColor,
                    isSavedForLater = false
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        if (quantity <= 0) {
            db.cartDao().deleteCartItem(CartItemEntity(id = cartItemId, userId = 0, productId = 0))
        } else {
            db.cartDao().updateQuantity(cartItemId, quantity)
        }
    }

    suspend fun toggleSaveForLater(cartItemId: Long, saveForLater: Boolean) {
        db.cartDao().toggleSaveForLater(cartItemId, saveForLater)
    }

    suspend fun removeCartItem(cartItemId: Long) {
        db.cartDao().deleteCartItem(CartItemEntity(id = cartItemId, userId = 0, productId = 0))
    }

    suspend fun clearCart(userId: Long) {
        db.cartDao().clearActiveCart(userId)
    }

    // Wishlist
    fun getWishlistProducts(userId: Long): Flow<List<ProductEntity>> {
        return combine(
            db.wishlistDao().getWishlistItems(userId),
            db.productDao().getAllActiveProducts()
        ) { wishlistItems, products ->
            val productMap = products.associateBy { it.id }
            wishlistItems.mapNotNull { productMap[it.productId] }
        }
    }

    fun isProductInWishlist(userId: Long, productId: Long): Flow<Boolean> =
        db.wishlistDao().isProductInWishlist(userId, productId)

    suspend fun toggleWishlist(userId: Long, productId: Long) {
        val existing = db.wishlistDao().getWishlistItem(userId, productId)
        if (existing != null) {
            db.wishlistDao().deleteWishlistItem(existing)
        } else {
            db.wishlistDao().insertWishlistItem(
                WishlistItemEntity(userId = userId, productId = productId)
            )
        }
    }

    // -------------------------------------------------------------
    // PRICING & DELIVERY CALCULATOR
    // -------------------------------------------------------------
    suspend fun calculatePricing(
        items: List<CartItemDetail>,
        appliedCoupon: CouponEntity?
    ): OrderPricingSummary {
        val subtotal = items.sumOf { it.itemTotal }
        val thresholdStr = db.appSettingsDao().getSettingValue("delivery_threshold") ?: "100.0"
        val standardFeeStr = db.appSettingsDao().getSettingValue("delivery_fee") ?: "79.0"

        val freeThreshold = thresholdStr.toDoubleOrNull() ?: 100.0
        val standardFee = standardFeeStr.toDoubleOrNull() ?: 79.0

        val isFreeDelivery = subtotal >= freeThreshold
        val deliveryCharge = if (subtotal > 0 && !isFreeDelivery) standardFee else 0.0

        var discount = 0.0
        if (appliedCoupon != null && subtotal >= appliedCoupon.minOrderAmount) {
            discount = if (appliedCoupon.discountType == "PERCENTAGE") {
                val calc = (subtotal * (appliedCoupon.discountValue / 100.0))
                minOf(calc, appliedCoupon.maxDiscountAmount)
            } else {
                appliedCoupon.discountValue
            }
        }

        val finalTotal = maxOf(0.0, subtotal + deliveryCharge - discount)
        val totalSavings = items.sumOf { it.itemSavings } + discount

        return OrderPricingSummary(
            subtotal = subtotal,
            deliveryCharge = deliveryCharge,
            discount = discount,
            finalTotal = finalTotal,
            totalSavings = totalSavings,
            isFreeDelivery = isFreeDelivery
        )
    }

    // -------------------------------------------------------------
    // ORDERS & PAYMENT FLOW
    // -------------------------------------------------------------
    suspend fun createOrder(
        userId: Long,
        items: List<CartItemDetail>,
        address: AddressEntity,
        paymentMethod: String, // "UPI", "CARD", "NET_BANKING"
        upiTransactionId: String = "",
        upiAppUsed: String = "",
        cardLast4: String = "",
        bankName: String = "",
        appliedCoupon: CouponEntity? = null
    ): Result<OrderEntity> {
        if (items.isEmpty()) {
            return Result.failure(Exception("Your cart is empty."))
        }

        val pricing = calculatePricing(items, appliedCoupon)
        val orderId = SecurityUtils.generateOrderId()

        val orderedItemsList = items.map {
            var firstImage = ""
            try {
                val jsonArr = JSONArray(it.product.imagesJson)
                if (jsonArr.length() > 0) firstImage = jsonArr.getString(0)
            } catch (_: Exception) {}

            OrderedItem(
                productId = it.product.id,
                productName = it.product.name,
                brand = it.product.brand,
                price = it.product.price,
                mrp = it.product.mrp,
                quantity = it.quantity,
                selectedSize = it.selectedSize,
                selectedColor = it.selectedColor,
                imageUrl = firstImage
            )
        }

        val itemsJson = JSONArray().apply {
            orderedItemsList.forEach { item ->
                put(JSONObject().apply {
                    put("productId", item.productId)
                    put("productName", item.productName)
                    put("brand", item.brand)
                    put("price", item.price)
                    put("mrp", item.mrp)
                    put("quantity", item.quantity)
                    put("selectedSize", item.selectedSize)
                    put("selectedColor", item.selectedColor)
                    put("imageUrl", item.imageUrl)
                })
            }
        }.toString()

        val addressJson = JSONObject().apply {
            put("fullName", address.fullName)
            put("mobile", address.mobile)
            put("email", address.email)
            put("house", address.house)
            put("area", address.area)
            put("landmark", address.landmark)
            put("pincode", address.pincode)
            put("city", address.city)
            put("state", address.state)
            put("addressType", address.addressType)
            put("deliveryInstructions", address.deliveryInstructions)
        }.toString()

        val initialPaymentStatus = "Payment Verification Pending"
        val initialOrderStatus = "Payment Verification Pending"

        val order = OrderEntity(
            id = orderId,
            userId = userId,
            itemsJson = itemsJson,
            subtotal = pricing.subtotal,
            deliveryCharge = pricing.deliveryCharge,
            discountAmount = pricing.discount,
            couponCode = appliedCoupon?.code ?: "",
            finalTotal = pricing.finalTotal,
            addressJson = addressJson,
            paymentMethod = paymentMethod,
            paymentStatus = initialPaymentStatus,
            orderStatus = initialOrderStatus,
            upiTransactionId = upiTransactionId,
            upiAppUsed = upiAppUsed,
            cardLast4 = cardLast4,
            bankName = bankName,
            expectedDeliveryDate = "Delivery in 2-3 Business Days"
        )

        db.orderDao().insertOrder(order)
        db.cartDao().clearActiveCart(userId)

        // Increment coupon usage if applied
        if (appliedCoupon != null) {
            db.couponDao().incrementUsage(appliedCoupon.id)
        }

        // Decrement product stock
        items.forEach {
            val newStock = maxOf(0, it.product.stock - it.quantity)
            db.productDao().updateStock(it.product.id, newStock)
        }

        // Dispatch Telegram Notification
        dispatchTelegramAlert(
            title = "🛒 <b>NEW ORDER PLACED</b>",
            orderId = orderId,
            customerName = address.fullName,
            phone = address.mobile,
            amount = pricing.finalTotal,
            paymentMethod = "$paymentMethod ${if (upiAppUsed.isNotBlank()) "($upiAppUsed)" else ""}",
            status = initialOrderStatus,
            extra = if (upiTransactionId.isNotBlank()) "<b>UTR / Ref ID:</b> <code>$upiTransactionId</code>" else ""
        )

        return Result.success(order)
    }

    fun getOrdersForUser(userId: Long): Flow<List<OrderEntity>> = db.orderDao().getOrdersForUser(userId)
    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?> = db.orderDao().getOrderByIdFlow(orderId)
    suspend fun getOrderById(orderId: String): OrderEntity? = db.orderDao().getOrderById(orderId)

    fun getAllOrdersForAdmin(): Flow<List<OrderEntity>> = db.orderDao().getAllOrders()
    fun getPendingPaymentOrders(): Flow<List<OrderEntity>> = db.orderDao().getPendingPaymentOrders()
    fun getProcessingOrders(): Flow<List<OrderEntity>> = db.orderDao().getProcessingOrders()
    fun getShippedOrders(): Flow<List<OrderEntity>> = db.orderDao().getShippedOrders()
    fun getDeliveredOrders(): Flow<List<OrderEntity>> = db.orderDao().getDeliveredOrders()
    fun getReturnAndRefundOrders(): Flow<List<OrderEntity>> = db.orderDao().getReturnAndRefundOrders()
    fun getTotalOrderCount(): Flow<Int> = db.orderDao().getTotalOrderCount()
    fun getTotalRevenue(): Flow<Double?> = db.orderDao().getTotalRevenue()

    // Admin Actions
    suspend fun confirmPayment(orderId: String) {
        val order = db.orderDao().getOrderById(orderId) ?: return
        db.orderDao().updatePaymentAndStatus(
            orderId = orderId,
            paymentStatus = "Payment Confirmed",
            orderStatus = "Payment Confirmed"
        )
        dispatchTelegramAlert(
            title = "✅ <b>PAYMENT CONFIRMED</b>",
            orderId = orderId,
            customerName = "Customer",
            phone = "",
            amount = order.finalTotal,
            paymentMethod = order.paymentMethod,
            status = "Payment Confirmed"
        )
    }

    suspend fun rejectPayment(orderId: String, reason: String = "Transaction ID verification failed") {
        val order = db.orderDao().getOrderById(orderId) ?: return
        db.orderDao().updatePaymentAndStatus(
            orderId = orderId,
            paymentStatus = "Payment Rejected",
            orderStatus = "Payment Rejected"
        )
        dispatchTelegramAlert(
            title = "❌ <b>PAYMENT REJECTED</b>",
            orderId = orderId,
            customerName = "Customer",
            phone = "",
            amount = order.finalTotal,
            paymentMethod = order.paymentMethod,
            status = "Payment Rejected",
            extra = "<b>Reason:</b> $reason"
        )
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        val order = db.orderDao().getOrderById(orderId) ?: return
        db.orderDao().updateOrderStatus(orderId, newStatus)
        dispatchTelegramAlert(
            title = "📦 <b>ORDER STATUS UPDATED</b>",
            orderId = orderId,
            customerName = "Customer",
            phone = "",
            amount = order.finalTotal,
            paymentMethod = order.paymentMethod,
            status = newStatus
        )
    }

    suspend fun updateTrackingInfo(
        orderId: String,
        trackingId: String,
        courier: String,
        shippingDate: String,
        expectedDelivery: String
    ) {
        val order = db.orderDao().getOrderById(orderId) ?: return
        db.orderDao().updateTracking(orderId, trackingId, courier, shippingDate, expectedDelivery)
        db.orderDao().updateOrderStatus(orderId, "Shipped")
        dispatchTelegramAlert(
            title = "🚚 <b>ORDER SHIPPED & TRACKING UPDATED</b>",
            orderId = orderId,
            customerName = "Customer",
            phone = "",
            amount = order.finalTotal,
            paymentMethod = order.paymentMethod,
            status = "Shipped",
            extra = "<b>Courier:</b> $courier\n<b>Tracking ID:</b> <code>$trackingId</code>\n<b>ETA:</b> $expectedDelivery"
        )
    }

    suspend fun cancelOrder(orderId: String, reason: String) {
        val order = db.orderDao().getOrderById(orderId) ?: return
        db.orderDao().cancelOrder(orderId, reason)
        dispatchTelegramAlert(
            title = "🚫 <b>ORDER CANCELLED</b>",
            orderId = orderId,
            customerName = "Customer",
            phone = "",
            amount = order.finalTotal,
            paymentMethod = order.paymentMethod,
            status = "Cancelled",
            extra = "<b>Cancellation Reason:</b> $reason"
        )
    }

    suspend fun requestReturn(orderId: String, reason: String) {
        val order = db.orderDao().getOrderById(orderId) ?: return
        db.orderDao().requestReturn(orderId, "Return Requested", reason)
        dispatchTelegramAlert(
            title = "🔄 <b>RETURN REQUESTED</b>",
            orderId = orderId,
            customerName = "Customer",
            phone = "",
            amount = order.finalTotal,
            paymentMethod = order.paymentMethod,
            status = "Return Requested",
            extra = "<b>Return Reason:</b> $reason"
        )
    }

    suspend fun requestReplacement(orderId: String, reason: String) {
        val order = db.orderDao().getOrderById(orderId) ?: return
        db.orderDao().requestReplacement(orderId, "Replacement Requested", reason)
        dispatchTelegramAlert(
            title = "🔁 <b>REPLACEMENT REQUESTED</b>",
            orderId = orderId,
            customerName = "Customer",
            phone = "",
            amount = order.finalTotal,
            paymentMethod = order.paymentMethod,
            status = "Replacement Requested",
            extra = "<b>Replacement Reason:</b> $reason"
        )
    }

    // -------------------------------------------------------------
    // REVIEWS
    // -------------------------------------------------------------
    fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>> = db.reviewDao().getReviewsForProduct(productId)
    fun getAllReviewsForAdmin(): Flow<List<ReviewEntity>> = db.reviewDao().getAllReviewsForAdmin()

    suspend fun submitReview(
        productId: Long,
        userId: Long,
        userName: String,
        rating: Int,
        title: String,
        comment: String
    ) {
        val review = ReviewEntity(
            productId = productId,
            userId = userId,
            userName = userName,
            rating = rating,
            title = title,
            comment = comment,
            reviewDate = "Today"
        )
        db.reviewDao().insertReview(review)
    }

    // -------------------------------------------------------------
    // COUPONS
    // -------------------------------------------------------------
    fun getActiveCoupons(): Flow<List<CouponEntity>> = db.couponDao().getActiveCoupons()
    fun getAllCoupons(): Flow<List<CouponEntity>> = db.couponDao().getAllCoupons()
    suspend fun getCouponByCode(code: String): CouponEntity? = db.couponDao().getCouponByCode(code)
    suspend fun saveCoupon(coupon: CouponEntity): Long {
        return if (coupon.id == 0L) {
            db.couponDao().insertCoupon(coupon)
        } else {
            db.couponDao().updateCoupon(coupon)
            coupon.id
        }
    }
    suspend fun deleteCoupon(coupon: CouponEntity) = db.couponDao().deleteCoupon(coupon)

    // -------------------------------------------------------------
    // SETTINGS
    // -------------------------------------------------------------
    fun getAllSettings(): Flow<List<AppSettingsEntity>> = db.appSettingsDao().getAllSettings()
    suspend fun getSetting(key: String): String? = db.appSettingsDao().getSettingValue(key)
    suspend fun setSetting(key: String, value: String) = db.appSettingsDao().setSetting(AppSettingsEntity(key, value))

    // -------------------------------------------------------------
    // RECENTLY VIEWED
    // -------------------------------------------------------------
    fun getRecentlyViewed(userId: Long): Flow<List<ProductEntity>> {
        return combine(
            db.recentlyViewedDao().getRecentlyViewed(userId),
            db.productDao().getAllActiveProducts()
        ) { entries, products ->
            val productMap = products.associateBy { it.id }
            entries.mapNotNull { productMap[it.productId] }
        }
    }

    suspend fun recordRecentlyViewed(userId: Long, productId: Long) {
        db.recentlyViewedDao().recordView(
            RecentlyViewedEntity(userId = userId, productId = productId)
        )
    }

    // -------------------------------------------------------------
    // TELEGRAM HELPER
    // -------------------------------------------------------------
    private suspend fun dispatchTelegramAlert(
        title: String,
        orderId: String,
        customerName: String,
        phone: String,
        amount: Double,
        paymentMethod: String,
        status: String,
        extra: String = ""
    ) {
        val botToken = db.appSettingsDao().getSettingValue("telegram_bot_token") ?: ""
        val chatId = db.appSettingsDao().getSettingValue("telegram_chat_id") ?: ""

        val htmlMessage = buildString {
            append("$title\n")
            append("<b>ShopKart E-Commerce Alert</b>\n\n")
            append("<b>Order ID:</b> <code>$orderId</code>\n")
            if (customerName.isNotBlank()) append("<b>Customer:</b> $customerName\n")
            if (phone.isNotBlank()) append("<b>Phone:</b> $phone\n")
            append("<b>Amount:</b> ₹${"%.2f".format(amount)}\n")
            append("<b>Payment:</b> $paymentMethod\n")
            append("<b>Current Status:</b> <b>$status</b>\n")
            if (extra.isNotBlank()) {
                append("\n$extra\n")
            }
            append("\n<i>Timestamp: ${java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(java.util.Date())}</i>")
        }

        telegramService.sendNotification(botToken, chatId, htmlMessage)
    }
}
