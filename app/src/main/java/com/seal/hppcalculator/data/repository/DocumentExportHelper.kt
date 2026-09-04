package com.seal.hppcalculator.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.seal.hppcalculator.data.model.CashTransaction
import com.seal.hppcalculator.data.model.ChannelPriceCalculator
import com.seal.hppcalculator.data.model.ProductCost
import com.seal.hppcalculator.ui.screen.formatRupiah
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utilitas untuk Milestone 4:
 * - Ekspor Riwayat HPP ke CSV / Excel
 * - Ekspor Buku Kas ke CSV / Excel
 * - Standarisasi SOP Dapur Karyawan (Tanpa Harga Modal)
 * - Laporan Manajerial Pemilik (Owner Mode)
 */
object DocumentExportHelper {

    /**
     * Ekspor seluruh produk HPP ke file CSV spreadsheet
     */
    fun exportProductsToCsv(context: Context, products: List<ProductCost>): File? {
        return try {
            val docsDir = File(context.cacheDir, "docs").apply { mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(docsDir, "Data_HPP_UMKM_$timeStamp.csv")

            val fos = FileOutputStream(file)
            val osw = OutputStreamWriter(fos, StandardCharsets.UTF_8)

            // UTF-8 BOM agar terbaca sempurna di Microsoft Excel Windows tanpa karakter aneh
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

            // Header Kolom
            osw.write(
                "\"No\",\"Nama Produk\",\"Kategori\",\"Target Produksi (Unit)\",\"Total Bahan (Rp)\"," +
                        "\"Kemasan (Rp)\",\"Tenaga Kerja (Rp)\",\"Overhead (Rp)\",\"Total Modal (Rp)\"," +
                        "\"HPP per Unit (Rp)\",\"Margin (%)\",\"Laba per Unit (Rp)\",\"Harga Jual Offline (Rp)\"," +
                        "\"Harga GoFood/Grab (Rp)\",\"Harga Marketplace (Rp)\"\n"
            )

            products.forEachIndexed { idx, p ->
                val categoryName = when (p.category) {
                    "RETAIL" -> "Retail / Toko"
                    "SERVICE" -> "Jasa"
                    else -> "Makanan & Minuman (F&B)"
                }
                val ojolPrice = ChannelPriceCalculator.calculateOnlineMarkupPrice(p.hargaJual, 20.0, 1000.0)
                val marketplacePrice = ChannelPriceCalculator.calculateOnlineMarkupPrice(p.hargaJual, 7.5, 1000.0)

                val line = buildString {
                    append("\"${idx + 1}\",")
                    append("\"${p.productName.replace("\"", "\"\"")}\",")
                    append("\"$categoryName\",")
                    append("\"${p.productionQty.toInt()}\",")
                    append("\"${p.totalBahan.toLong()}\",")
                    append("\"${p.packagingCost.toLong()}\",")
                    append("\"${p.laborCost.toLong()}\",")
                    append("\"${p.overheadCost.toLong()}\",")
                    append("\"${p.totalModal.toLong()}\",")
                    append("\"${p.hppPerUnit.toLong()}\",")
                    append("\"${String.format(Locale("in", "ID"), "%.1f", p.marginPercent)}\",")
                    append("\"${p.profitPerUnit.toLong()}\",")
                    append("\"${p.hargaJual.toLong()}\",")
                    append("\"${ojolPrice.toLong()}\",")
                    append("\"${marketplacePrice.toLong()}\"\n")
                }
                osw.write(line)
            }

            osw.flush()
            osw.close()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Ekspor seluruh transaksi Buku Kas ke file CSV spreadsheet
     */
    fun exportCashflowToCsv(context: Context, transactions: List<CashTransaction>): File? {
        return try {
            val docsDir = File(context.cacheDir, "docs").apply { mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(docsDir, "Buku_Kas_UMKM_$timeStamp.csv")

            val fos = FileOutputStream(file)
            val osw = OutputStreamWriter(fos, StandardCharsets.UTF_8)

            // UTF-8 BOM
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

            // Header
            osw.write("\"No\",\"Tanggal & Waktu\",\"Jenis Arus Kas\",\"Pos Kategori\",\"Nominal (Rp)\",\"Catatan / Keterangan\"\n")

            val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("in", "ID"))

            transactions.forEachIndexed { idx, t ->
                val typeName = if (t.type == "IN") "Pemasukan (Cash In)" else "Pengeluaran (Cash Out)"
                val formattedDate = dateFormat.format(Date(t.date))
                val titleOrNotes = t.title.ifEmpty { t.notes }

                val line = buildString {
                    append("\"${idx + 1}\",")
                    append("\"$formattedDate\",")
                    append("\"$typeName\",")
                    append("\"${t.category}\",")
                    append("\"${t.amount.toLong()}\",")
                    append("\"${titleOrNotes.replace("\"", "\"\"")}\"\n")
                }
                osw.write(line)
            }

            osw.flush()
            osw.close()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Buka Android Share Sheet untuk membagikan file (CSV, Dokumen, dll)
     */
    fun shareExportedFile(context: Context, file: File, mimeType: String = "text/csv", chooserTitle: String = "Bagikan File Excel/CSV") {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, file.name)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, chooserTitle))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Format Teks SOP Dapur Karyawan (100% RAHASIA MODAL & LABA DISEMBUNYIKAN)
     */
    fun generateKitchenSopText(product: ProductCost, businessName: String = ""): String {
        val sb = StringBuilder()
        sb.append("📋 *KARTU STANDAR OPERASIONAL PROSEDUR (SOP DAPUR)*\n")
        if (businessName.isNotBlank()) {
            sb.append("🏬 $businessName\n")
        }
        sb.append("🏷️ *Menu:* ${product.productName}\n")
        sb.append("📦 *Standar Porsi Batch:* ${product.productionQty.toInt()} Porsi/Unit\n")
        sb.append("───────────────────────\n")
        sb.append("🍳 *TAKARAN RESEP BAKU:*\n")

        if (product.ingredients.isEmpty()) {
            sb.append("• (Belum ada rincian bahan baku)\n")
        } else {
            product.ingredients.forEachIndexed { idx, ing ->
                sb.append("${idx + 1}. *${ing.name}*: ${ing.usedQty} ${ing.unit}\n")
            }
        }

        sb.append("───────────────────────\n")
        sb.append("📌 *STANDAR PENYAJIAN & KUALITAS:*\n")
        sb.append("• Pastikan kebersihan alat & bahan sebelum memulai produksi.\n")
        sb.append("• Gunakan timbangan dan takaran persis sesuai takaran di atas.\n")
        sb.append("• Jaga konsistensi rasa dan kerapihan kemasan saat disajikan.\n")
        sb.append("───────────────────────\n")
        sb.append("🔒 _Dokumen internal dapur. Hak Cipta dilindungi._")
        return sb.toString()
    }

    /**
     * Format Teks Laporan Manajerial Pemilik Lengkap (Owner Mode)
     */
    fun generateOwnerReportText(product: ProductCost, businessName: String = ""): String {
        val ojolPrice = ChannelPriceCalculator.calculateOnlineMarkupPrice(product.hargaJual, 20.0, 1000.0)
        val marketplacePrice = ChannelPriceCalculator.calculateOnlineMarkupPrice(product.hargaJual, 7.5, 1000.0)

        val sb = StringBuilder()
        sb.append("📊 *LAPORAN ANALISIS HPP & MARGIN FINANSIAL (OWNER)*\n")
        if (businessName.isNotBlank()) {
            sb.append("🏢 *Usaha:* $businessName\n")
        }
        sb.append("🏷️ *Produk:* ${product.productName}\n")
        sb.append("📦 *Target Produksi:* ${product.productionQty.toInt()} Unit\n")
        sb.append("───────────────────────\n")
        sb.append("💰 *STRUKTUR MODAL PRODUKSI:*\n")
        sb.append("• Bahan Baku: ${product.totalBahan.formatRupiah()}\n")
        if (product.category == "FNB") {
            sb.append("• Kemasan: ${product.packagingCost.formatRupiah()}\n")
        }
        sb.append("• Upah Kerja: ${product.laborCost.formatRupiah()}\n")
        sb.append("• Overhead / Operasional: ${product.overheadCost.formatRupiah()}\n")
        sb.append("👉 *Total Modal Batch:* ${product.totalModal.formatRupiah()}\n")
        sb.append("───────────────────────\n")
        sb.append("🎯 *HARGA & MARGIN:*\n")
        sb.append("• HPP per Unit: *${product.hppPerUnit.formatRupiah()}*\n")
        sb.append("• Target Margin: *${String.format(Locale("in", "ID"), "%.1f", product.marginPercent)}%*\n")
        sb.append("• Keuntungan per Unit: *+${product.profitPerUnit.formatRupiah()}*\n")
        sb.append("• Harga Jual Offline: *${product.hargaJual.formatRupiah()}*\n")
        sb.append("───────────────────────\n")
        sb.append("🛵 *REKOMENDASI HARGA ONLINE:*\n")
        sb.append("• GoFood / GrabFood (20%): *${ojolPrice.formatRupiah()}*\n")
        sb.append("• Shopee / TikTok Shop (7.5%): *${marketplacePrice.formatRupiah()}*\n")
        sb.append("───────────────────────\n")
        sb.append("✨ Dihitung dengan *SuperApp Kalkulator HPP UMKM*")
        return sb.toString()
    }
}
