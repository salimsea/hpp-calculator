package com.seal.hppcalculator.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "product_costs")
data class ProductCostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String = "FNB",
    val productName: String,
    val productionQty: Double,
    val packagingCost: Double,
    val laborCost: Double,
    val overheadCost: Double,
    val marginPercent: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "ingredients",
    foreignKeys = [
        ForeignKey(
            entity = ProductCostEntity::class,
            parentColumns = ["id"],
            childColumns = ["productCostId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productCostId: Long,
    val name: String,
    val buyPrice: Double,
    val buyQty: Double,
    val usedQty: Double,
    val unit: String
)

data class ProductCostWithIngredients(
    @Embedded val productCost: ProductCostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "productCostId"
    )
    val ingredients: List<IngredientEntity>
)
