package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String, // e.g. "SK-ORD-83921"
    val userId: Long,
    val itemsJson: String, // JSON array of OrderedItem
    val subtotal: Double,
    val deliveryCharge: Double,
    val discountAmount: Double = 0.0,
    val couponCode: String = "",
    val finalTotal: Double,
    val addressJson: String, // Full snapshot of AddressEntity
    val paymentMethod: String, // "UPI", "CARD", "NET_BANKING"
    val paymentStatus: String = "Payment Verification Pending", // "Payment Verification Pending", "Payment Confirmed", "Payment Rejected"
    val orderStatus: String = "Payment Verification Pending", // "Payment Verification Pending", "Payment Confirmed", "Processing", "Shipped", "Out for Delivery", "Delivered", "Cancelled", "Return Requested", "Return Approved", "Pickup Scheduled", "Returned", "Refund Processing", "Refund Completed", "Replacement Requested", "Replacement Approved", "Replacement Shipped", "Replacement Delivered"
    val upiTransactionId: String = "",
    val upiAppUsed: String = "",
    val cardLast4: String = "",
    val bankName: String = "",
    val trackingId: String = "",
    val courierName: String = "",
    val shippingDate: String = "",
    val expectedDeliveryDate: String = "",
    val deliveryDate: String = "",
    val cancelReason: String = "",
    val returnReason: String = "",
    val replacementReason: String = "",
    val refundAmount: Double = 0.0,
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
