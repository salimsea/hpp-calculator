package com.seal.hppcalculator.data.model

import java.util.Locale
import kotlin.math.ceil

/**
 * Model data dan utilitas perhitungan Saluran Distribusi & Komisi Penjualan
 * (Milestone 2 - SuperApp Kalkulator HPP & Finansial UMKM)
 */

enum class ChannelCategory {
    OFFLINE,
    ONLINE_DELIVERY,
    MARKETPLACE,
    WHOLESALE,
    CUSTOM
}

data class ChannelPreset(
    val id: String,
    val name: String,
    val category: ChannelCategory,
    val defaultCommissionPercent: Double,
    val defaultFixedFee: Double = 0.0,
    val badge: String,
    val note: String
)

object ChannelPresets {
    val ALL = listOf(
        ChannelPreset(
            id = "offline",
            name = "Dine-in / Toko Fisik",
            category = ChannelCategory.OFFLINE,
            defaultCommissionPercent = 0.0,
            defaultFixedFee = 0.0,
            badge = "Toko",
            note = "Penjualan langsung ke pelanggan tanpa komisi aplikasi."
        ),
        ChannelPreset(
            id = "gofood_grab",
            name = "GoFood / GrabFood",
            category = ChannelCategory.ONLINE_DELIVERY,
            defaultCommissionPercent = 20.0,
            defaultFixedFee = 1000.0,
            badge = "Ojol 20%",
            note = "Komisi merchant 20% + biaya layanan sistem (~Rp 1.000)."
        ),
        ChannelPreset(
            id = "shopeefood",
            name = "ShopeeFood",
            category = ChannelCategory.ONLINE_DELIVERY,
            defaultCommissionPercent = 20.0,
            defaultFixedFee = 0.0,
            badge = "Ojol 20%",
            note = "Standar bagi hasil merchant online 20%."
        ),
        ChannelPreset(
            id = "tiktok_shop",
            name = "TikTok Shop",
            category = ChannelCategory.MARKETPLACE,
            defaultCommissionPercent = 8.5,
            defaultFixedFee = 0.0,
            badge = "E-Commerce",
            note = "Biaya administrasi marketplace live & video shopping (~8.5%)."
        ),
        ChannelPreset(
            id = "shopee_seller",
            name = "Shopee (Star Seller)",
            category = ChannelCategory.MARKETPLACE,
            defaultCommissionPercent = 6.5,
            defaultFixedFee = 1000.0,
            badge = "E-Commerce",
            note = "Biaya administrasi non-gratis ongkir / star seller (~6.5% - 8.5%)."
        ),
        ChannelPreset(
            id = "tokopedia",
            name = "Tokopedia",
            category = ChannelCategory.MARKETPLACE,
            defaultCommissionPercent = 6.5,
            defaultFixedFee = 0.0,
            badge = "E-Commerce",
            note = "Biaya layanan seller power merchant (~6.5%)."
        ),
        ChannelPreset(
            id = "reseller",
            name = "Reseller / Grosir",
            category = ChannelCategory.WHOLESALE,
            defaultCommissionPercent = 0.0,
            defaultFixedFee = 0.0,
            badge = "Grosir",
            note = "Diskon khusus pembelian partai (biasanya 15% - 25% di bawah harga eceran)."
        )
    )
}

data class ChannelTierPricing(
    val presetId: String,
    val channelName: String,
    val category: ChannelCategory,
    val commissionPercent: Double,
    val fixedFee: Double,
    val sellingPrice: Double,
    val deductionAmount: Double,
    val netReceived: Double,
    val hppPerUnit: Double,
    val profitPerUnit: Double,
    val marginPercent: Double,
    val note: String = ""
)

object ChannelPriceCalculator {
    /**
     * Menghitung harga jual yang harus dicantumkan di aplikasi agar
     * uang bersih yang diterima penjual sama dengan targetNetPrice (Harga Jual Dine-in).
     *
     * Rumus:
     * NetReceived = Price * (1 - c/100) - fixedFee = targetNetPrice
     * Price = (targetNetPrice + fixedFee) / (1 - c/100)
     *
     * Dibulatkan ke atas ke kelipatan [roundTo] (default Rp 500) agar rapi di daftar menu konsumen.
     */
    fun calculateOnlineMarkupPrice(
        targetNetPrice: Double,
        commissionPercent: Double,
        fixedFee: Double = 0.0,
        roundTo: Double = 500.0
    ): Double {
        if (targetNetPrice <= 0.0) return 0.0
        if (commissionPercent <= 0.0) return targetNetPrice + fixedFee
        if (commissionPercent >= 100.0) return targetNetPrice * 2 // Safety guard

        val rawPrice = (targetNetPrice + fixedFee) / (1.0 - (commissionPercent / 100.0))
        return if (roundTo > 0) {
            ceil(rawPrice / roundTo) * roundTo
        } else {
            rawPrice
        }
    }

