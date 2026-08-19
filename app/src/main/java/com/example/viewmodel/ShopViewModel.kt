package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.AddressEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ReviewEntity
import com.example.data.local.entities.UserEntity
import com.example.data.model.CartItemDetail
import com.example.data.model.OrderPricingSummary
import com.example.data.model.SearchFilterState
import com.example.data.model.SortOption
import com.example.data.repository.ShopRepository
import com.example.data.util.TelegramService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminMetrics(
    val totalOrders: Int = 0,
    val pendingPayments: Int = 0,
    val processingOrders: Int = 0,
    val shippedOrders: Int = 0,
    val deliveredOrders: Int = 0,
    val returnRequests: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalProducts: Int = 0,
    val totalCustomers: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModel(
    private val repository: ShopRepository
) : ViewModel() {

    // Current User Session
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Global Notification / Toast message event
    private val _snackMessage = MutableSharedFlow<String>()
    val snackMessage: SharedFlow<String> = _snackMessage.asSharedFlow()
    val snackbarMessage: SharedFlow<String> = _snackMessage.asSharedFlow()

    init {
        // Auto-login default demo customer for immediate ready-to-shop experience
        viewModelScope.launch {
            val defaultUser = repository.getUserById(2) // Rehan Ansari (Customer)
            if (defaultUser != null) {
                _currentUser.value = defaultUser
            }
        }
    }

    // -------------------------------------------------------------
    // PRODUCTS & DISCOVERY
    // -------------------------------------------------------------
    val allActiveProducts: StateFlow<List<ProductEntity>> = repository.getAllActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deals: StateFlow<List<ProductEntity>> = repository.getDeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestSellers: StateFlow<List<ProductEntity>> = repository.getBestSellers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filter
    private val _searchFilterState = MutableStateFlow(SearchFilterState())
    val searchFilterState: StateFlow<SearchFilterState> = _searchFilterState.asStateFlow()

    val searchResults: StateFlow<List<ProductEntity>> = _searchFilterState
        .flatMapLatest { filter -> repository.searchProducts(filter) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchFilterState.value = _searchFilterState.value.copy(query = query)
    }

    fun updateSearchFilter(filter: SearchFilterState) {
        _searchFilterState.value = filter
    }

    fun setSortOption(sort: SortOption) {
        _searchFilterState.value = _searchFilterState.value.copy(sortBy = sort)
    }

    fun setCategoryFilter(category: String) {
        _searchFilterState.value = _searchFilterState.value.copy(selectedCategory = category)
    }

    fun clearSearchFilter() {
        _searchFilterState.value = SearchFilterState()
    }

    fun getProductByIdFlow(id: Long): Flow<ProductEntity?> = repository.getProductByIdFlow(id)
    suspend fun getProductById(id: Long): ProductEntity? = repository.getProductById(id)
    suspend fun getProductsByIds(ids: List<Long>): List<ProductEntity> = repository.getProductsByIds(ids)

    // -------------------------------------------------------------
    // CART & WISHLIST
    // -------------------------------------------------------------
    val activeCartItems: StateFlow<List<CartItemDetail>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getCartDetails(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount: StateFlow<Int> = activeCartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val savedForLaterItems: StateFlow<List<CartItemDetail>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getSavedForLaterDetails(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistProducts: StateFlow<List<ProductEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getWishlistProducts(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyViewedProducts: StateFlow<List<ProductEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getRecentlyViewed(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addToCart(
        productId: Long,
        quantity: Int = 1,
        selectedSize: String = "",
        selectedColor: String = ""
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addToCart(user.id, productId, quantity, selectedSize, selectedColor)
            _snackMessage.emit("Added to Cart successfully")
        }
    }

    fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun toggleSaveForLater(cartItemId: Long, saveForLater: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveForLater(cartItemId, saveForLater)
            _snackMessage.emit(if (saveForLater) "Saved for later" else "Moved back to Cart")
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
            _snackMessage.emit("Item removed from Cart")
        }
    }

    fun toggleWishlist(productId: Long) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleWishlist(user.id, productId)
        }
    }

    fun isProductInWishlist(productId: Long): Flow<Boolean> {
        val user = _currentUser.value ?: return flowOf(false)
        return repository.isProductInWishlist(user.id, productId)
    }

    fun recordRecentlyViewed(productId: Long) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.recordRecentlyViewed(user.id, productId)
        }
    }

    // -------------------------------------------------------------
    // ADDRESSES
    // -------------------------------------------------------------
    val savedAddresses: StateFlow<List<AddressEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getAddressesForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveAddress(address: AddressEntity, onComplete: (Long) -> Unit = {}) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val id = repository.saveAddress(address.copy(userId = user.id))
            _snackMessage.emit("Address saved successfully")
            onComplete(id)
        }
    }

    fun deleteAddress(address: AddressEntity) {
        viewModelScope.launch {
            repository.deleteAddress(address)
            _snackMessage.emit("Address deleted")
        }
    }

    // -------------------------------------------------------------
    // COUPONS & SETTINGS
    // -------------------------------------------------------------
    val activeCoupons: StateFlow<List<CouponEntity>> = repository.getActiveCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCouponsForAdmin: StateFlow<List<CouponEntity>> = repository.getAllCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getCouponByCode(code: String): CouponEntity? = repository.getCouponByCode(code)

    val appSettings: StateFlow<Map<String, String>> = repository.getAllSettings().flatMapLatest { list ->
        flowOf(list.associate { it.key to it.value })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    suspend fun calculatePricing(
        items: List<CartItemDetail>,
        coupon: CouponEntity?
    ): OrderPricingSummary = repository.calculatePricing(items, coupon)

    // -------------------------------------------------------------
    // ORDERS & CHECKOUT
    // -------------------------------------------------------------
    val userOrders: StateFlow<List<OrderEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getOrdersForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?> = repository.getOrderByIdFlow(orderId)

    fun placeOrder(
        items: List<CartItemDetail>,
        address: AddressEntity,
        paymentMethod: String,
        upiTransactionId: String = "",
        upiAppUsed: String = "",
        cardLast4: String = "",
        bankName: String = "",
        appliedCoupon: CouponEntity? = null,
        onSuccess: (OrderEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = _currentUser.value
        if (user == null) {
            onError("Please log in to place your order.")
            return
        }

        viewModelScope.launch {
            val result = repository.createOrder(
                userId = user.id,
                items = items,
                address = address,
                paymentMethod = paymentMethod,
                upiTransactionId = upiTransactionId,
                upiAppUsed = upiAppUsed,
                cardLast4 = cardLast4,
                bankName = bankName,
                appliedCoupon = appliedCoupon
            )

            result.onSuccess { order ->
                _snackMessage.emit("Order placed successfully! ID: ${order.id}")
                onSuccess(order)
            }.onFailure { err ->
                onError(err.message ?: "Failed to place order.")
            }
        }
    }

    fun cancelOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            repository.cancelOrder(orderId, reason)
            _snackMessage.emit("Order cancellation requested.")
        }
    }

    fun requestReturn(orderId: String, reason: String) {
        viewModelScope.launch {
            repository.requestReturn(orderId, reason)
            _snackMessage.emit("Return request submitted.")
        }
    }

    fun requestReplacement(orderId: String, reason: String) {
        viewModelScope.launch {
            repository.requestReplacement(orderId, reason)
            _snackMessage.emit("Replacement request submitted.")
        }
    }

    // -------------------------------------------------------------
    // REVIEWS
    // -------------------------------------------------------------
    fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>> = repository.getReviewsForProduct(productId)

    fun submitReview(productId: Long, rating: Int, title: String, comment: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.submitReview(
                productId = productId,
                userId = user.id,
                userName = user.fullName,
                rating = rating,
                title = title,
                comment = comment
            )
            _snackMessage.emit("Review submitted! Thank you.")
        }
    }

    // -------------------------------------------------------------
    // AUTHENTICATION
    // -------------------------------------------------------------
    fun login(identifier: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val res = repository.authenticateUser(identifier, pass)
            res.onSuccess { user ->
                _currentUser.value = user
                _snackMessage.emit("Welcome back, ${user.fullName}!")
                onSuccess()
            }.onFailure { e ->
                onError(e.message ?: "Login failed")
            }
        }
    }

    fun signup(name: String, email: String, phone: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val res = repository.registerUser(name, email, phone, pass)
            res.onSuccess { user ->
                _currentUser.value = user
                _snackMessage.emit("Account created! Welcome to ShopKart.")
                onSuccess()
            }.onFailure { e ->
                onError(e.message ?: "Signup failed")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        viewModelScope.launch {
            _snackMessage.emit("Logged out successfully")
        }
    }

    fun switchUserToAdmin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val admin = repository.getUserById(1) // Admin
            if (admin != null) {
                _currentUser.value = admin
                onSuccess()
            }
        }
    }

    // -------------------------------------------------------------
    // ADMIN DASHBOARD & MUTATIONS
    // -------------------------------------------------------------
    val allOrdersForAdmin: StateFlow<List<OrderEntity>> = repository.getAllOrdersForAdmin()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminMetrics: StateFlow<AdminMetrics> = combine(
        repository.getAllOrdersForAdmin(),
        repository.getProductCount(),
        repository.getCustomerCount()
    ) { orders, prodCount, custCount ->
        val pending = orders.count { it.paymentStatus == "Payment Verification Pending" }
        val processing = orders.count { it.orderStatus == "Processing" }
        val shipped = orders.count { it.orderStatus in listOf("Shipped", "Out for Delivery") }
        val delivered = orders.count { it.orderStatus == "Delivered" }
        val returns = orders.count { it.orderStatus.startsWith("Return") || it.orderStatus.startsWith("Replacement") || it.orderStatus.startsWith("Refund") }
        val revenue = orders.filter { it.paymentStatus == "Payment Confirmed" }.sumOf { it.finalTotal }

        AdminMetrics(
            totalOrders = orders.size,
            pendingPayments = pending,
            processingOrders = processing,
            shippedOrders = shipped,
            deliveredOrders = delivered,
            returnRequests = returns,
            totalRevenue = revenue,
            totalProducts = prodCount,
            totalCustomers = custCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminMetrics())

    fun confirmPayment(orderId: String) {
        viewModelScope.launch {
            repository.confirmPayment(orderId)
            _snackMessage.emit("Payment confirmed for order $orderId")
        }
    }

    fun rejectPayment(orderId: String, reason: String = "UTR / Ref ID not found in bank statement") {
        viewModelScope.launch {
            repository.rejectPayment(orderId, reason)
            _snackMessage.emit("Payment rejected for order $orderId")
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            _snackMessage.emit("Order status updated to $newStatus")
        }
    }

    fun updateTrackingInfo(orderId: String, trackingId: String, courier: String, shippingDate: String, expectedDelivery: String) {
        viewModelScope.launch {
            repository.updateTrackingInfo(orderId, trackingId, courier, shippingDate, expectedDelivery)
            _snackMessage.emit("Tracking details updated for $orderId")
        }
    }

    fun saveProduct(product: ProductEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveProduct(product)
            _snackMessage.emit("Product saved successfully")
            onComplete()
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            _snackMessage.emit("Product deleted")
        }
    }

    fun setProductHidden(productId: Long, hidden: Boolean) {
        viewModelScope.launch {
            repository.setProductHidden(productId, hidden)
            _snackMessage.emit(if (hidden) "Product hidden from store" else "Product made visible")
        }
    }

    fun saveCoupon(coupon: CouponEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveCoupon(coupon)
            _snackMessage.emit("Coupon saved successfully")
            onComplete()
        }
    }

    fun deleteCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.deleteCoupon(coupon)
            _snackMessage.emit("Coupon deleted")
        }
    }

    fun updateSettings(settingsMap: Map<String, String>, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            settingsMap.forEach { (k, v) ->
                repository.setSetting(k, v)
            }
            _snackMessage.emit("Settings updated successfully")
            onComplete()
        }
    }

    fun testTelegramNotification(botToken: String, chatId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val telegramService = TelegramService()
            val msg = "<b>[ShopKart Test Alert]</b>\nTelegram notifications successfully connected and operational!"
            val success = telegramService.sendNotification(botToken, chatId, msg)
            if (success) {
                onResult(true, "Test alert sent to Telegram successfully!")
            } else {
                onResult(false, "Failed to send message. Please verify Bot Token & Chat ID.")
            }
        }
    }
}

class ShopViewModelFactory(
    private val context: Context,
    private val repository: ShopRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            return ShopViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
