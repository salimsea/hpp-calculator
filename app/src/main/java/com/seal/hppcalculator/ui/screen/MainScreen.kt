package com.seal.hppcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.seal.hppcalculator.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seal.hppcalculator.data.model.ProductCost
import com.seal.hppcalculator.data.model.SampleRecipe
import com.seal.hppcalculator.data.repository.DocumentExportHelper
import com.seal.hppcalculator.data.repository.SampleRecipeProvider
import com.seal.hppcalculator.ui.theme.*
import com.seal.hppcalculator.viewmodel.HppViewModel
import java.util.Locale

enum class BottomNavItem(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),
    INSPIRASI("Inspirasi", Icons.Filled.Lightbulb),
    CASHFLOW("Buku Kas", Icons.Filled.ShoppingCart),
    SETTINGS("Pengaturan", Icons.Filled.Settings)
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
    onNavigateToResult: (Long) -> Unit,
    onNavigateToMarkupOjol: () -> Unit = {},
    onNavigateToTargetBep: () -> Unit = {},
    onNavigateToDataHpp: () -> Unit = {},
    onResetAppToOnboarding: () -> Unit = {}
) {
    val history by viewModel.history.collectAsState()
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }
    val context = androidx.compose.ui.platform.LocalContext.current

    var showCategorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var showRatingDialog by remember { mutableStateOf(false) }

    // Check on launch / resume if 3 actions have been reached
    LaunchedEffect(history.size) {
        val prefs = context.getSharedPreferences("app_rating_prefs", android.content.Context.MODE_PRIVATE)
        val currentCount = prefs.getInt("key_save_or_use_action_count", 0)
        val hasRated = prefs.getBoolean("key_has_rated", false)
        val neverShow = prefs.getBoolean("key_never_show", false)
        if (currentCount >= 3 && !hasRated && !neverShow) {
            showRatingDialog = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Clean, minimal bottom navigation bar with soft border
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem.values().forEach { item ->
                        val isSelected = selectedTab == item
                        val activeColor = MaterialTheme.colorScheme.primary
                        val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedTab = item }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (isSelected) activeColor else inactiveColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) activeColor else inactiveColor
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                BottomNavItem.HOME -> HomeDashboard(
                    history = history,
                    viewModel = viewModel,
                    onNavigateToResult = onNavigateToResult,
                    onNavigateToCreate = onNavigateToCreate,
                    onNavigateToDataHpp = onNavigateToDataHpp,
                    onNavigateToCashflowTab = {
                        selectedTab = BottomNavItem.CASHFLOW
                    },
                    onOpenCategorySheet = { showCategorySheet = true },
                    onNavigateToMarkupOjol = onNavigateToMarkupOjol,
                    onNavigateToTargetBep = onNavigateToTargetBep
                )
                BottomNavItem.INSPIRASI -> InspirasiResepScreen(
                    viewModel = viewModel,
                    onNavigateToCreate = onNavigateToCreate,
                    onNavigateToResult = onNavigateToResult
                )
                BottomNavItem.CASHFLOW -> CashflowScreen(
                    viewModel = viewModel,
                    onNavigateToCreateHpp = { showCategorySheet = true }
                )
                BottomNavItem.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onResetAppToOnboarding = onResetAppToOnboarding
                )
            }
        }
    }

    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pilih Kategori Usaha",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Format formulir perhitungan HPP akan disesuaikan dengan jenis usahamu.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                HppCategory.values().forEach { category ->
                    val (iconBg, iconTint) = when (category) {
                        HppCategory.RETAIL -> Pair(TagBlueBg, TagBlueText)
                        HppCategory.FNB -> Pair(TagOrangeBg, TagOrangeText)
                        HppCategory.SERVICE -> Pair(TagPurpleBg, TagPurpleText)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                viewModel.updateDraft(ProductCost(category = category.id))
                                showCategorySheet = false
                                onNavigateToCreate()
                            },
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, CardBorderSubtle),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = iconBg,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = category.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (category) {
                                        HppCategory.RETAIL -> "Toko fisik, online shop, & kulakan"
                                        HppCategory.FNB -> "Resep kuliner, minuman, & kemasan"
                                        HppCategory.SERVICE -> "Jasa, alat kerja, & bahan habis pakai"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showRatingDialog) {
        com.seal.hppcalculator.ui.components.GooglePlayRatingDialog(
            onDismiss = { showRatingDialog = false },
            onRateNow = {
                showRatingDialog = false
                com.seal.hppcalculator.util.AppRatingManager.setHasRated(context)
                com.seal.hppcalculator.util.AppRatingManager.openPlayStore(context)
            },
            onRemindLater = {
                showRatingDialog = false
                com.seal.hppcalculator.util.AppRatingManager.setRemindLater(context)
            }
        )
    }
}

