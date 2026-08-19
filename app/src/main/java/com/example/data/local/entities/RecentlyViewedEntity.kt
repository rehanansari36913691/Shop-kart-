package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val productId: Long,
    val viewedAt: Long = System.currentTimeMillis()
)
