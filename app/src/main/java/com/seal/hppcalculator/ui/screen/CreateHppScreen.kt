package com.seal.hppcalculator.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.seal.hppcalculator.ads.loadAndShowRewardedAd
import com.seal.hppcalculator.data.model.Ingredient
import com.seal.hppcalculator.viewmodel.HppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHppScreen(
    viewModel: HppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val draft by viewModel.currentDraft.collectAsState()
    val context = LocalContext.current
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Form HPP", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (!isSaving) {
                            isSaving = true
                            loadAndShowRewardedAd(context) {
                                viewModel.saveDraft()
                                onNavigateBack()
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Simpan Data", fontSize = MaterialTheme.typography.titleMedium.fontSize, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val itemsTitle = when (draft.category) {
            "RETAIL" -> "Daftar Barang Modal"
            "SERVICE" -> "Alat & Bahan Habis Pakai"
            else -> "Bahan Baku"
        }
        val itemsAddLabel = when (draft.category) {
            "RETAIL" -> "Tambah Barang"
            "SERVICE" -> "Tambah Alat/Bahan"
            else -> "Tambah Bahan Baku"
        }
        val showKemasan = draft.category == "FNB"

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header Section
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            when (draft.category) {
                                "RETAIL" -> "Informasi Toko / Retail"
                                "SERVICE" -> "Informasi Jasa / Layanan"
                                else -> "Informasi Produk F&B"
                            }, 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.ExtraBold, 
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = draft.productName,
                            onValueChange = { viewModel.updateDraft(draft.copy(productName = it)) },
                            label = { 
                                Text(
                                    when (draft.category) {
                                        "SERVICE" -> "Nama Layanan Jasa"
                                        else -> "Nama Produk"
                                    }, 
                                    fontWeight = FontWeight.Medium
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }
            
            // Ingredients Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(itemsTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                    TextButton(onClick = { viewModel.addIngredient() }) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(itemsAddLabel, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(draft.ingredients) { ingredient ->
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    IngredientInputItem(
                        ingredient = ingredient,
                        category = draft.category,
                        onUpdate = { updated -> viewModel.updateIngredient(ingredient.id, updated) },
                        onDelete = { viewModel.removeIngredient(ingredient.id) }
                    )
                }
            }

            if (draft.ingredients.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada data ditambahkan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            
            // Other Costs Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Biaya Operasional", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        
                        if (showKemasan) {
                            CostInputRow("Kemasan", draft.packagingCost) { viewModel.updateDraft(draft.copy(packagingCost = it)) }
                        }
                        CostInputRow("Tenaga Kerja", draft.laborCost) { viewModel.updateDraft(draft.copy(laborCost = it)) }
                        CostInputRow("Overhead / Ongkir", draft.overheadCost) { viewModel.updateDraft(draft.copy(overheadCost = it)) }
                    }
                }
            }

            // Margin Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Target Produksi & Margin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = if (draft.productionQty == 0.0) "" else draft.productionQty.toString(),
                                onValueChange = { viewModel.updateDraft(draft.copy(productionQty = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Jml Produksi") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = if (draft.marginPercent == 0.0) "" else draft.marginPercent.toString(),
                                onValueChange = { viewModel.updateDraft(draft.copy(marginPercent = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Margin (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CostInputRow(label: String, value: Double, onValueChange: (Double) -> Unit) {
    OutlinedTextField(
        value = if (value == 0.0) "" else value.toString(),
        onValueChange = { onValueChange(it.toDoubleOrNull() ?: 0.0) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Text(
                "Rp", 
                modifier = Modifier.padding(start = 16.dp), 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Composable
fun IngredientInputItem(
    ingredient: Ingredient,
    category: String,
    onUpdate: (Ingredient) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = ingredient.name,
                    onValueChange = { onUpdate(ingredient.copy(name = it)) },
                    label = { 
                        Text(
                            when (category) {
                                "RETAIL" -> "Nama Barang"
                                "SERVICE" -> "Nama Alat / Bahan"
                                else -> "Nama Bahan"
                            }
                        ) 
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = if (ingredient.buyPrice == 0.0) "" else ingredient.buyPrice.toString(),
                    onValueChange = { onUpdate(ingredient.copy(buyPrice = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Harga Beli") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
                OutlinedTextField(
                    value = if (ingredient.buyQty == 0.0) "" else ingredient.buyQty.toString(),
                    onValueChange = { onUpdate(ingredient.copy(buyQty = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Jml Beli") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = if (ingredient.usedQty == 0.0) "" else ingredient.usedQty.toString(),
                    onValueChange = { onUpdate(ingredient.copy(usedQty = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Jml Pakai") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ingredient.unit,
                    onValueChange = { onUpdate(ingredient.copy(unit = it)) },
                    label = { Text("Satuan") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
            
            if (ingredient.cost > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(
                            ingredient.cost.formatRupiah(), 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
