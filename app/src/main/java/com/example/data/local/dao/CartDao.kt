package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND isSavedForLater = 0 ORDER BY addedAt DESC")
    fun getActiveCartItems(userId: Long): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND isSavedForLater = 1 ORDER BY addedAt DESC")
    fun getSavedForLaterItems(userId: Long): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId AND isSavedForLater = 0 LIMIT 1")
    suspend fun getCartItem(userId: Long, productId: Long): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND isSavedForLater = 0")
    suspend fun clearActiveCart(userId: Long)

    @Query("UPDATE cart_items SET isSavedForLater = :saveForLater WHERE id = :id")
    suspend fun toggleSaveForLater(id: Long, saveForLater: Boolean)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int)
}
