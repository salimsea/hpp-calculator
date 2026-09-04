package com.seal.hppcalculator.data.repository

import com.seal.hppcalculator.data.local.HppDao
import com.seal.hppcalculator.data.model.ProductCost
import com.seal.hppcalculator.data.model.toDomainModel
import com.seal.hppcalculator.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HppRepository(private val hppDao: HppDao) {
    val allProductCosts: Flow<List<ProductCost>> = hppDao.getAllProductCosts().map { list ->
        list.map { it.toDomainModel() }
    }

    suspend fun saveProductCost(product: ProductCost) {
        val isUpdate = product.id != 0L
        val insertedId = hppDao.insertProductCost(product.toEntity())
        val targetProductId = if (isUpdate) product.id else insertedId
        
        if (isUpdate) {
            // Delete old ingredients before adding new ones
            hppDao.deleteIngredientsForProduct(targetProductId)
        }
        
        val ingredientEntities = product.ingredients.map { it.toEntity(targetProductId) }
        hppDao.insertIngredients(ingredientEntities)
    }

    suspend fun deleteProduct(id: Long) {
        hppDao.deleteProductCost(id)
    }

    // Cashflow Repository
    val allCashTransactions: Flow<List<com.seal.hppcalculator.data.model.CashTransaction>> = 
        hppDao.getAllCashTransactions().map { list -> list.map { it.toDomainModel() } }

    suspend fun saveCashTransaction(transaction: com.seal.hppcalculator.data.model.CashTransaction): Long {
        return hppDao.insertCashTransaction(transaction.toEntity())
    }

    suspend fun deleteCashTransaction(id: Long) {
        hppDao.deleteCashTransaction(id)
    }

    suspend fun clearAllData() {
        hppDao.clearAllData()
    }
}
