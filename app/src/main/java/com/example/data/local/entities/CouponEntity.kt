package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // e.g. "SHOPKART10", "WELCOME50", "SUPERDEAL"
    val description: String = "",
    val discountType: String = "PERCENTAGE", // "PERCENTAGE" or "FIXED"
    val discountValue: Double = 10.0, // 10% or ₹100
    val minOrderAmount: Double = 0.0,
    val maxDiscountAmount: Double = 500.0,
    val expiryDate: String = "31 Dec 2026",
    val applicableCategory: String = "ALL", // "ALL" or specific category
    val usageLimit: Int = 1000,
    val timesUsed: Int = 0,
    val isActive: Boolean = true
)
