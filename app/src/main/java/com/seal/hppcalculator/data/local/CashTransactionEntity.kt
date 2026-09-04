package com.seal.hppcalculator.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_transactions")
data class CashTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "IN" (Pemasukan) or "OUT" (Pengeluaran)
    val category: String, // "PENJUALAN", "MODAL", "BAHAN_BAKU", "OPERASIONAL", "GAJI", "LAINNYA"
    val title: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)
