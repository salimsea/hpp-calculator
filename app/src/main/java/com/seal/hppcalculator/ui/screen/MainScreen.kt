package com.seal.hppcalculator.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seal.hppcalculator.data.model.ProductCost
import com.seal.hppcalculator.viewmodel.HppViewModel
// import com.seal.hppcalculator.ads.AdBanner  // IKLAN DIMATIKAN
import java.util.Locale

enum class BottomNavItem(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),
    DATA("Data HPP", Icons.Filled.List),
    ABOUT("About", Icons.Filled.Info)
}

enum class HppCategory(val title: String, val id: String, val icon: ImageVector) {
    RETAIL("Retail / Toko", "RETAIL", Icons.Filled.ShoppingCart),
    FNB("Makanan & Minuman", "FNB", Icons.Filled.Favorite),
    SERVICE("Jasa", "SERVICE", Icons.Filled.Build)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: HppViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToResult: (Long) -> Unit
) {
    val history by viewModel.history.collectAsState()
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }
    
    var showCategorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                BottomNavItem.values().forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontWeight = FontWeight.SemiBold) },
                        selected = selectedTab == item,
                        onClick = { selectedTab = item },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab != BottomNavItem.ABOUT) {
                ExtendedFloatingActionButton(
                    onClick = { showCategorySheet = true },
                    icon = { Icon(Icons.Filled.Add, "Tambah") },
                    text = { Text("Hitung HPP", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                BottomNavItem.HOME -> HomeDashboard(
                    history = history,
                    viewModel = viewModel,
                    onNavigateToResult = onNavigateToResult,
                    onNavigateToDataTab = { selectedTab = BottomNavItem.DATA }
                )
                BottomNavItem.DATA -> DataHppList(history, viewModel, onNavigateToResult)
                BottomNavItem.ABOUT -> AboutScreen()
            }
        }
    }

    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Pilih Kategori Usaha",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Pilih kategori yang sesuai agar formulir HPP bisa disesuaikan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                HppCategory.values().forEach { category ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.updateDraft(ProductCost(category = category.id))
                                showCategorySheet = false
                                onNavigateToCreate()
                            },
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(category.icon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                category.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HomeDashboard(
    history: List<ProductCost>,
    viewModel: HppViewModel,
    onNavigateToResult: (Long) -> Unit,
    onNavigateToDataTab: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Halo, Pengusaha!", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
                            Text("Dashboard HPP", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Kalkulasi HPP", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${history.size} Item", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Filled.List, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Ringkasan Kategori",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            val retailCount = history.count { it.category == "RETAIL" }
            val fnbCount = history.count { it.category == "FNB" }
            val serviceCount = history.count { it.category == "SERVICE" }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CategoryModernCard("Retail", retailCount, Icons.Filled.ShoppingCart, Color(0xFF3B82F6), Modifier.weight(1f))
                CategoryModernCard("F&B", fnbCount, Icons.Filled.Favorite, Color(0xFFEF4444), Modifier.weight(1f))
                CategoryModernCard("Jasa", serviceCount, Icons.Filled.Build, Color(0xFFF59E0B), Modifier.weight(1f))
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
            // IKLAN DIMATIKAN
            // AdBanner(modifier = Modifier.padding(horizontal = 24.dp))
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "HPP Terbaru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onNavigateToDataTab) {
                    Text("Lihat Semua", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val recentItems = history.sortedByDescending { it.id }.take(5)
        
        if (recentItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp), 
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data kalkulasi HPP.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(recentItems, key = { it.id }) { product ->
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    ProductItem(
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

@Composable
fun CategoryModernCard(title: String, count: Int, icon: ImageVector, iconColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("$count Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DataHppList(
    history: List<ProductCost>, 
    viewModel: HppViewModel, 
    onNavigateToResult: (Long) -> Unit
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Semua", "Retail", "F&B", "Jasa")
    val categoryIds = listOf("ALL", "RETAIL", "FNB", "SERVICE")

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column {
                Text(
                    "Data Kalkulasi HPP",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(24.dp, 24.dp, 24.dp, 8.dp)
                )
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama produk...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(androidx.compose.material.icons.Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(androidx.compose.material.icons.Icons.Filled.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
                
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 24.dp,
                    indicator = { tabPositions ->
                        if (selectedCategoryIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedCategoryIndex == index,
                            onClick = { selectedCategoryIndex = index },
                            text = { 
                                Text(
                                    title, 
                                    fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
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
            Box(
                modifier = Modifier.fillMaxSize(), 
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada data untuk kategori ini", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredHistory.size, key = { filteredHistory[it].id }) { index ->
                    val product = filteredHistory[index]
                    ProductItem(
                        product = product,
                        onClick = { 
                            viewModel.setDraftForEdit(product)
                            onNavigateToResult(product.id) 
                        }
                    )
                    
                    // Show an ad every 3 items
                    if ((index + 1) % 3 == 0 && index != filteredHistory.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                        // IKLAN DIMATIKAN
                        // AdBanner(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(20.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Kalkulator HPP App",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Versi 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProductItem(product: ProductCost, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val catLabel = HppCategory.values().find { it.id == product.category }?.title ?: "Umum"
                    Text(
                        text = catLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.productName.ifEmpty { "Produk Tanpa Nama" }, 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Target: ${product.productionQty.toInt()} Unit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    val formattedMargin = String.format(Locale("in", "ID"), "%.2f", product.marginPercent)
                    Text(
                        text = "Margin $formattedMargin%",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant ?: Color(0xFFE5E7EB))
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "HPP / Unit",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = product.hppPerUnit.formatRupiah(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Harga Jual",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = product.hargaJual.formatRupiah(), 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
