package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersForUser(userId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderByIdFlow(id: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE paymentStatus = 'Payment Verification Pending' ORDER BY createdAt DESC")
    fun getPendingPaymentOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderStatus = 'Processing' ORDER BY createdAt DESC")
    fun getProcessingOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderStatus IN ('Shipped', 'Out for Delivery') ORDER BY createdAt DESC")
    fun getShippedOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderStatus = 'Delivered' ORDER BY createdAt DESC")
    fun getDeliveredOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderStatus LIKE 'Return%' OR orderStatus LIKE 'Replacement%' OR orderStatus LIKE 'Refund%' ORDER BY createdAt DESC")
    fun getReturnAndRefundOrders(): Flow<List<OrderEntity>>

    @Query("SELECT COUNT(*) FROM orders")
    fun getTotalOrderCount(): Flow<Int>

    @Query("SELECT SUM(finalTotal) FROM orders WHERE paymentStatus = 'Payment Confirmed'")
    fun getTotalRevenue(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET paymentStatus = :paymentStatus, orderStatus = :orderStatus, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updatePaymentAndStatus(orderId: String, paymentStatus: String, orderStatus: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET orderStatus = :orderStatus, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, orderStatus: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET trackingId = :trackingId, courierName = :courier, shippingDate = :shippingDate, expectedDeliveryDate = :expectedDelivery, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updateTracking(orderId: String, trackingId: String, courier: String, shippingDate: String, expectedDelivery: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET orderStatus = 'Cancelled', cancelReason = :reason, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun cancelOrder(orderId: String, reason: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET orderStatus = :status, returnReason = :reason, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun requestReturn(orderId: String, status: String, reason: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET orderStatus = :status, replacementReason = :reason, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun requestReplacement(orderId: String, status: String, reason: String, updatedAt: Long = System.currentTimeMillis())
}
