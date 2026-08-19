package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productId = :productId AND isApproved = 1 ORDER BY createdAt DESC")
    fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY createdAt DESC")
    fun getReviewsForUser(userId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviewsForAdmin(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Update
    suspend fun updateReview(review: ReviewEntity)

    @Query("UPDATE reviews SET isApproved = :approved WHERE id = :id")
    suspend fun setApproval(id: Long, approved: Boolean)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReview(id: Long)
}