@Composable
fun HomeDashboard(
    history: List<ProductCost>,
    viewModel: HppViewModel,
    onNavigateToResult: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDataHpp: () -> Unit,
    onNavigateToCashflowTab: () -> Unit,
    onOpenCategorySheet: () -> Unit,
    onNavigateToMarkupOjol: () -> Unit = {},
    onNavigateToTargetBep: () -> Unit = {}
) {
    var homeSearchQuery by remember { mutableStateOf("") }
    var selectedCategoryChip by remember { mutableStateOf("ALL") }

    val avgMargin = if (history.isNotEmpty()) {
        history.map { it.marginPercent }.average()
    } else 0.0

    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("hpp_prefs", android.content.Context.MODE_PRIVATE) }
    val savedName = remember { sharedPrefs.getString("userName", "Juragan HPP") ?: "Juragan HPP" }
    val savedBusiness = remember { sharedPrefs.getString("businessName", "") ?: "" }
    // Navigation to full-page screens (replaced bottom sheet modals)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp)
    ) {
        // 1. CLEAN TOP HEADER (Matching Dribbble Reference)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (savedBusiness.isNotEmpty()) savedBusiness else "Halo,",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$savedName 👋",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Logo Aplikasi",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }

        // 3. TWO BENTO STAT CARDS SIDE-BY-SIDE (Exact look from Reference Image)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // LEFT CARD: PURPLE GRADIENT (Total HPP)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, CardBorderSubtle)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PurpleCardGradient)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.22f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.List,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${history.size}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 28.sp
                                    ),
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Total HPP",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                // RIGHT CARD: ORANGE/AMBER GRADIENT (Rata-rata Margin)
                val formattedMargin = String.format(Locale("in", "ID"), "%.0f%%", avgMargin)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, CardBorderSubtle)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OrangeCardGradient)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.22f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = formattedMargin,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 26.sp
                                    ),
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Avg. Margin",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // GOJEK-STYLE ICON GRID MENU
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                ) {
                    // Row 1 (4 Menu Utama)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 1. Hitung HPP
                        GojekMenuItem(
                            icon = Icons.Filled.Calculate,
                            label = "Hitung HPP",
                            iconBgColor = TagPurpleBg,
                            iconTintColor = TagPurpleText,
                            onClick = onOpenCategorySheet
                        )

                        // 2. Markup Ojol
                        GojekMenuItem(
                            icon = Icons.Filled.TwoWheeler,
                            label = "Markup Ojol",
                            iconBgColor = TagPurpleBg,
                            iconTintColor = TagPurpleText,
                            onClick = onNavigateToMarkupOjol
                        )

                        // 3. Buku Kas
                        GojekMenuItem(
                            icon = Icons.Filled.AccountBalanceWallet,
                            label = "Buku Kas",
                            iconBgColor = TagPurpleBg,
                            iconTintColor = TagPurpleText,
                            onClick = onNavigateToCashflowTab
                        )

                        // 4. Target BEP
                        GojekMenuItem(
                            icon = Icons.Filled.TrendingUp,
                            label = "Target BEP",
                            iconBgColor = TagPurpleBg,
                            iconTintColor = TagPurpleText,
                            onClick = onNavigateToTargetBep
                        )
                    }
                }
            }
        }

        // 3. CATEGORY PILLS FILTER ROW
        item {
            Spacer(modifier = Modifier.height(20.dp))
            val categoryChips = listOf("ALL" to "Semua", "FNB" to "F&B", "RETAIL" to "Retail", "SERVICE" to "Jasa")

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categoryChips) { (id, label) ->
                    val isSelected = selectedCategoryChip == id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedCategoryChip = id },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) null else BorderStroke(1.dp, CardBorderSubtle)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                        )
                    }
                }
            }
        }

        // 5. SECTION TITLE: "Kalkulasi Terbaru" (matching "Upcoming event" in screenshot)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kalkulasi Terbaru",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (history.isNotEmpty()) {
                    Text(
                        text = "Lihat Semua",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateToDataHpp() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 6. FILTERED ITEMS
        val filteredByCategory = if (selectedCategoryChip == "ALL") history else history.filter { it.category == selectedCategoryChip }
        val filteredList = if (homeSearchQuery.isBlank()) filteredByCategory else filteredByCategory.filter {
            it.productName.contains(homeSearchQuery, ignoreCase = true)
        }
        val recentItems = filteredList.sortedByDescending { it.id }.take(6)

        if (recentItems.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, CardBorderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.il_empty_state),
                            contentDescription = "Belum ada kalkulasi HPP",
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(160.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (homeSearchQuery.isNotEmpty()) "Produk tidak ditemukan" else "Belum Ada Kalkulasi HPP",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (homeSearchQuery.isNotEmpty()) "Coba gunakan kata kunci lain" else "Mulai hitung harga pokok dan margin keuntungan pertamamu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onOpenCategorySheet,
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hitung HPP Pertama", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            items(recentItems, key = { it.id }) { product ->
                Box(modifier = Modifier.padding(vertical = 6.dp)) {
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

/**
 * Gojek-style circular icon menu item with label underneath.
 * Used in the 2x2 quick tools grid on Home Dashboard.
 */
@Composable
fun GojekMenuItem(
    icon: ImageVector,
    label: String,
    iconBgColor: Color,
    iconTintColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .width(72.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = iconBgColor,
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTintColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Clean Product Card resembling the card list in reference image (Upcoming event list)
 */
@Composable
fun CleanProductCard(product: ProductCost, onClick: () -> Unit) {
    val (catLabel, catBg, catText) = when (product.category) {
        "RETAIL" -> Triple("Retail", TagBlueBg, TagBlueText)
        "SERVICE" -> Triple("Jasa", TagPurpleBg, TagPurpleText)
        else -> Triple("F&B", TagOrangeBg, TagOrangeText)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CardBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top Row: Category Tag + Margin Tag (Like "Medium" and "Weekly" tags in reference)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }

                    val marginFormatted = String.format(Locale("in", "ID"), "%.1f%%", product.marginPercent)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (product.marginPercent >= 25.0) TagGreenBg else TagOrangeBg
                    ) {
                        Text(
                            text = "Margin $marginFormatted",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (product.marginPercent >= 25.0) TagGreenText else TagOrangeText,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "${product.productionQty.toInt()} Unit",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product Title
            Text(
                text = product.productName.ifEmpty { "Produk Tanpa Nama" },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = OutlineLight.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Pricing Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Modal HPP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = product.hppPerUnit.formatRupiah(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Harga Jual",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = product.hargaJual.formatRupiah(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}





/**
 * Modal BottomSheet for Detail Recipe Inspiration and Profit Simulation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleRecipeDetailSheet(
    sample: SampleRecipe,
    onDismiss: () -> Unit,
    onUseRecipe: () -> Unit,
    onSaveDirectly: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (catLabel, catBg, catText) = when (sample.category) {
                    "RETAIL" -> Triple("Retail", TagBlueBg, TagBlueText)
                    "SERVICE" -> Triple("Jasa", TagPurpleBg, TagPurpleText)
                    else -> Triple("F&B", TagOrangeBg, TagOrangeText)
                }
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
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TagGreenBg
                ) {
                    Text(
                        text = "Margin ${sample.marginPercent.toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = TagGreenText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = sample.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (sample.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = sample.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bento Pricing Box (HPP, Jual, Laba)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, CardBorderSubtle)
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
                            text = "HPP / Unit",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = sample.hppPerUnit.formatRupiah(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Harga Jual",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = sample.hargaJual.formatRupiah(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Untung / Unit",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+${sample.labaPerUnit.formatRupiah()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            ),
                            color = TagGreenText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SIMULASI PROYEKSI LABA CARD
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = TagGreenBg,
                border = BorderStroke(1.dp, TagGreenText.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎯 SIMULASI TARGET BISNIS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = TagGreenText
                        )
                        Text(
                            text = "${sample.targetDays} Hari",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = TagGreenText
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Jual ${sample.dailySalesTarget} unit/hari ➔ Potensi Untung Bersih:",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "+ ${sample.totalLabaPeriode.formatRupiah()} / Bulan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        ),
                        color = TagGreenText
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Omset Penjualan: ${sample.totalOmsetPeriode.formatRupiah()} • Modal Terputar: ${(sample.hppPerUnit * sample.dailySalesTarget * sample.targetDays).formatRupiah()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (sample.businessTips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, CardBorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = TagOrangeText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = sample.businessTips,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // DAFTAR BAHAN BAKU
            Text(
                text = "Komponen Bahan & Modal (${sample.ingredients.size} Item)",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            sample.ingredients.forEach { ing ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ${ing.name}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = ing.cost.formatRupiah(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onSaveDirectly,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Simpan ke Data",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = onUseRecipe,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Gunakan & Edit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
