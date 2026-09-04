package com.seal.hppcalculator.data.model

import com.seal.hppcalculator.data.local.CashTransactionEntity

data class CashTransaction(
    val id: Long = 0,
    val type: String = "IN", // "IN" or "OUT"
    val category: String = "PENJUALAN",
    val title: String = "",
    val amount: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

fun CashTransactionEntity.toDomainModel(): CashTransaction {
    return CashTransaction(
        id = id,
        type = type,
        category = category,
        title = title,
        amount = amount,
        date = date,
        notes = notes
    )
}

fun CashTransaction.toEntity(): CashTransactionEntity {
    return CashTransactionEntity(
        id = id,
        type = type,
        category = category,
        title = title,
        amount = amount,
        date = date,
        notes = notes
    )
}
