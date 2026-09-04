package com.seal.hppcalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.seal.hppcalculator.data.model.Ingredient
import com.seal.hppcalculator.data.model.ProductCost
import com.seal.hppcalculator.data.repository.HppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HppViewModel(private val repository: HppRepository) : ViewModel() {

    val history: StateFlow<List<ProductCost>> = repository.allProductCosts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentDraft = MutableStateFlow(ProductCost())
    val currentDraft: StateFlow<ProductCost> = _currentDraft.asStateFlow()

    fun updateDraft(draft: ProductCost) {
        _currentDraft.value = draft
    }

    fun addIngredient() {
        _currentDraft.update {
            it.copy(ingredients = it.ingredients + Ingredient(id = System.currentTimeMillis()))
        }
    }

    fun removeIngredient(id: Long) {
        _currentDraft.update { draft ->
            draft.copy(ingredients = draft.ingredients.filter { it.id != id })
        }
    }

    fun updateIngredient(id: Long, updatedIngredient: Ingredient) {
        _currentDraft.update { draft ->
            val newIngredients = draft.ingredients.map {
                if (it.id == id) updatedIngredient else it
            }
            draft.copy(ingredients = newIngredients)
        }
    }

    fun saveDraft() {
        viewModelScope.launch {
            val finalDraft = _currentDraft.value
            repository.saveProductCost(finalDraft)
            _currentDraft.value = ProductCost() // Reset draft
        }
    }
    
    fun updateMarginAndSave(newMargin: Double) {
        viewModelScope.launch {
            val updatedDraft = _currentDraft.value.copy(marginPercent = newMargin)
            _currentDraft.value = updatedDraft
            repository.saveProductCost(updatedDraft)
        }
    }
    
    fun setDraftForEdit(product: ProductCost) {
        _currentDraft.value = product
    }
    
    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    // Product Clone & Sample Recipe Methods
    fun cloneProduct(source: ProductCost) {
        val clonedIngredients = source.ingredients.mapIndexed { index, ing ->
            ing.copy(id = System.currentTimeMillis() + index)
        }
        _currentDraft.value = source.copy(
            id = 0L,
            productName = "${source.productName} (Salinan)",
            ingredients = clonedIngredients
        )
    }

    fun applySampleRecipe(sample: com.seal.hppcalculator.data.model.SampleRecipe) {
        val product = sample.toProductCost().copy(
            ingredients = sample.ingredients.mapIndexed { index, ing ->
                ing.copy(id = System.currentTimeMillis() + index)
            }
        )
        _currentDraft.value = product
    }

    fun saveSampleRecipeToHistory(sample: com.seal.hppcalculator.data.model.SampleRecipe, onFinished: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val product = sample.toProductCost().copy(
                ingredients = sample.ingredients.mapIndexed { index, ing ->
                    ing.copy(id = System.currentTimeMillis() + index)
                }
            )
            repository.saveProductCost(product)
            onFinished(product.id)
        }
    }

    // Cashflow Methods
    val cashTransactions: StateFlow<List<com.seal.hppcalculator.data.model.CashTransaction>> = repository.allCashTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveCashTransaction(transaction: com.seal.hppcalculator.data.model.CashTransaction) {
        viewModelScope.launch {
            repository.saveCashTransaction(transaction)
        }
    }

    fun deleteCashTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteCashTransaction(id)
        }
    }

    fun clearAllData(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.clearAllData()
            onSuccess()
        }
    }
}

class HppViewModelFactory(private val repository: HppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
