package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<AppSettingsEntity>>

    @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
    fun getSettingValueFlow(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSettingsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSettings(settings: List<AppSettingsEntity>)
}
