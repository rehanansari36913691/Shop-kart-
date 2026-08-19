package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AddressDao
import com.example.data.local.dao.AppSettingsDao
import com.example.data.local.dao.CartDao
import com.example.data.local.dao.CouponDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.RecentlyViewedDao
import com.example.data.local.dao.ReviewDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WishlistDao
import com.example.data.local.entities.AddressEntity
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.CartItemEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RecentlyViewedEntity
import com.example.data.local.entities.ReviewEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.WishlistItemEntity

@Database(
    entities = [
        UserEntity::class,
        AddressEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        ReviewEntity::class,
        CouponEntity::class,
        AppSettingsEntity::class,
        RecentlyViewedEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun addressDao(): AddressDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun couponDao(): CouponDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shopkart_ecommerce.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
