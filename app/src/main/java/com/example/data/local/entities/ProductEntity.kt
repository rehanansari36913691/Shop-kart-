package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val category: String,
    val subcategory: String = "",
    val sku: String,
    val price: Double,
    val mrp: Double,
    val discountPercent: Int = 0,
    val stock: Int = 50,
    val rating: Double = 4.3,
    val reviewCount: Int = 120,
    val description: String,
    val imagesJson: String = "[]", // JSON array of image URLs or local drawables
    val featuresJson: String = "[]", // JSON array of feature bullets
    val specsJson: String = "{}", // JSON map of specifications
    val includedJson: String = "[]", // What's in the box
    val deliveryEstimate: String = "FREE Delivery by Tomorrow",
    val isDeal: Boolean = false,
    val dealDiscountText: String = "",
    val isBestSeller: Boolean = false,
    val isRecommended: Boolean = true,
    val sizesJson: String = "[]",
    val colorsJson: String = "[]",
    val keywords: String = "", // Search keywords e.g. "juta, joota, shoe, sneakers, black shoes"
    val relatedProductIdsJson: String = "[]",
    val frequentlyBoughtIdsJson: String = "[]",
    val isHidden: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
