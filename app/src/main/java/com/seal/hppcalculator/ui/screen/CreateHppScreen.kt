package com.seal.hppcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import com.seal.hppcalculator.R
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seal.hppcalculator.data.model.Ingredient
import com.seal.hppcalculator.ui.components.RupiahInputField
import com.seal.hppcalculator.ui.theme.*
import com.seal.hppcalculator.viewmodel.HppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHppScreen(
    viewModel: HppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val draft by viewModel.currentDraft.collectAsState()
    var isSaving by remember { mutableStateOf(false) }

    val (catLabel, catBg, catText) = when (draft.category) {
        "RETAIL" -> Triple("Retail / Toko", TagBlueBg, TagBlueText)
        "SERVICE" -> Triple("Jasa / Layanan", TagPurpleBg, TagPurpleText)
        else -> Triple("Kuliner / F&B", TagOrangeBg, TagOrangeText)
    }

    val itemsTitle = when (draft.category) {
        "RETAIL" -> "Daftar Barang Modal"
        "SERVICE" -> "Alat & Bahan Habis Pakai"
        else -> "Rincian Bahan Baku"
    }

    val itemsAddLabel = when (draft.category) {
        "RETAIL" -> "Tambah Barang"
        "SERVICE" -> "Tambah Bahan"
        else -> "Tambah Bahan"
    }

    val showKemasan = draft.category == "FNB"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Clean Minimalist TopBar with soft border
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onNavigateBack)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (draft.id != 0L) "Edit Resep HPP" else "Formulir HPP",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (draft.id != 0L) "Perbarui rincian modal & takaran" else "Isi komponen modal usaha",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Category Pill Tag
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = catBg
                    ) {
                        Text(
                            text = catLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = catText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Elegant floating bottom bar with soft border
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estimasi HPP/Unit",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = draft.hppPerUnit.formatRupiah(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    val context = LocalContext.current
                    Button(
                        onClick = {
                            if (!isSaving) {
                                isSaving = true
                                com.seal.hppcalculator.util.AppRatingManager.incrementActionAndCheckShouldPrompt(context)
                                viewModel.saveDraft()
                                onNavigateBack()
                            }
                        },
                        enabled = !isSaving,
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(50.dp)
                            .padding(start = 16.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = if (draft.id != 0L) "Perbarui Resep" else "Simpan Data",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. INFORMASI DASAR PRODUK (Card 1)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, CardBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "1",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = when (draft.category) {
                                    "SERVICE" -> "Nama Layanan Jasa"
                                    "RETAIL" -> "Nama Barang Retail"
                                    else -> "Nama Menu / Produk"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ModernInputField(
                            value = draft.productName,
                            onValueChange = { viewModel.updateDraft(draft.copy(productName = it)) },
                            label = "Nama Produk / Menu",
                            placeholder = when (draft.category) {
                                "SERVICE" -> "Contoh: Cuci Sepatu Deep Clean"
                                "RETAIL" -> "Contoh: Kemeja Polos Pria L"
                                else -> "Contoh: Kopi Susu Gula Aren 250ml"
                            }
                        )
                    }
                }
            }

            // 2. DAFTAR BAHAN BAKU / BARANG MODAL (Card 2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "2",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = itemsTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Button Tambah Bahan
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.addIngredient() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = itemsAddLabel,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            if (draft.ingredients.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, CardBorderSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.il_empty_state),
                                contentDescription = "Belum ada bahan",
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .height(130.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Bahan Ditambahkan",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Klik tombol di atas untuk menambah rincian bahan usahamu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(draft.ingredients, key = { _, item -> item.id }) { index, ingredient ->
                    IngredientInputItem(
                        index = index,
                        ingredient = ingredient,
                        category = draft.category,
                        onUpdate = { updated -> viewModel.updateIngredient(ingredient.id, updated) },
                        onDelete = { viewModel.removeIngredient(ingredient.id) }
                    )
                }
            }

            // 3. BIAYA OPERASIONAL & KEMASAN (Card 3)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, CardBorderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "3",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Biaya Operasional & Kemasan",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (showKemasan) {
                            RupiahInputField(
                                value = if (draft.packagingCost == 0.0) "" else if (draft.packagingCost % 1.0 == 0.0) draft.packagingCost.toLong().toString() else draft.packagingCost.toString(),
                                onValueChange = { viewModel.updateDraft(draft.copy(packagingCost = it.toDoubleOrNull() ?: 0.0)) },
                                label = "Biaya Kemasan / Packaging",
                                placeholder = "0",
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        RupiahInputField(
                            value = if (draft.laborCost == 0.0) "" else if (draft.laborCost % 1.0 == 0.0) draft.laborCost.toLong().toString() else draft.laborCost.toString(),
                            onValueChange = { viewModel.updateDraft(draft.copy(laborCost = it.toDoubleOrNull() ?: 0.0)) },
                            label = "Upah Tenaga Kerja",
                            placeholder = "0",
                            shape = RoundedCornerShape(16.dp)
                        )

                        RupiahInputField(
                            value = if (draft.overheadCost == 0.0) "" else if (draft.overheadCost % 1.0 == 0.0) draft.overheadCost.toLong().toString() else draft.overheadCost.toString(),
                            onValueChange = { viewModel.updateDraft(draft.copy(overheadCost = it.toDoubleOrNull() ?: 0.0)) },
                            label = "Overhead / Gas / Listrik / Ongkir",
                            placeholder = "0",
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // 4. TARGET PRODUKSI & MARGIN KEUNTUNGAN (Card 4)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, CardBorderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "4",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Target & Margin Keuntungan",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ModernInputField(
                                value = if (draft.productionQty == 0.0) "" else draft.productionQty.toString(),
                                onValueChange = { viewModel.updateDraft(draft.copy(productionQty = it.toDoubleOrNull() ?: 0.0)) },
                                label = "Target Batch",
                                placeholder = "misal: 10",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )

                            ModernInputField(
                                value = if (draft.marginPercent == 0.0) "" else draft.marginPercent.toString(),
                                onValueChange = { viewModel.updateDraft(draft.copy(marginPercent = it.toDoubleOrNull() ?: 0.0)) },
                                label = "Margin (%)",
                                placeholder = "misal: 30",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Live summary card
                        if (draft.totalModal > 0) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Total Modal Produksi",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = draft.totalModal.formatRupiah(),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Rekomendasi Jual",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = draft.hargaJual.formatRupiah(),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern Clean Ingredient Input Card
 */
@Composable
fun IngredientInputItem(
    index: Int,
    ingredient: Ingredient,
    category: String,
    onUpdate: (Ingredient) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CardBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Item # + Delete icon button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "#${index + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDelete)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nama Bahan
            ModernInputField(
                value = ingredient.name,
                onValueChange = { onUpdate(ingredient.copy(name = it)) },
                label = when (category) {
                    "RETAIL" -> "Nama Barang"
                    "SERVICE" -> "Nama Alat / Bahan"
                    else -> "Nama Bahan Baku"
                },
                placeholder = "misal: Susu UHT / Kopi / Cup"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Baris Harga Beli & Jumlah Beli
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RupiahInputField(
                    value = if (ingredient.buyPrice == 0.0) "" else if (ingredient.buyPrice % 1.0 == 0.0) ingredient.buyPrice.toLong().toString() else ingredient.buyPrice.toString(),
                    onValueChange = { onUpdate(ingredient.copy(buyPrice = it.toDoubleOrNull() ?: 0.0)) },
                    label = "Harga Beli",
                    placeholder = "0",
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                )

                ModernInputField(
                    value = if (ingredient.buyQty == 0.0) "" else ingredient.buyQty.toString(),
                    onValueChange = { onUpdate(ingredient.copy(buyQty = it.toDoubleOrNull() ?: 0.0)) },
                    label = "Jumlah Beli",
                    placeholder = "misal: 1000",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Baris Jumlah Pakai & Satuan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernInputField(
                    value = if (ingredient.usedQty == 0.0) "" else ingredient.usedQty.toString(),
                    onValueChange = { onUpdate(ingredient.copy(usedQty = it.toDoubleOrNull() ?: 0.0)) },
                    label = "Jumlah Pakai",
                    placeholder = "misal: 50",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                ModernInputField(
                    value = ingredient.unit,
                    onValueChange = { onUpdate(ingredient.copy(unit = it)) },
                    label = "Satuan",
                    placeholder = "gr / ml / pcs",
                    modifier = Modifier.weight(1f)
                )
            }

            // Subtotal biaya bahan terpakai
            if (ingredient.cost > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Subtotal Biaya",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = ingredient.cost.formatRupiah(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable clean modern input field with soft container and rounded corners
 */
@Composable
fun ModernInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    prefixText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = if (placeholder != null) {
                {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else null,
            prefix = if (prefixText != null) {
                {
                    Text(
                        text = prefixText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = CardBorderSubtle
            )
        )
    }
}
