package com.seal.hppcalculator.data.model

data class ProductCost(
    val id: Long = 0,
    val category: String = "FNB",
    val productName: String = "",
    val productionQty: Double = 0.0,
    val packagingCost: Double = 0.0,
    val laborCost: Double = 0.0,
    val overheadCost: Double = 0.0,
    val marginPercent: Double = 0.0,
    val ingredients: List<Ingredient> = emptyList()
) {
    val totalBahan: Double
        get() = ingredients.sumOf { it.cost }

    val totalModal: Double
        get() = totalBahan + packagingCost + laborCost + overheadCost

    val hppPerUnit: Double
        get() = if (productionQty > 0) totalModal / productionQty else 0.0

    val profitPerUnit: Double
        get() = hppPerUnit * (marginPercent / 100)

    val hargaJual: Double
        get() = hppPerUnit + profitPerUnit
}

data class Ingredient(
    val id: Long = 0,
    val name: String = "",
    val buyPrice: Double = 0.0,
    val buyQty: Double = 0.0,
    val usedQty: Double = 0.0,
    val unit: String = ""
) {
    val cost: Double
        get() = if (buyQty > 0) (buyPrice / buyQty) * usedQty else 0.0
}

fun com.seal.hppcalculator.data.local.ProductCostWithIngredients.toDomainModel(): ProductCost {
    return ProductCost(
        id = productCost.id,
        category = productCost.category,
        productName = productCost.productName,
        productionQty = productCost.productionQty,
        packagingCost = productCost.packagingCost,
        laborCost = productCost.laborCost,
        overheadCost = productCost.overheadCost,
        marginPercent = productCost.marginPercent,
        ingredients = ingredients.map { it.toDomainModel() }
    )
}

fun com.seal.hppcalculator.data.local.IngredientEntity.toDomainModel(): Ingredient {
    return Ingredient(
        id = id,
        name = name,
        buyPrice = buyPrice,
        buyQty = buyQty,
        usedQty = usedQty,
        unit = unit
    )
}

fun ProductCost.toEntity(overrideId: Long = 0): com.seal.hppcalculator.data.local.ProductCostEntity {
    return com.seal.hppcalculator.data.local.ProductCostEntity(
        id = if (overrideId != 0L) overrideId else id,
        category = category,
        productName = productName,
        productionQty = productionQty,
        packagingCost = packagingCost,
        laborCost = laborCost,
        overheadCost = overheadCost,
        marginPercent = marginPercent
    )
}

fun Ingredient.toEntity(productCostId: Long): com.seal.hppcalculator.data.local.IngredientEntity {
    return com.seal.hppcalculator.data.local.IngredientEntity(
        id = 0, // Auto-generate on insert to prevent large id conflicts
        productCostId = productCostId,
        name = name,
        buyPrice = buyPrice,
        buyQty = buyQty,
        usedQty = usedQty,
        unit = unit
    )
}
