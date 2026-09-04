package com.seal.hppcalculator.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.seal.hppcalculator.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingFeature(
    val title: String,
    val subtitle: String,
    val description: String,
    val imageRes: Int,
    val tags: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sharedPrefs = remember { context.getSharedPreferences("hpp_prefs", Context.MODE_PRIVATE) }

    val features = listOf(
        OnboardingFeature(
            title = "Hitung Modal Otomatis",
            subtitle = "Akurat & Cepat",
            description = "Masukkan takaran bahan baku, kemasan, dan biaya operasional. Aplikasi menghitung HPP per porsi/unit secara otomatis tanpa repot rumus Excel.",
            imageRes = R.drawable.il_personal_finance,
            tags = listOf("Bahan Baku", "Operasional", "HPP per Porsi")
        ),
        OnboardingFeature(
            title = "Simulasi Margin & Harga",
            subtitle = "Tentukan Target Laba",
            description = "Atur persentase keuntungan yang kamu mau lewat slider interaktif dan dapatkan rekomendasi harga jual paling pas untuk pasar bisnismu.",
            imageRes = R.drawable.il_printing_invoices,
            tags = listOf("Simulasi Margin", "Rekomendasi Jual", "Hitung Profit")
        ),
        OnboardingFeature(
            title = "Siap untuk Semua Usaha",
            subtitle = "Retail, F&B & Jasa",
            description = "Dirancang khusus mendampingi wirausaha UMKM Kuliner, Toko Kelontong/Retail, hingga Jasa Service. Data tersimpan aman dan offline.",
            imageRes = R.drawable.il_handshake_deal,
            tags = listOf("Kuliner F&B", "Toko Retail", "Jasa Layanan")
        )
    )

    // Total 4 pages: 3 features + 1 profile setup
    val totalPages = features.size + 1
    val pagerState = rememberPagerState(pageCount = { totalPages })

    var userNameInput by remember { mutableStateOf("") }
    var businessNameInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("FNB") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Logo & Name
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Logo Aplikasi",
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HPP Calculator",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Skip button (visible on pages 0-2)
                if (pagerState.currentPage < totalPages - 1) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(totalPages - 1)
                            }
                        }
                    ) {
                        Text(
                            text = "Lewati",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 18.dp)
                ) {
                    // Pager Indicator Dots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(totalPages) { index ->
                            val isSelected = pagerState.currentPage == index
                            val width by animateDpAsState(
                                targetValue = if (isSelected) 28.dp else 8.dp,
                                label = "indicatorWidth"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(7.dp)
                                    .width(width)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) PurplePrimary else CardBorderSubtle
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (pagerState.currentPage > 0) {
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, CardBorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "Kembali",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (pagerState.currentPage < totalPages - 1) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                } else {
                                    // Save preferences
                                    val finalName = userNameInput.trim().ifEmpty { "Juragan" }
                                    val finalBusiness = businessNameInput.trim()
                                    sharedPrefs.edit()
                                        .putBoolean("hasSeenOnboarding", true)
                                        .putString("userName", finalName)
                                        .putString("businessName", finalBusiness)
                                        .putString("userCategory", selectedCategory)
                                        .apply()
                                    onFinish()
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PurplePrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(if (pagerState.currentPage > 0) 1.5f else 1f)
                                .height(52.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (pagerState.currentPage == totalPages - 1) "Mulai Sekarang" else "Lanjut",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (pagerState.currentPage < totalPages - 1) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { pageIndex ->
            if (pageIndex < features.size) {
                // Feature Presentation Slide
                val feature = features[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Illustration Card with Soft Border
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp),
                        shape = RoundedCornerShape(26.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, CardBorderSubtle)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = feature.imageRes),
                                contentDescription = feature.title,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Feature Subtitle Tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = OrangeLight
                    ) {
                        Text(
                            text = feature.subtitle.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = OrangeAccent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = feature.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Feature highlight tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        feature.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, CardBorderSubtle)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Profile Setup Slide (Step 4)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Header Avatar Icon
                    Surface(
                        shape = CircleShape,
                        color = PurpleLight,
                        border = BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.2f)),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "👋",
                                fontSize = 34.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Kenalan Dulu, Juragan!",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 23.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Biar aplikasi terasa lebih personal, isi nama panggilan dan usaha yang sedang kamu jalankan.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Profile Setup Card
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
                            // Nama Juragan Input
                            Column {
                                Text(
                                    text = "Nama Kamu / Panggilan",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                )
                                OutlinedTextField(
                                    value = userNameInput,
                                    onValueChange = { userNameInput = it },
                                    placeholder = {
                                        Text(
                                            "misal: Salim / Budi",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            fontSize = 14.sp
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        focusedBorderColor = PurplePrimary,
                                        unfocusedBorderColor = CardBorderSubtle
                                    )
                                )
                            }

                            // Nama Usaha Input
                            Column {
                                Text(
                                    text = "Nama Usaha / Toko (Opsional)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                )
                                OutlinedTextField(
                                    value = businessNameInput,
                                    onValueChange = { businessNameInput = it },
                                    placeholder = {
                                        Text(
                                            "misal: Dapur Mama / Salim Jaya",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            fontSize = 14.sp
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        focusedBorderColor = PurplePrimary,
                                        unfocusedBorderColor = CardBorderSubtle
                                    )
                                )
                            }

                            // Pilihan Jenis Usaha
                            Column {
                                Text(
                                    text = "Bidang Usaha Utama",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )

                                val categories = listOf(
                                    Triple("FNB", "Kuliner / F&B", "☕"),
                                    Triple("RETAIL", "Retail / Toko", "🛍️"),
                                    Triple("SERVICE", "Jasa Layanan", "✂️")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    categories.forEach { (id, label, emoji) ->
                                        val isSelected = selectedCategory == id
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { selectedCategory = id },
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) PurplePrimary else MaterialTheme.colorScheme.surface,
                                            border = if (isSelected) null else BorderStroke(1.dp, CardBorderSubtle)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(emoji, fontSize = 18.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        fontSize = 10.sp
                                                    ),
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                    textAlign = TextAlign.Center
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
    }
}
