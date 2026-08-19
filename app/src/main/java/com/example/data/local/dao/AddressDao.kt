package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC, id DESC")
    fun getAddressesForUser(userId: Long): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE id = :id LIMIT 1")
    suspend fun getAddressById(id: Long): AddressEntity?

    @Query("SELECT * FROM addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    suspend fun getDefaultAddress(userId: Long): AddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity): Long

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultFlags(userId: Long)
}
