package com.example.data.util

import java.security.MessageDigest

object SecurityUtils {
    private const val SALT = "ShopKartSecureSalt2026!#"

    fun hashPassword(password: String): String {
        val input = "$SALT:$password"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        val computedHash = hashPassword(password)
        return computedHash.equals(storedHash, ignoreCase = true)
    }

    fun generateOrderId(): String {
        val timestamp = (System.currentTimeMillis() % 100000).toString().padStart(5, '0')
        val randomDigits = (1000..9999).random()
        return "SK-ORD-$timestamp-$randomDigits"
    }

    fun generateTrackingId(): String {
        val randomNum = (10000000..99999999).random()
        return "TRK$randomNum-IN"
    }
}