    /**
     * Hitung rincian multi-tier harga untuk satu produk HPP.
     */
    fun calculateAllTiers(
        productName: String,
        hppPerUnit: Double,
        baseSellingPrice: Double,
        customCommissionPercent: Double = 20.0,
        wholesaleDiscountPercent: Double = 20.0
    ): List<ChannelTierPricing> {
        val result = mutableListOf<ChannelTierPricing>()

        // 1. Offline / Dine-in (Basis Utama)
        val offlineProfit = baseSellingPrice - hppPerUnit
        val offlineMargin = if (hppPerUnit > 0) (offlineProfit / hppPerUnit) * 100.0 else 0.0
        result.add(
            ChannelTierPricing(
                presetId = "offline",
                channelName = "Dine-in / Toko Fisik",
                category = ChannelCategory.OFFLINE,
                commissionPercent = 0.0,
                fixedFee = 0.0,
                sellingPrice = baseSellingPrice,
                deductionAmount = 0.0,
                netReceived = baseSellingPrice,
                hppPerUnit = hppPerUnit,
                profitPerUnit = offlineProfit,
                marginPercent = offlineMargin,
                note = "Harga standar tanpa potongan pihak ketiga."
            )
        )

        // 2. GoFood / GrabFood (20% + Rp 1.000)
        val ojolPrice = calculateOnlineMarkupPrice(baseSellingPrice, 20.0, 1000.0, 500.0)
        val ojolDeduction = (ojolPrice * 0.20) + 1000.0
        val ojolNet = ojolPrice - ojolDeduction
        val ojolProfit = ojolNet - hppPerUnit
        result.add(
            ChannelTierPricing(
                presetId = "gofood_grab",
                channelName = "GoFood / GrabFood",
                category = ChannelCategory.ONLINE_DELIVERY,
                commissionPercent = 20.0,
                fixedFee = 1000.0,
                sellingPrice = ojolPrice,
                deductionAmount = ojolDeduction,
                netReceived = ojolNet,
                hppPerUnit = hppPerUnit,
                profitPerUnit = ojolProfit,
                marginPercent = if (hppPerUnit > 0) (ojolProfit / hppPerUnit) * 100.0 else 0.0,
                note = "Komisi 20% + biaya layanan Rp 1.000 terlindungi penuh."
            )
        )

        // 3. ShopeeFood (20%)
        val shopeeFoodPrice = calculateOnlineMarkupPrice(baseSellingPrice, 20.0, 0.0, 500.0)
        val shopeeFoodDeduction = shopeeFoodPrice * 0.20
        val shopeeFoodNet = shopeeFoodPrice - shopeeFoodDeduction
        val shopeeFoodProfit = shopeeFoodNet - hppPerUnit
        result.add(
            ChannelTierPricing(
                presetId = "shopeefood",
                channelName = "ShopeeFood",
                category = ChannelCategory.ONLINE_DELIVERY,
                commissionPercent = 20.0,
                fixedFee = 0.0,
                sellingPrice = shopeeFoodPrice,
                deductionAmount = shopeeFoodDeduction,
                netReceived = shopeeFoodNet,
                hppPerUnit = hppPerUnit,
                profitPerUnit = shopeeFoodProfit,
                marginPercent = if (hppPerUnit > 0) (shopeeFoodProfit / hppPerUnit) * 100.0 else 0.0,
                note = "Komisi 20% dibulatkan ke Rp 500 terdekat."
            )
        )

        // 4. TikTok Shop (8.5%)
        val tiktokPrice = calculateOnlineMarkupPrice(baseSellingPrice, 8.5, 0.0, 500.0)
        val tiktokDeduction = tiktokPrice * 0.085
        val tiktokNet = tiktokPrice - tiktokDeduction
        val tiktokProfit = tiktokNet - hppPerUnit
        result.add(
            ChannelTierPricing(
                presetId = "tiktok_shop",
                channelName = "TikTok Shop",
                category = ChannelCategory.MARKETPLACE,
                commissionPercent = 8.5,
                fixedFee = 0.0,
                sellingPrice = tiktokPrice,
                deductionAmount = tiktokDeduction,
                netReceived = tiktokNet,
                hppPerUnit = hppPerUnit,
                profitPerUnit = tiktokProfit,
                marginPercent = if (hppPerUnit > 0) (tiktokProfit / hppPerUnit) * 100.0 else 0.0,
                note = "Potongan admin live & video shopping (~8.5%)."
            )
        )

        // 5. Shopee / Tokopedia (6.5% - 8.5%)
        val marketplacePrice = calculateOnlineMarkupPrice(baseSellingPrice, 7.5, 1000.0, 500.0)
        val marketplaceDeduction = (marketplacePrice * 0.075) + 1000.0
        val marketplaceNet = marketplacePrice - marketplaceDeduction
        val marketplaceProfit = marketplaceNet - hppPerUnit
        result.add(
            ChannelTierPricing(
                presetId = "marketplace",
                channelName = "Shopee / Tokopedia",
                category = ChannelCategory.MARKETPLACE,
                commissionPercent = 7.5,
                fixedFee = 1000.0,
                sellingPrice = marketplacePrice,
                deductionAmount = marketplaceDeduction,
                netReceived = marketplaceNet,
                hppPerUnit = hppPerUnit,
                profitPerUnit = marketplaceProfit,
                marginPercent = if (hppPerUnit > 0) (marketplaceProfit / hppPerUnit) * 100.0 else 0.0,
                note = "Biaya admin marketplace seller reguler & gratis ongkir."
            )
        )

        // 6. Reseller / Grosir (-wholesaleDiscountPercent %)
        val wholesaleDiscount = (wholesaleDiscountPercent / 100.0).coerceIn(0.05, 0.50)
        val rawWholesalePrice = baseSellingPrice * (1.0 - wholesaleDiscount)
        // Pastikan harga grosir tidak di bawah HPP
        val wholesalePrice = if (rawWholesalePrice > hppPerUnit) {
            ceil(rawWholesalePrice / 500.0) * 500.0
        } else {
            ceil((hppPerUnit * 1.1) / 500.0) * 500.0 // Minimal margin 10%
        }
        val wholesaleProfit = wholesalePrice - hppPerUnit
        result.add(
            ChannelTierPricing(
                presetId = "reseller",
                channelName = "Grosir / Reseller (-${wholesaleDiscountPercent.toInt()}%)",
                category = ChannelCategory.WHOLESALE,
                commissionPercent = 0.0,
                fixedFee = 0.0,
                sellingPrice = wholesalePrice,
                deductionAmount = baseSellingPrice - wholesalePrice,
                netReceived = wholesalePrice,
                hppPerUnit = hppPerUnit,
                profitPerUnit = wholesaleProfit,
                marginPercent = if (hppPerUnit > 0) (wholesaleProfit / hppPerUnit) * 100.0 else 0.0,
                note = "Harga khusus mitra/partai dengan tetap untung di atas HPP."
            )
        )

        return result
    }

