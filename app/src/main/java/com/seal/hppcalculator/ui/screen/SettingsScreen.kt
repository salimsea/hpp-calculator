package com.seal.hppcalculator.ui.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.seal.hppcalculator.R
import com.seal.hppcalculator.data.repository.DocumentExportHelper
import com.seal.hppcalculator.ui.theme.*
import com.seal.hppcalculator.util.CashflowReminderManager
import com.seal.hppcalculator.viewmodel.HppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HppViewModel,
    onResetAppToOnboarding: () -> Unit = {}
) {
    val context = LocalContext.current
    val history by viewModel.history.collectAsState()

    var isReminderEnabled by remember {
        mutableStateOf(CashflowReminderManager.isReminderEnabled(context))
    }
    var reminderHour by remember {
        mutableIntStateOf(CashflowReminderManager.getReminderHour(context))
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Launcher for Android 13+ POST_NOTIFICATIONS permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isReminderEnabled = true
            CashflowReminderManager.setReminder(context, true, reminderHour)
            Toast.makeText(context, "Pengingat buku kas harian aktif!", Toast.LENGTH_SHORT).show()
        } else {
            isReminderEnabled = false
            CashflowReminderManager.setReminder(context, false)
            Toast.makeText(context, "Izin notifikasi tidak diberikan", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengaturan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    )
                },
                windowInsets = WindowInsets(0.dp),
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
            Spacer(modifier = Modifier.height(10.dp))

            // ==================== SECTION 1: NOTIFIKASI ====================
            SettingsSectionHeader(title = "Notifikasi & Pengingat")
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            SettingsIconBadge(
                                icon = Icons.Filled.Notifications,
                                bgColor = TagOrangeBg,
                                tintColor = TagOrangeText
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Pengingat Buku Kas",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Ingatkan catat rekap kas tiap hari",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isReminderEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        isReminderEnabled = true
                                        CashflowReminderManager.setReminder(context, true, reminderHour)
                                        Toast.makeText(context, "Pengingat buku kas harian aktif!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    isReminderEnabled = false
                                    CashflowReminderManager.setReminder(context, false)
                                    Toast.makeText(context, "Pengingat dimatikan", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    if (isReminderEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = CardBorderSubtle.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Pilih Jam Pengingat:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(19, 20, 21, 22).forEach { hour ->
                                val isSelected = reminderHour == hour
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else CardBorderSubtle
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            reminderHour = hour
                                            CashflowReminderManager.setReminder(context, true, hour)
                                            Toast.makeText(context, "Pengingat diatur jam $hour:00 WIB", Toast.LENGTH_SHORT).show()
                                        }
                                ) {
                                    Text(
                                        text = "$hour:00",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        FilledTonalButton(
                            onClick = {
                                CashflowReminderManager.triggerTestNotification(context)
                                Toast.makeText(context, "Notifikasi pengingat dikirim!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Uji Notifikasi Sekarang", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==================== SECTION 2: MANAJEMEN DATA ====================
            SettingsSectionHeader(title = "Manajemen Data")
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsItemRow(
                        icon = Icons.Filled.TableChart,
                        iconBg = TagGreenBg,
                        iconTint = TagGreenText,
                        title = "Ekspor Seluruh Data HPP",
                        subtitle = "Simpan semua resep ke file CSV / Excel",
                        onClick = {
                            if (history.isEmpty()) {
                                Toast.makeText(context, "Belum ada data resep HPP untuk diekspor", Toast.LENGTH_SHORT).show()
                            } else {
                                val csvFile = DocumentExportHelper.exportProductsToCsv(context, history)
                                if (csvFile != null) {
                                    DocumentExportHelper.shareExportedFile(context, csvFile, "text/csv", "Ekspor Seluruh Data HPP")
                                }
                            }
                        }
                    )

                    HorizontalDivider(
                        color = CardBorderSubtle.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    SettingsItemRow(
                        icon = Icons.Filled.DeleteForever,
                        iconBg = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                        iconTint = MaterialTheme.colorScheme.error,
                        title = "Hapus Seluruh Data",
                        subtitle = "Kosongkan semua riwayat resep HPP & buku kas",
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==================== SECTION 3: TENTANG APLIKASI ====================
            SettingsSectionHeader(title = "Tentang Aplikasi")
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsItemRow(
                        icon = Icons.Filled.Info,
                        iconBg = TagBlueBg,
                        iconTint = TagBlueText,
                        title = "Tentang Kalkulator HPP",
                        subtitle = "Versi aplikasi, visi, dan pengembang",
                        onClick = { showAboutDialog = true }
                    )

                    HorizontalDivider(
                        color = CardBorderSubtle.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    SettingsItemRow(
                        icon = Icons.Filled.Share,
                        iconBg = TagPurpleBg,
                        iconTint = TagPurpleText,
                        title = "Bagikan Aplikasi",
                        subtitle = "Bantu teman & sesama pengusaha UMKM",
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Yuk hitung HPP, margin keuntungan, target BEP impas, dan markup ojol usahamu secara praktis dengan aplikasi Kalkulator HPP & Manajemen Bisnis UMKM!"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Aplikasi"))
                        }
                    )

                    HorizontalDivider(
                        color = CardBorderSubtle.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    SettingsItemRow(
                        icon = Icons.Filled.Star,
                        iconBg = TagAmberBg,
                        iconTint = TagAmberText,
                        title = "Beri Rating di Google Play",
                        subtitle = "Dukung aplikasi ini dengan review bintang 5",
                        onClick = {
                            com.seal.hppcalculator.util.AppRatingManager.openPlayStore(context)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==================== SECTION 4: REKOMENDASI APLIKASI BISNIS ====================
            SettingsSectionHeader(title = "Rekomendasi Aplikasi Bisnis")
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsItemRow(
                        icon = Icons.Filled.PointOfSale,
                        iconBg = TagGreenBg,
                        iconTint = TagGreenText,
                        title = "Aplikasi Kasir Gratis (POS)",
                        subtitle = "Sellaris POS • Kasir penjualan & cetak struk",
                        onClick = {
                            com.seal.hppcalculator.util.AppRatingManager.openAppOnPlayStore(context, "com.sellaris.app")
                        }
                    )

                    HorizontalDivider(
                        color = CardBorderSubtle.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    SettingsItemRow(
                        icon = Icons.Filled.BusinessCenter,
                        iconBg = TagBlueBg,
                        iconTint = TagBlueText,
                        title = "Aplikasi Bisnis ERP Gratis",
                        subtitle = "Sellaris ERP • Inventori, stok, & operasional",
                        onClick = {
                            com.seal.hppcalculator.util.AppRatingManager.openAppOnPlayStore(context, "com.sellaris.erpapp")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // ==================== DIALOG KONFIRMASI HAPUS SELURUH DATA ====================
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.DeleteForever,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "Hapus Seluruh Data?",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Tindakan ini akan menghapus permanen SEMUA data resep HPP dan riwayat transaksi Buku Kas Anda. Data yang sudah dihapus tidak dapat dipulihkan.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData {
                            // Reset preferensi onboarding dan profil pengguna
                            val prefs = context.getSharedPreferences("hpp_prefs", android.content.Context.MODE_PRIVATE)
                            prefs.edit().clear().apply()

                            showDeleteDialog = false
                            Toast.makeText(context, "Seluruh data dihapus. Memulai ulang onboarding...", Toast.LENGTH_LONG).show()
                            onResetAppToOnboarding()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ya, Hapus Semua", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(22.dp)
        )
    }

    // ==================== DIALOG TENTANG APLIKASI ====================
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = null,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.il_handshake_deal),
                        contentDescription = "Tentang Aplikasi",
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(130.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Kalkulator HPP Bisnis",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Versi 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aplikasi cerdas dan mandiri untuk membantu pelaku usaha UMKM menghitung harga pokok produksi secara akurat, merancang margin laba yang sehat, menentukan harga pasang ojol, analisis target impas (BEP), dan rekap buku kas harian.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "❤️ Dibuat untuk Kemajuan UMKM Indonesia",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(22.dp)
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SettingsIconBadge(
    icon: ImageVector,
    bgColor: Color,
    tintColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier.size(38.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsItemRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            SettingsIconBadge(icon = icon, bgColor = iconBg, tintColor = iconTint)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(14.dp)
        )
    }
}
