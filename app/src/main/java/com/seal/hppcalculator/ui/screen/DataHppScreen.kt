package com.seal.hppcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seal.hppcalculator.R
import com.seal.hppcalculator.data.model.ProductCost
import com.seal.hppcalculator.data.repository.DocumentExportHelper
import com.seal.hppcalculator.ui.theme.*
import com.seal.hppcalculator.viewmodel.HppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataHppScreen(
    viewModel: HppViewModel,
    initialCategoryId: String = "ALL",
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Long) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val history by viewModel.history.collectAsState()
    val context = LocalContext.current

    val categoryIds = listOf("ALL", "RETAIL", "FNB", "SERVICE")
    val initialIndex = categoryIds.indexOf(initialCategoryId).takeIf { it >= 0 } ?: 0
    var selectedCategoryIndex by remember(initialCategoryId) { mutableStateOf(initialIndex) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Semua", "Retail", "F&B", "Jasa")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Data HPP Tersimpan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    if (history.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TagGreenBg,
                            border = BorderStroke(1.dp, TagGreenText.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    val csvFile = DocumentExportHelper.exportProductsToCsv(context, history)
                                    if (csvFile != null) {
                                        DocumentExportHelper.shareExportedFile(
                                            context,
                                            csvFile,
                                            "text/csv",
                                            "Ekspor Seluruh Data HPP"
                                        )
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.TableChart,
                                    contentDescription = "Ekspor Excel",
                                    tint = TagGreenText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Ekspor Excel",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = TagGreenText
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Hitung HPP Baru"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle)
            ) {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Cari nama produk...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    ScrollableTabRow(
                        selectedTabIndex = selectedCategoryIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 20.dp
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedCategoryIndex == index,
                                onClick = { selectedCategoryIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedCategoryIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }
                }
            }

            val filteredByCategory = if (selectedCategoryIndex == 0) {
                history
            } else {
                history.filter { it.category == categoryIds[selectedCategoryIndex] }
            }

            val filteredHistory = if (searchQuery.isBlank()) {
                filteredByCategory
            } else {
                filteredByCategory.filter {
                    it.productName.contains(searchQuery, ignoreCase = true)
                }
            }

            if (filteredHistory.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.il_empty_state),
                        contentDescription = "Data Kosong",
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .height(170.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Produk Tidak Ditemukan" else "Belum Ada Data Tersimpan",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Coba gunakan kata kunci lain" else "Daftar produk HPP yang kamu simpan akan muncul di sini.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredHistory, key = { it.id }) { product ->
                        CleanProductCard(
                            product = product,
                            onClick = {
                                viewModel.setDraftForEdit(product)
                                onNavigateToResult(product.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
