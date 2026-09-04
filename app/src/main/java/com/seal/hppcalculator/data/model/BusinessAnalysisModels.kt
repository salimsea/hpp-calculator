package com.seal.hppcalculator.data.model

import java.util.Locale
import kotlin.math.ceil

/**
 * Model Domain & Logika Perhitungan Milestone 3:
 * - Kalkulator BEP (Break-Even Point / Titik Impas Usaha)
 * - Goal Planner (Target Profit Bulanan & Kuota Harian)
 * - CapEx Planner (Perencana Modal Awal & Payback Period)
 */

data class FixedExpense(
    val name: String,
    val amount: Double
)

object DefaultFixedExpenses {
    val TEMPLATES = listOf(
        FixedExpense("Sewa Tempat / Kios", 1500000.0),
        FixedExpense("Gaji Karyawan", 2000000.0),
        FixedExpense("Listrik, Air & Gas", 500000.0),
        FixedExpense("Internet & Pulsa", 150000.0),
        FixedExpense("Kebersihan & Iuran", 100000.0)
    )
}

data class BepResult(
    val totalFixedCostMonthly: Double,
    val sellingPrice: Double,
    val hppPerUnit: Double,
    val contributionMargin: Double,
    val workingDaysMonthly: Int = 30,
    val bepUnitsMonthly: Int,
    val bepUnitsDaily: Int,
    val bepRevenueMonthly: Double,
    val bepRevenueDaily: Double
)

data class ProfitGoalResult(
    val targetMonthlyProfit: Double,
    val totalFixedCostMonthly: Double,
    val sellingPrice: Double,
    val hppPerUnit: Double,
    val contributionMargin: Double,
    val workingDaysMonthly: Int = 30,
    val targetUnitsMonthly: Int,
    val targetUnitsDaily: Int,
    val targetRevenueMonthly: Double,
    val targetRevenueDaily: Double,
    val totalProfitGenerated: Double
)

data class CapexItem(
    val name: String,
    val cost: Double
)

data class CapexResult(
    val totalCapex: Double,
    val monthlyNetProfitEstimate: Double,
    val paybackPeriodMonths: Double,
    val items: List<CapexItem>
)

object BusinessAnalysisCalculator {

    /**
     * Hitung Break-Even Point (Titik Impas) Operasional Bulanan & Harian.
     *
     * Formula:
     * Margin Kontribusi = Harga Jual - HPP
     * BEP (Unit) = Total Biaya Tetap / Margin Kontribusi
     */
    fun calculateBep(
        totalFixedCostMonthly: Double,
        sellingPrice: Double,
        hppPerUnit: Double,
        workingDays: Int = 30
    ): BepResult {
        val contributionMargin = (sellingPrice - hppPerUnit).coerceAtLeast(0.0)
        val safeDays = workingDays.coerceIn(1, 31)

        val bepUnitsMonthly = if (contributionMargin > 0) {
            ceil(totalFixedCostMonthly / contributionMargin).toInt()
        } else 0

        val bepUnitsDaily = if (safeDays > 0) {
            ceil(bepUnitsMonthly.toDouble() / safeDays).toInt()
        } else 0

        val bepRevenueMonthly = bepUnitsMonthly * sellingPrice
        val bepRevenueDaily = bepUnitsDaily * sellingPrice

        return BepResult(
            totalFixedCostMonthly = totalFixedCostMonthly,
            sellingPrice = sellingPrice,
            hppPerUnit = hppPerUnit,
            contributionMargin = contributionMargin,
            workingDaysMonthly = safeDays,
            bepUnitsMonthly = bepUnitsMonthly,
            bepUnitsDaily = bepUnitsDaily,
            bepRevenueMonthly = bepRevenueMonthly,
            bepRevenueDaily = bepRevenueDaily
        )
    }

    /**
     * Goal Planner: Hitung kuota penjualan harian agar tercapai target keuntungan bersih tertentu.
     *
     * Formula:
     * Target Penjualan (Unit) = (Total Biaya Tetap + Target Laba Bersih) / Margin Kontribusi
     */
    fun calculateProfitGoal(
        targetMonthlyProfit: Double,
        totalFixedCostMonthly: Double,
        sellingPrice: Double,
        hppPerUnit: Double,
        workingDays: Int = 30
    ): ProfitGoalResult {
        val contributionMargin = (sellingPrice - hppPerUnit).coerceAtLeast(0.0)
        val safeDays = workingDays.coerceIn(1, 31)
        val totalNeeded = totalFixedCostMonthly + targetMonthlyProfit

        val targetUnitsMonthly = if (contributionMargin > 0) {
            ceil(totalNeeded / contributionMargin).toInt()
        } else 0

        val targetUnitsDaily = if (safeDays > 0) {
            ceil(targetUnitsMonthly.toDouble() / safeDays).toInt()
        } else 0

        val targetRevenueMonthly = targetUnitsMonthly * sellingPrice
        val targetRevenueDaily = targetUnitsDaily * sellingPrice
        val totalProfitGenerated = (targetUnitsMonthly * contributionMargin) - totalFixedCostMonthly

        return ProfitGoalResult(
            targetMonthlyProfit = targetMonthlyProfit,
            totalFixedCostMonthly = totalFixedCostMonthly,
            sellingPrice = sellingPrice,
            hppPerUnit = hppPerUnit,
            contributionMargin = contributionMargin,
            workingDaysMonthly = safeDays,
            targetUnitsMonthly = targetUnitsMonthly,
            targetUnitsDaily = targetUnitsDaily,
            targetRevenueMonthly = targetRevenueMonthly,
            targetRevenueDaily = targetRevenueDaily,
            totalProfitGenerated = totalProfitGenerated
        )
    }

    /**
     * Perencanaan Modal Awal Usaha (CapEx) & Payback Period (Waktu Balik Modal).
     *
     * Payback Period (Bulan) = Total Modal Awal / Perkiraan Laba Bersih Bulanan
     */
    fun calculateCapex(
        items: List<CapexItem>,
        emergencyFundMonths: Int = 3,
        monthlyOperatingCost: Double = 0.0,
        monthlyNetProfitEstimate: Double = 3000000.0
    ): CapexResult {
        val rawItemsTotal = items.sumOf { it.cost }
        val emergencyTotal = monthlyOperatingCost * emergencyFundMonths
        val totalCapex = rawItemsTotal + emergencyTotal

        val paybackMonths = if (monthlyNetProfitEstimate > 0) {
            totalCapex / monthlyNetProfitEstimate
        } else 0.0

        return CapexResult(
            totalCapex = totalCapex,
            monthlyNetProfitEstimate = monthlyNetProfitEstimate,
            paybackPeriodMonths = paybackMonths,
            items = items
        )
    }
}
