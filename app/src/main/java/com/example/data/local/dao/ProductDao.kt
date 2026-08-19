package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isHidden = 0 ORDER BY id ASC")
    fun getAllActiveProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY id ASC")
    fun getAllProductsForAdmin(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductByIdFlow(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id IN (:ids) AND isHidden = 0")
    suspend fun getProductsByIds(ids: List<Long>): List<ProductEntity>

    @Query("SELECT * FROM products WHERE isDeal = 1 AND isHidden = 0 ORDER BY discountPercent DESC")
    fun getDeals(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isBestSeller = 1 AND isHidden = 0 ORDER BY rating DESC")
    fun getBestSellers(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category AND isHidden = 0 ORDER BY rating DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT DISTINCT category FROM products WHERE isHidden = 0")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM products")
    fun getProductCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET isHidden = :hide WHERE id = :id")
    suspend fun setProductHidden(id: Long, hide: Boolean)

    @Query("UPDATE products SET stock = :newStock WHERE id = :id")
    suspend fun updateStock(id: Long, newStock: Int)
}
