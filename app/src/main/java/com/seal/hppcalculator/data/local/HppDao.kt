package com.seal.hppcalculator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductCost(productCost: ProductCostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Transaction
    @Query("SELECT * FROM product_costs ORDER BY createdAt DESC")
    fun getAllProductCosts(): Flow<List<ProductCostWithIngredients>>

    @Query("DELETE FROM product_costs WHERE id = :id")
    suspend fun deleteProductCost(id: Long)
    
    @Query("DELETE FROM ingredients WHERE productCostId = :productId")
    suspend fun deleteIngredientsForProduct(productId: Long)
}
