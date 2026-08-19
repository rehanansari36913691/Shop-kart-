package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val productId: Long,
    val quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = "",
    val isSavedForLater: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)
