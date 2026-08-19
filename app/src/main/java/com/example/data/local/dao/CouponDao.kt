package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.CouponEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons WHERE isActive = 1")
    fun getActiveCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons ORDER BY id DESC")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons WHERE UPPER(code) = UPPER(:code) AND isActive = 1 LIMIT 1")
    suspend fun getCouponByCode(code: String): CouponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity): Long

    @Update
    suspend fun updateCoupon(coupon: CouponEntity)

    @Delete
    suspend fun deleteCoupon(coupon: CouponEntity)

    @Query("UPDATE coupons SET timesUsed = timesUsed + 1 WHERE id = :id")
    suspend fun incrementUsage(id: Long)
}
