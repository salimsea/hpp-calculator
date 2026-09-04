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

    // Cash Transactions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashTransaction(transaction: CashTransactionEntity): Long

    @Query("SELECT * FROM cash_transactions ORDER BY date DESC, id DESC")
    fun getAllCashTransactions(): Flow<List<CashTransactionEntity>>

    @Query("DELETE FROM cash_transactions WHERE id = :id")
    suspend fun deleteCashTransaction(id: Long)

    @Query("DELETE FROM ingredients")
    suspend fun deleteAllIngredients()

    @Query("DELETE FROM product_costs")
    suspend fun deleteAllProductCosts()

    @Query("DELETE FROM cash_transactions")
    suspend fun deleteAllCashTransactions()

    @Transaction
    suspend fun clearAllData() {
        deleteAllIngredients()
        deleteAllProductCosts()
        deleteAllCashTransactions()
    }
}
