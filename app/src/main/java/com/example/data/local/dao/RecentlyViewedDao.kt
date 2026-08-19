package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.RecentlyViewedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyViewedDao {
    @Query("SELECT * FROM recently_viewed WHERE userId = :userId ORDER BY viewedAt DESC LIMIT 20")
    fun getRecentlyViewed(userId: Long): Flow<List<RecentlyViewedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordView(entry: RecentlyViewedEntity)

    @Query("DELETE FROM recently_viewed WHERE userId = :userId AND productId = :productId")
    suspend fun deleteEntry(userId: Long, productId: Long)
}
