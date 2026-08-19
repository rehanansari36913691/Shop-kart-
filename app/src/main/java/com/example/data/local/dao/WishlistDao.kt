package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getWishlistItems(userId: Long): Flow<List<WishlistItemEntity>>

    @Query("SELECT * FROM wishlist_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getWishlistItem(userId: Long, productId: Long): WishlistItemEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE userId = :userId AND productId = :productId)")
    fun isProductInWishlist(userId: Long, productId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistItemEntity): Long

    @Delete
    suspend fun deleteWishlistItem(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE userId = :userId AND productId = :productId")
    suspend fun deleteByProduct(userId: Long, productId: Long)
}
