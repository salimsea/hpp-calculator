package com.seal.hppcalculator.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seal.hppcalculator.data.model.*
import com.seal.hppcalculator.ui.components.RupiahInputField
import com.seal.hppcalculator.ui.theme.*
import java.util.Locale

/**
 * Full-page screen untuk Analisis Target Impas (BEP), Goal Planner & CapEx Planner.
 * Menggantikan QuickBusinessAnalysisSheet (ModalBottomSheet).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetImpasBepScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val productName = "Simulasi Usaha Cepat"
    val hppPerUnit = 8000.0
    val hargaJual = 15000.0

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Goal Planner, 1: CapEx

    // Tab 1 States (Goal Planner & BEP)
    var fixedCostInput by remember { mutableStateOf("3000000") }
    var targetProfitInput by remember { mutableStateOf("5000000") }
    var workingDays by remember { mutableIntStateOf(30) }

    val fixedCost = fixedCostInput.toDoubleOrNull() ?: 0.0
    val targetProfit = targetProfitInput.toDoubleOrNull() ?: 0.0

    val bep = remember(fixedCost, hargaJual, hppPerUnit, workingDays) {
        BusinessAnalysisCalculator.calculateBep(
            totalFixedCostMonthly = fixedCost,
            sellingPrice = hargaJual,
            hppPerUnit = hppPerUnit,
            workingDays = workingDays
        )
    }

    val goal = remember(targetProfit, fixedCost, hargaJual, hppPerUnit, workingDays) {
        BusinessAnalysisCalculator.calculateProfitGoal(
            targetMonthlyProfit = targetProfit,
            totalFixedCostMonthly = fixedCost,
            sellingPrice = hargaJual,
            hppPerUnit = hppPerUnit,
            workingDays = workingDays
        )
    }

    // Tab 2 States (CapEx Planner)
    var capexItems by remember {
        mutableStateOf(
            listOf(
                CapexItem("Booth / Gerobak / Interior", 6000000.0),
                CapexItem("Peralatan & Mesin Utama", 5000000.0),
                CapexItem("Banner, Neon Box & Branding", 1000000.0),
                CapexItem("Stok Awal Bahan Baku", 2000000.0)
            )
        )
    }
    var newCapexName by remember { mutableStateOf("") }
    var newCapexCost by remember { mutableStateOf("") }
    var emergencyFundMonths by remember { mutableIntStateOf(2) }

    val capexResult = remember(capexItems, emergencyFundMonths, fixedCost, targetProfit) {
        BusinessAnalysisCalculator.calculateCapex(
            items = capexItems,
            emergencyFundMonths = emergencyFundMonths,
            monthlyOperatingCost = fixedCost,
            monthlyNetProfitEstimate = if (targetProfit > 0) targetProfit else 3000000.0
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Target Impas (BEP)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // TAB SELECTOR (Goal Planner vs CapEx)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
                    .padding(4.dp)
            ) {
                listOf("Target Laba & BEP", "Modal Awal (CapEx)").forEachIndexed { index, label ->
                    val isSelected = selectedTab == index
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedTab = index },
                        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (selectedTab == 0) {
                // ==================== TAB 1: GOAL PLANNER & BEP ====================
                RupiahInputField(
                    value = fixedCostInput,
                    onValueChange = { fixedCostInput = it },
                    label = "1. Biaya Tetap Operasional Bulanan",
                    supportingText = "Sewa tempat, gaji karyawan, listrik, air & kuota",
                    placeholder = "0",
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                RupiahInputField(
                    value = targetProfitInput,
                    onValueChange = { targetProfitInput = it },
                    label = "2. Target Keuntungan Bersih Bulanan",
                    supportingText = "Berapa laba bersih yang ingin Anda kantongi setiap bulan",
                    prefixColor = TagGreenText,
                    placeholder = "0",
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hari Kerja Sebulan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hari Buka Toko Sebulan:",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(26 to "26 Hari (Libur 1x/mgg)", 30 to "30 Hari (Buka Penuh)").forEach { (days, _) ->
                            val isSelected = workingDays == days
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { workingDays = days }
                            ) {
                                Text(
                                    text = "$days Hari",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // HASIL GOAL PLANNER CARD (BENTO HERO)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = TagGreenBg,
                    border = BorderStroke(1.dp, TagGreenText.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "KUOTA PENJUALAN HARIAN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = TagGreenText
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.8f)
                            ) {
                                Text(
                                    text = "Target Profit: ${targetProfit.formatRupiah()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = TagGreenText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Wajib Terjual per Hari",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${goal.targetUnitsDaily} Unit",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 28.sp
                                    ),
                                    color = TagGreenText
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Target Total Bulanan",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${goal.targetUnitsMonthly} Unit",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = TagGreenText.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Omset Harian: ${goal.targetRevenueDaily.formatRupiah()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Omset Bulanan: ${goal.targetRevenueMonthly.formatRupiah()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // PERBANDINGAN BEP VS GOAL
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, CardBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Rangkuman Target Usaha:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• 1 s/d ${bep.bepUnitsDaily} unit/hari: Menutup biaya operasional (BEP)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• ${bep.bepUnitsDaily + 1} s/d ${goal.targetUnitsDaily} unit/hari: Mengumpulkan laba bersih menuju target ${targetProfit.formatRupiah()}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = TagGreenText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // BUTTON SALIN STRATEGI TARGET
                Button(
                    onClick = {
                        val shareText = buildString {
                            append("*TARGET PENJUALAN & GOAL BISNIS*\n")
                            append("*Menu/Produk:* $productName\n")
                            append("───────────────────────\n")
                            append("Biaya Tetap Bulanan: ${fixedCost.formatRupiah()}\n")
                            append("Target Laba Bersih: ${targetProfit.formatRupiah()}\n")
                            append("───────────────────────\n")
                            append("*Titik Impas (BEP):* ${bep.bepUnitsDaily} unit/hari (${bep.bepUnitsMonthly} unit/bln)\n")
                            append("*Target Capai Laba:* ${goal.targetUnitsDaily} unit/hari (${goal.targetUnitsMonthly} unit/bln)\n")
                            append("*Target Omset:* ${goal.targetRevenueDaily.formatRupiah()} / hari\n")
                            append("───────────────────────\n")
                            append("Dihitung dengan *SuperApp Kalkulator HPP UMKM*")
                        }
                        clipboardManager.setText(AnnotatedString(shareText))
                        Toast.makeText(context, "Target bisnis berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salin Target Penjualan ke WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

            } else {
                // ==================== TAB 2: CAPEX PLANNER ====================
                Text(
                    text = "Daftar Kebutuhan Modal Awal Buka Usaha",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                // List of CapEx Items
                capexItems.forEachIndexed { idx, item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, CardBorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = item.cost.formatRupiah(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(
                                onClick = {
                                    capexItems = capexItems.toMutableList().also { it.removeAt(idx) }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Hapus",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Form Tambah Item CapEx Baru
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCapexName,
                        onValueChange = { newCapexName = it },
                        placeholder = { Text("Nama alat / renovasi...", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    RupiahInputField(
                        value = newCapexCost,
                        onValueChange = { newCapexCost = it },
                        placeholder = "Biaya",
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.9f)
                    )
                    Button(
                        onClick = {
                            val cost = newCapexCost.toDoubleOrNull() ?: 0.0
                            if (newCapexName.isNotBlank() && cost > 0) {
                                capexItems = capexItems + CapexItem(newCapexName.trim(), cost)
                                newCapexName = ""
                                newCapexCost = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Tambah")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cadangan Dana Darurat Operasional
                Text(
                    text = "Cadangan Dana Darurat Operasional",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dana cadangan sewa & listrik saat bulan-bulan awal buka toko.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0 to "Tanpa Cadangan", 1 to "1 Bulan", 2 to "2 Bulan", 3 to "3 Bulan").forEach { (months, label) ->
                        val isSelected = emergencyFundMonths == months
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { emergencyFundMonths = months }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    ),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // HASIL RINGKASAN CAPEX & PAYBACK PERIOD
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = TagPurpleBg,
                    border = BorderStroke(1.dp, TagPurpleText.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "TOTAL MODAL AWAL YANG DIBUTUHKAN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = TagPurpleText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = capexResult.totalCapex.formatRupiah(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 26.sp
                            ),
                            color = TagPurpleText
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = TagPurpleText.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Estimasi Balik Modal:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val paybackFormatted = String.format(Locale("in", "ID"), "%.1f Bulan", capexResult.paybackPeriodMonths)
                                Text(
                                    text = paybackFormatted,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Basis Profit/Bulan:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = capexResult.monthlyNetProfitEstimate.formatRupiah(),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = TagGreenText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
