package com.example.data.model

import com.example.data.local.entities.ProductEntity

data class CartItemDetail(
    val cartItemId: Long,
    val product: ProductEntity,
    val quantity: Int,
    val selectedSize: String,
    val selectedColor: String,
    val isSavedForLater: Boolean
) {
    val itemTotal: Double get() = product.price * quantity
    val itemMrpTotal: Double get() = product.mrp * quantity
    val itemSavings: Double get() = (product.mrp - product.price) * quantity
}

data class OrderedItem(
    val productId: Long,
    val productName: String,
    val brand: String,
    val price: Double,
    val mrp: Double,
    val quantity: Int,
    val selectedSize: String = "",
    val selectedColor: String = "",
    val imageUrl: String = ""
)

data class OrderPricingSummary(
    val subtotal: Double,
    val deliveryCharge: Double,
    val discount: Double,
    val finalTotal: Double,
    val totalSavings: Double,
    val isFreeDelivery: Boolean
)

data class SearchFilterState(
    val query: String = "",
    val selectedCategory: String = "All",
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Double = 0.0,
    val selectedBrand: String = "All",
    val minDiscount: Int = 0,
    val inStockOnly: Boolean = false,
    val sortBy: SortOption = SortOption.RELEVANCE
)

enum class SortOption(val displayName: String) {
    RELEVANCE("Featured"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    RATING("Avg. Customer Review"),
    NEWEST("Newest Arrivals"),
    DISCOUNT("Discount: High to Low")
}
