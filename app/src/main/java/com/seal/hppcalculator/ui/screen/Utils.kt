package com.seal.hppcalculator.ui.screen

import java.text.NumberFormat
import java.util.Locale

fun Double.formatRupiah(): String {
    val localeID = Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    numberFormat.maximumFractionDigits = 0
    return numberFormat.format(this)
}
