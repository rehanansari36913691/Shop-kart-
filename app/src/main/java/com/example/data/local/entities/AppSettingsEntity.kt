package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val key: String, // e.g. "delivery_threshold", "delivery_fee", "upi_id", "upi_payee_name", "telegram_bot_token", "telegram_chat_id"
    val value: String
)
