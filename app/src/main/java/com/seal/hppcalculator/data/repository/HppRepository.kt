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
        // Since product ID might be 0 for new, insertProductCost returns the new ID.
        // Or if it's an update, it will replace.
        val isUpdate = product.id != 0L
        val productId = hppDao.insertProductCost(product.toEntity())
        
        if (isUpdate) {
            // Delete old ingredients before adding new ones
            hppDao.deleteIngredientsForProduct(productId)
        }
        
        val ingredientEntities = product.ingredients.map { it.toEntity(productId) }
        hppDao.insertIngredients(ingredientEntities)
    }

    suspend fun deleteProduct(id: Long) {
        hppDao.deleteProductCost(id)
    }
}
