package com.seal.hppcalculator.ui.screen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.seal.hppcalculator.viewmodel.HppViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: HppViewModel,
    productId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val draft by viewModel.currentDraft.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    var showMarginSheet by remember { mutableStateOf(false) }
    var simulatedMargin by remember(draft.marginPercent) { mutableFloatStateOf(draft.marginPercent.toFloat()) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Kalkulasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        coroutineScope.launch {
                            try {
                                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                shareBitmap(context, bitmap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { 
                        viewModel.deleteProduct(productId)
                        onNavigateToHome()
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { showMarginSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("Simulasi Margin", fontSize = MaterialTheme.typography.titleMedium.fontSize, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                }
        ) {
            // Header Hero Section
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Filled.List, contentDescription = null, tint = Color.White, modifier = Modifier.padding(16.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = draft.productName.ifEmpty { "Produk Tanpa Nama" }, 
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Rekomendasi Harga Jual", 
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = draft.hargaJual.formatRupiah(), 
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Info Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("HPP per Unit", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(draft.hppPerUnit.formatRupiah(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            val formattedMargin = String.format(java.util.Locale("in", "ID"), "%.2f", draft.marginPercent)
                            Text("Margin ($formattedMargin%)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(draft.profitPerUnit.formatRupiah(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant ?: Color(0xFFE5E7EB))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Target Produksi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${draft.productionQty.toInt()} Unit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // Breakdown Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-24).dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Rincian Modal Produksi", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ResultRow("Bahan Baku", draft.totalBahan.formatRupiah())
                    ResultRow("Kemasan", draft.packagingCost.formatRupiah())
                    ResultRow("Tenaga Kerja", draft.laborCost.formatRupiah())
                    ResultRow("Overhead", draft.overheadCost.formatRupiah())
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        ResultRow(
                            "Total Modal", 
                            draft.totalModal.formatRupiah(), 
                            isBold = true,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }

    if (showMarginSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMarginSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("Simulasi Margin", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                
                val simulatedHargaJual = draft.hppPerUnit * (1 + simulatedMargin / 100)
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Harga Jual Rekomendasi", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Text(
                            simulatedHargaJual.formatRupiah(), 
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Pilih Margin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    val formattedSimulatedMargin = String.format(java.util.Locale("in", "ID"), "%.2f", simulatedMargin)
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("$formattedSimulatedMargin%", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
                
                Slider(
                    value = simulatedMargin,
                    onValueChange = { simulatedMargin = it },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { showMarginSheet = false },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            viewModel.updateMarginAndSave(simulatedMargin.toDouble())
                            showMarginSheet = false
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Update", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ResultRow(
    label: String, 
    value: String, 
    isBold: Boolean = false, 
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier.padding(vertical = 8.dp),
    valueSize: androidx.compose.ui.text.TextStyle? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isBold) color else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value, 
            style = valueSize ?: if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge, 
            color = color, 
            fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold
        )
    }
}

fun shareBitmap(context: Context, bitmap: Bitmap) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "hpp_result.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Hasil HPP"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
