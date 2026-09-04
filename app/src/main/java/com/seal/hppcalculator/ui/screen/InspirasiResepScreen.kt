package com.seal.hppcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seal.hppcalculator.data.model.SampleRecipe
import com.seal.hppcalculator.data.repository.SampleRecipeProvider
import com.seal.hppcalculator.ui.theme.*
import com.seal.hppcalculator.viewmodel.HppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspirasiResepScreen(
    viewModel: HppViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onNavigateToCreate: () -> Unit,
    onNavigateToResult: (Long) -> Unit
) {
    val context = LocalContext.current
    val sampleRecipes = remember { SampleRecipeProvider.getSampleRecipes(context) }
    var selectedCategoryTab by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSampleRecipe by remember { mutableStateOf<SampleRecipe?>(null) }

    val categoryTabs = listOf(
        Triple("ALL", "Semua", Icons.Filled.List),
        Triple("FNB", "F&B / Kuliner", Icons.Filled.Favorite),
        Triple("RETAIL", "Retail & Toko", Icons.Filled.ShoppingCart),
        Triple("SERVICE", "Jasa & Layanan", Icons.Filled.Build)
    )

    val filteredRecipes = remember(sampleRecipes, selectedCategoryTab, searchQuery) {
        sampleRecipes.filter { recipe ->
            val matchesCategory = selectedCategoryTab == "ALL" || recipe.category.equals(selectedCategoryTab, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    recipe.name.contains(searchQuery, ignoreCase = true) ||
                    recipe.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inspirasi Resep & Usaha",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    )
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    }
                },
                windowInsets = WindowInsets(0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Cari resep, produk, atau ide usaha...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Cari",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Hapus",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = CardBorderSubtle
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 2. Category Tabs (Pills)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categoryTabs) { (id, label, icon) ->
                        val isSelected = selectedCategoryTab == id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else CardBorderSubtle
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCategoryTab = id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 3. Grouped Content
            if (filteredRecipes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ditemukan resep atau ide usaha.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (selectedCategoryTab == "ALL" && searchQuery.isBlank()) {
                // Group by Category (FNB, RETAIL, SERVICE)
                val fnbRecipes = filteredRecipes.filter { it.category.equals("FNB", ignoreCase = true) }
                val retailRecipes = filteredRecipes.filter { it.category.equals("RETAIL", ignoreCase = true) }
                val serviceRecipes = filteredRecipes.filter { it.category.equals("SERVICE", ignoreCase = true) }

                if (fnbRecipes.isNotEmpty()) {
                    item {
                        CategorySectionHeader(
                            title = "Makanan & Minuman (F&B)",
                            count = fnbRecipes.size,
                            icon = Icons.Filled.Favorite,
                            iconBg = TagOrangeBg,
                            iconTint = TagOrangeText
                        )
                    }
                    items(fnbRecipes, key = { it.id }) { recipe ->
                        FullInspirasiCard(recipe = recipe, onClick = { selectedSampleRecipe = recipe })
                    }
                }

                if (retailRecipes.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        CategorySectionHeader(
                            title = "Retail & Toko Online",
                            count = retailRecipes.size,
                            icon = Icons.Filled.ShoppingCart,
                            iconBg = TagBlueBg,
                            iconTint = TagBlueText
                        )
                    }
                    items(retailRecipes, key = { it.id }) { recipe ->
                        FullInspirasiCard(recipe = recipe, onClick = { selectedSampleRecipe = recipe })
                    }
                }

                if (serviceRecipes.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        CategorySectionHeader(
                            title = "Jasa & Layanan",
                            count = serviceRecipes.size,
                            icon = Icons.Filled.Build,
                            iconBg = TagPurpleBg,
                            iconTint = TagPurpleText
                        )
                    }
                    items(serviceRecipes, key = { it.id }) { recipe ->
                        FullInspirasiCard(recipe = recipe, onClick = { selectedSampleRecipe = recipe })
                    }
                }
            } else {
                // Flat list for specific category or search results
                items(filteredRecipes, key = { it.id }) { recipe ->
                    FullInspirasiCard(recipe = recipe, onClick = { selectedSampleRecipe = recipe })
                }
            }
        }
    }

    // Modal Detail Inspirasi Resep & Simulasi
    if (selectedSampleRecipe != null) {
        SampleRecipeDetailSheet(
            sample = selectedSampleRecipe!!,
            onDismiss = { selectedSampleRecipe = null },
            onUseRecipe = {
                val recipe = selectedSampleRecipe!!
                com.seal.hppcalculator.util.AppRatingManager.incrementActionAndCheckShouldPrompt(context)
                viewModel.applySampleRecipe(recipe)
                selectedSampleRecipe = null
                onNavigateToCreate()
            },
            onSaveDirectly = {
                val recipe = selectedSampleRecipe!!
                com.seal.hppcalculator.util.AppRatingManager.incrementActionAndCheckShouldPrompt(context)
                viewModel.saveSampleRecipeToHistory(recipe) { savedId ->
                    selectedSampleRecipe = null
                    onNavigateToResult(savedId)
                }
            }
        )
    }
}

@Composable
private fun CategorySectionHeader(
    title: String,
    count: Int,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = "$count Ide",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun FullInspirasiCard(
    recipe: SampleRecipe,
    onClick: () -> Unit
) {
    val (catLabel, catBg, catText) = when (recipe.category.uppercase()) {
        "RETAIL" -> Triple("Retail", TagBlueBg, TagBlueText)
        "SERVICE" -> Triple("Jasa", TagPurpleBg, TagPurpleText)
        else -> Triple("F&B", TagOrangeBg, TagOrangeText)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, CardBorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = catBg
                ) {
                    Text(
                        text = catLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = catText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TagGreenBg
                ) {
                    Text(
                        text = "Margin ${recipe.marginPercent.toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = TagGreenText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (recipe.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pricing Bento Row
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, CardBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HPP / Unit",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = recipe.hppPerUnit.formatRupiah(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Harga Jual",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = recipe.hargaJual.formatRupiah(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Laba / Unit",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+${recipe.labaPerUnit.formatRupiah()}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            ),
                            color = TagGreenText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Potensi Laba Bulanan Highlight
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TagGreenBg,
                border = BorderStroke(1.dp, TagGreenText.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target ${recipe.dailySalesTarget} unit/hr ➔ Potensi Laba:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "+${recipe.totalLabaPeriode.formatRupiah()}/bln",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        ),
                        color = TagGreenText
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lihat Rincian & Resep →",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
