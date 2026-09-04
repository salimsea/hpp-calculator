package com.seal.hppcalculator.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.seal.hppcalculator.data.repository.DocumentExportHelper
import com.seal.hppcalculator.ui.components.*
import com.seal.hppcalculator.viewmodel.HppViewModel

/**
 * CashflowScreen (Buku Kas UMKM) - Refactored into Clean, Modular Architecture.
 * All atomic UI parts are modularized inside [com.seal.hppcalculator.ui.components.CashflowComponents].
 */
@Composable
fun CashflowScreen(
    viewModel: HppViewModel,
    onNavigateToCreateHpp: () -> Unit
) {
    val transactions by viewModel.cashTransactions.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "IN", "OUT"
    var showAddSheet by remember { mutableStateOf(false) }
    var newTxType by remember { mutableStateOf("IN") } // "IN" or "OUT"

    val totalIncome = transactions.filter { it.type == "IN" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "OUT" }.sumOf { it.amount }
    val currentBalance = totalIncome - totalExpense

    val filteredList = when (selectedFilter) {
        "IN" -> transactions.filter { it.type == "IN" }
        "OUT" -> transactions.filter { it.type == "OUT" }
        else -> transactions
    }

    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("hpp_prefs", android.content.Context.MODE_PRIVATE) }
    val savedBusiness = remember { sharedPrefs.getString("businessName", "") ?: "" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP HEADER & EXCEL EXPORT BUTTON
        item {
            CashflowHeader(
                savedBusiness = savedBusiness,
                hasTransactions = transactions.isNotEmpty(),
                onExportCsv = {
                    val csvFile = DocumentExportHelper.exportCashflowToCsv(context, transactions)
                    if (csvFile != null) {
                        DocumentExportHelper.shareExportedFile(
                            context = context,
                            file = csvFile,
                            mimeType = "text/csv",
                            chooserTitle = "Bagikan Rekap Buku Kas Excel"
                        )
                    }
                }
            )
        }

        // 2. SALDO KAS BERJALAN COMPACT HERO CARD
        item {
            CashflowBalanceHeroCard(currentBalance = currentBalance)
        }

        // 3. MINI BENTO CARDS: TOTAL MASUK & TOTAL KELUAR
        item {
            CashflowIncomeExpenseCards(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                onRecordIncome = {
                    newTxType = "IN"
                    showAddSheet = true
                },
                onRecordExpense = {
                    newTxType = "OUT"
                    showAddSheet = true
                }
            )
        }

        // 4. FILTER TABS (SEMUA, MASUK, KELUAR)
        item {
            CashflowFilterPills(
                selectedFilter = selectedFilter,
                onSelectFilter = { selectedFilter = it }
            )
        }

        // 5. TRANSACTION LIST / EMPTY STATE
        if (filteredList.isEmpty()) {
            item {
                CashflowEmptyState(
                    onAddTransaction = {
                        newTxType = "IN"
                        showAddSheet = true
                    }
                )
            }
        } else {
            items(filteredList, key = { it.id }) { tx ->
                CashTransactionItem(
                    transaction = tx,
                    onDelete = { viewModel.deleteCashTransaction(tx.id) }
                )
            }
        }
    }

    // 6. MODAL BOTTOM SHEET INPUT TRANSAKSI
    if (showAddSheet) {
        AddCashTransactionSheet(
            initialType = newTxType,
            onDismiss = { showAddSheet = false },
            onSave = { transaction ->
                viewModel.saveCashTransaction(transaction)
                showAddSheet = false
            }
        )
    }
}
