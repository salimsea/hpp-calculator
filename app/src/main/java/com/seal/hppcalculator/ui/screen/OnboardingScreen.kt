package com.seal.hppcalculator.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.seal.hppcalculator.R

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = "Hitung HPP Cepat & Tepat",
            description = "Tidak perlu pusing lagi menghitung modal. Masukkan bahan baku dan biaya operasional, biar aplikasi yang hitung otomatis!",
            imageRes = R.drawable.il_data_input,
            color = Color(0xFF3B82F6) // Blue
        ),
        OnboardingPage(
            title = "Simulasi Margin Keuntungan",
            description = "Atur persentase keuntungan yang kamu mau, dan lihat langsung rekomendasi harga jual terbaik untuk produkmu.",
            imageRes = R.drawable.il_certification,
            color = Color(0xFF10B981) // Green
        ),
        OnboardingPage(
            title = "Pantau Semua Jenis Usaha",
            description = "Mulai dari Retail, F&B, hingga Jasa. Semua data tersimpan aman secara offline di perangkatmu.",
            imageRes = R.drawable.il_performance_comparison,
            color = Color(0xFFF59E0B) // Orange
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Pager Indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pages.size) { iteration ->
                            val isSelected = pagerState.currentPage == iteration
                            val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            val width = if (isSelected) 24.dp else 8.dp
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .height(8.dp)
                                    .width(width)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (pagerState.currentPage > 0) {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            ) {
                                Text("Kembali", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(80.dp)) // Placeholder to keep alignment
                        }

                        Button(
                            onClick = {
                                if (pagerState.currentPage < pages.size - 1) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                } else {
                                    onFinish()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                if (pagerState.currentPage == pages.size - 1) "Mulai Sekarang" else "Lanjut",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { position ->
            val page = pages[position]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Illustration Image
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                )
            }
        }
    }
}
