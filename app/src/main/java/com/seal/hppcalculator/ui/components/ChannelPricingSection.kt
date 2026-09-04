package com.seal.hppcalculator.ui.components

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seal.hppcalculator.data.model.ChannelCategory
import com.seal.hppcalculator.data.model.ChannelPriceCalculator
import com.seal.hppcalculator.data.model.ChannelTierPricing
import com.seal.hppcalculator.ui.screen.formatRupiah
import com.seal.hppcalculator.ui.theme.*
import java.util.Locale

/**
 * Komponen UI untuk Milestone 2: Multi-tier Pricing & Saluran Distribusi (Ojol, Marketplace, Reseller)
 */

@Composable
fun ChannelPricingBentoCard(
    productName: String,
    hppPerUnit: Double,
    hargaJual: Double,
    profitPerUnit: Double,
    onOpenDetailSheet: () -> Unit
) {
    val tiers = remember(hargaJual, hppPerUnit) {
        ChannelPriceCalculator.calculateAllTiers(
            productName = productName,
            hppPerUnit = hppPerUnit,
            baseSellingPrice = hargaJual
        )
    }

    val ojolTier = tiers.firstOrNull { it.presetId == "gofood_grab" }
    val marketplaceTier = tiers.firstOrNull { it.presetId == "marketplace" }
    val resellerTier = tiers.firstOrNull { it.presetId == "reseller" }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, CardBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Saluran Jual & Komisi Online",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TagBlueBg
                ) {
                    Text(
                        text = "Anti Tekor",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = TagBlueText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rekomendasi harga jual agar margin bersih tetap utuh setelah dipotong komisi platform.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Multi-channel Preview Grid
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // 1. Offline / Dine-in
                ChannelMiniRow(
                    icon = "🍽️",
                    title = "Dine-in / Offline",
                    tag = "Normal",
                    tagColor = TagGreenBg to TagGreenText,
                    price = hargaJual,
                    subtitle = "Laba bersih: +${profitPerUnit.formatRupiah()}"
                )

                // 2. GoFood & GrabFood
                if (ojolTier != null) {
                    ChannelMiniRow(
                        icon = "🛵",
                        title = "GoFood / GrabFood",
                        tag = "Komisi 20%",
                        tagColor = TagOrangeBg to TagOrangeText,
                        price = ojolTier.sellingPrice,
                        subtitle = "Dipotong ${ojolTier.deductionAmount.formatRupiah()} ➔ Bersih tetap ${ojolTier.netReceived.formatRupiah()}"
                    )
                }

                // 3. Shopee / Marketplace
                if (marketplaceTier != null) {
                    ChannelMiniRow(
                        icon = "🛍️",
                        title = "Shopee / TikTok Shop",
                        tag = "Admin 7.5%",
                        tagColor = TagPurpleBg to TagPurpleText,
                        price = marketplaceTier.sellingPrice,
                        subtitle = "Dipotong ${marketplaceTier.deductionAmount.formatRupiah()} ➔ Bersih tetap ${marketplaceTier.netReceived.formatRupiah()}"
                    )
                }

                // 4. Reseller / Grosir
                if (resellerTier != null) {
                    ChannelMiniRow(
                        icon = "📦",
                        title = "Reseller / Grosir",
                        tag = "Diskon 20%",
                        tagColor = MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant,
                        price = resellerTier.sellingPrice,
                        subtitle = "Untung grosir: +${resellerTier.profitPerUnit.formatRupiah()} (di atas HPP)"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button Buka Kustomisasi & Salin
            OutlinedButton(
                onClick = onOpenDetailSheet,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "Kustomisasi Komisi & Salin Daftar Harga →",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ChannelMiniRow(
    icon: String,
    title: String,
    tag: String,
    tagColor: Pair<Color, Color>,
    price: Double,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, CardBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = icon, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = tagColor.first
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.sp
                                ),
                                color = tagColor.second,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = price.formatRupiah(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * BottomSheet Lengkap untuk Kustomisasi Saluran Distribusi, Simulator Komisi, dan Salin Teks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelPricingDetailSheet(
    productName: String,
    hppPerUnit: Double,
    baseSellingPrice: Double,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var customCommission by remember { mutableFloatStateOf(20f) }
    var customFixedFee by remember { mutableFloatStateOf(1000f) }
    var wholesaleDiscount by remember { mutableFloatStateOf(20f) }

    val tiers = remember(baseSellingPrice, hppPerUnit, customCommission, customFixedFee, wholesaleDiscount) {
        ChannelPriceCalculator.calculateAllTiers(
            productName = productName,
            hppPerUnit = hppPerUnit,
            baseSellingPrice = baseSellingPrice,
            customCommissionPercent = customCommission.toDouble(),
            wholesaleDiscountPercent = wholesaleDiscount.toDouble()
        )
    }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Strategi Harga Multi-Saluran",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Tutup",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // INFO BOX: Mengapa UMKM Jangan Salah Markup
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = TagOrangeBg,
                border = BorderStroke(1.dp, TagOrangeText.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "💡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Rumus Anti Tekor di Ojol",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = TagOrangeText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Banyak penjual salah kaprah: cuma menambah 20% ke harga offline (misal 20rb jadi 24rb). Saat dipotong 20% oleh GoFood/Grab, sisa hanya 19.200 (rugi 800/porsi). Dengan sistem ini, harga otomatis dihitung 25.000 sehingga setelah dipotong 20%, uang masuk ke kas Anda tetap utuh 20.000!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // DAFTAR RINCIAN LENGKAP TIAP SALURAN
            Text(
                text = "Rincian Tiap Saluran Penjualan",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            tiers.forEach { tier ->
                ChannelDetailCard(tier = tier)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SIMULATOR CUSTOM KOMISI
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, CardBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎛️ Simulator Saluran Lain (Custom)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Persentase Komisi:",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${customCommission.toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = customCommission,
                        onValueChange = { customCommission = it },
                        valueRange = 0f..40f,
                        steps = 39,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    val customPrice = ChannelPriceCalculator.calculateOnlineMarkupPrice(
                        targetNetPrice = baseSellingPrice,
                        commissionPercent = customCommission.toDouble(),
                        fixedFee = customFixedFee.toDouble()
                    )
                    val customCut = (customPrice * (customCommission / 100.0)) + customFixedFee

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Harga Pasang Ideal",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = customPrice.formatRupiah(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Potongan: ${customCut.formatRupiah()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Masuk Kas: ${(customPrice - customCut).formatRupiah()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = TagGreenText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ACTION BUTTON: SALIN KE WHATSAPP
            Button(
                onClick = {
                    val shareText = ChannelPriceCalculator.formatShareableText(productName, tiers)
                    clipboardManager.setText(AnnotatedString(shareText))
                    Toast.makeText(context, "Daftar harga berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salin Daftar Harga ke WhatsApp",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ChannelDetailCard(tier: ChannelTierPricing) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, CardBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tier.channelName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = tier.note,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = tier.sellingPrice.formatRupiah(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (tier.category != ChannelCategory.OFFLINE) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = OutlineLight.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Potongan: ${tier.deductionAmount.formatRupiah()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Diterima Kas: ${tier.netReceived.formatRupiah()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        ),
                        color = TagGreenText
                    )
                    Text(
                        text = "Laba: +${tier.profitPerUnit.formatRupiah()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Dialog / Sheet Kalkulator Cepat Markup Online (Dapat diakses dari Home Dashboard kapan saja)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMarkupCalculatorSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var offlinePriceInput by remember { mutableStateOf("20000") }
    var selectedChannelIndex by remember { mutableIntStateOf(0) }
    var customCommissionInput by remember { mutableStateOf("20") }
    var includeAdminFee by remember { mutableStateOf(true) }

    val offlinePrice = offlinePriceInput.toDoubleOrNull() ?: 0.0

    val channels = listOf(
        Triple("GoFood & GrabFood", 20.0, if (includeAdminFee) 1000.0 else 0.0),
        Triple("ShopeeFood", 20.0, 0.0),
        Triple("TikTok Shop", 8.5, 0.0),
        Triple("Shopee Star", 6.5, if (includeAdminFee) 1000.0 else 0.0),
        Triple("Tokopedia", 6.5, 0.0),
        Triple("Custom %", (customCommissionInput.toDoubleOrNull() ?: 20.0), if (includeAdminFee) 1000.0 else 0.0)
    )

    val currentChannel = channels[selectedChannelIndex]
    val onlinePrice = ChannelPriceCalculator.calculateOnlineMarkupPrice(
        targetNetPrice = offlinePrice,
        commissionPercent = currentChannel.second,
        fixedFee = currentChannel.third
    )
    val cutAmount = (onlinePrice * (currentChannel.second / 100.0)) + currentChannel.third
    val netReceived = onlinePrice - cutAmount

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kalkulator Markup Ojol & Olshop",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Hitung harga pasang aplikasi agar keuntungan tidak tekor",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Tutup",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Target Uang Bersih (Harga Dine-in / Offline)
            RupiahInputField(
                value = offlinePriceInput,
                onValueChange = { offlinePriceInput = it },
                label = "Harga Bersih Yang Ingin Diterima (Offline / Dine-in)",
                placeholder = "0",
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pilih Platform / Channel
            Text(
                text = "Pilih Aplikasi Penjualan",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                channels.chunked(2).forEachIndexed { rowIdx, pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEachIndexed { colIdx, item ->
                            val index = (rowIdx * 2) + colIdx
                            val isSelected = selectedChannelIndex == index
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else CardBorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedChannelIndex = index }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = "${item.first} (${item.second}%)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (selectedChannelIndex == 5) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = customCommissionInput,
                    onValueChange = { customCommissionInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Persentase Komisi (%)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // HASIL REKOMENDASI HARGA (BENTO CARD HERO)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HARGA PASANG DI APLIKASI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = onlinePrice.formatRupiah(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Potongan Aplikasi",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "-${cutAmount.formatRupiah()}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Uang Masuk ke Anda",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = netReceived.formatRupiah(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                ),
                                color = TagGreenText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action: Salin Nominal Harga
            Button(
                onClick = {
                    val textToCopy = "Harga ${currentChannel.first}: ${onlinePrice.formatRupiah()} (Terima bersih: ${netReceived.formatRupiah()})"
                    clipboardManager.setText(AnnotatedString(textToCopy))
                    Toast.makeText(context, "Harga ${onlinePrice.formatRupiah()} disalin!", Toast.LENGTH_SHORT).show()
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
                Text(
                    text = "Salin Harga (${onlinePrice.formatRupiah()})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
