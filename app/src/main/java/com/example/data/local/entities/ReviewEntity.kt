package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val userId: Long,
    val userName: String,
    val rating: Int, // 1 to 5
    val title: String,
    val comment: String,
    val isVerifiedPurchase: Boolean = true,
    val isApproved: Boolean = true,
    val reviewDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
