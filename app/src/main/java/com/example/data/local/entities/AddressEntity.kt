package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val fullName: String,
    val mobile: String,
    val email: String = "",
    val altPhone: String = "",
    val house: String,
    val area: String,
    val landmark: String = "",
    val pincode: String,
    val city: String,
    val state: String,
    val addressType: String = "Home", // "Home" or "Work"
    val deliveryInstructions: String = "",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