    /**
     * Format shareable WhatsApp text list
     */
    fun formatShareableText(
        productName: String,
        tiers: List<ChannelTierPricing>
    ): String {
        val sb = StringBuilder()
        sb.append("📋 *DAFTAR HARGA JUAL RESMI*\n")
        sb.append("🏷️ *Produk:* $productName\n")
        sb.append("───────────────────────\n")
        tiers.forEach { tier ->
            val icon = when (tier.category) {
                ChannelCategory.OFFLINE -> "🍽️"
                ChannelCategory.ONLINE_DELIVERY -> "🛵"
                ChannelCategory.MARKETPLACE -> "🛍️"
                ChannelCategory.WHOLESALE -> "📦"
                ChannelCategory.CUSTOM -> "⚡"
            }
            sb.append("$icon *${tier.channelName}:* ${tier.sellingPrice.formatRupiahString()}\n")
            if (tier.category == ChannelCategory.ONLINE_DELIVERY || tier.category == ChannelCategory.MARKETPLACE) {
                sb.append("   _(Harga aman terpotong komisi aplikasi)_\n")
            } else if (tier.category == ChannelCategory.WHOLESALE) {
                sb.append("   _(Harga grosir per unit)_\n")
            }
        }
        sb.append("───────────────────────\n")
        sb.append("✨ Dihitung aman dengan *SuperApp Kalkulator HPP UMKM*")
        return sb.toString()
    }
}

private fun Double.formatRupiahString(): String {
    val formatted = String.format(Locale("in", "ID"), "%,d", this.toLong())
    return "Rp $formatted"
}
