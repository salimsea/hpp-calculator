package com.seal.hppcalculator.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
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
import com.seal.hppcalculator.data.model.ChannelPriceCalculator
import com.seal.hppcalculator.ui.components.RupiahInputField
import com.seal.hppcalculator.ui.theme.*

/**
 * Full-page screen untuk Kalkulator Markup Harga Ojol & Olshop.
 * Menggantikan QuickMarkupCalculatorSheet (ModalBottomSheet).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkupOjolScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var offlinePriceInput by remember { mutableStateOf("20000") }
    var selectedChannelIndex by remember { mutableIntStateOf(0) }
    var customCommissionInput by remember { mutableStateOf("20") }
    var includeAdminFee by remember { mutableStateOf(true) }

    val channels = listOf(
        Triple("GoFood", 0.20, 1000.0),
        Triple("GrabFood", 0.20, 1000.0),
        Triple("ShopeeFood", 0.20, 1000.0),
        Triple("Kustom", (customCommissionInput.toDoubleOrNull() ?: 20.0) / 100.0, 0.0)
    )

    val currentChannel = channels[selectedChannelIndex]
    val offlinePrice = offlinePriceInput.toDoubleOrNull() ?: 0.0
    val commissionRate = currentChannel.second
    val fixedFee = if (includeAdminFee) currentChannel.third else 0.0

    val onlinePrice = ChannelPriceCalculator.calculateOnlineMarkupPrice(
        targetNetPrice = offlinePrice,
        commissionPercent = commissionRate * 100,
        fixedFee = fixedFee
    )
    val cutAmount = (onlinePrice * commissionRate) + fixedFee
    val netReceived = onlinePrice - cutAmount

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kalkulator Markup Ojol & Olshop",
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
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salin Harga (${onlinePrice.formatRupiah()})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
