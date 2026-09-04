package com.seal.hppcalculator.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object AppRatingManager {
    private const val PREFS_NAME = "app_rating_prefs"
    private const val KEY_ACTION_COUNT = "key_save_or_use_action_count"
    private const val KEY_HAS_RATED = "key_has_rated"
    private const val KEY_NEVER_SHOW = "key_never_show"
    private const val REQUIRED_COUNT = 3

    /**
     * Mencatat aksi penyimpanan HPP atau penggunaan resep inspirasi.
     * Mengembalikan true jika kondisi terpenuhi (misal sudah 3x) dan dialog rating layak ditampilkan.
     */
    fun incrementActionAndCheckShouldPrompt(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hasRated = prefs.getBoolean(KEY_HAS_RATED, false)
        val neverShow = prefs.getBoolean(KEY_NEVER_SHOW, false)

        if (hasRated || neverShow) {
            return false
        }

        val currentCount = prefs.getInt(KEY_ACTION_COUNT, 0) + 1
        prefs.edit().putInt(KEY_ACTION_COUNT, currentCount).apply()

        return currentCount == REQUIRED_COUNT
    }

    /**
     * Tandai bahwa pengguna sudah memberi rating / menekan tombol rating ke Google Play.
     */
    fun setHasRated(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_HAS_RATED, true).apply()
    }

    /**
     * Pengguna memilih "Nanti Saja", reset counter agar muncul lagi nanti setelah 3 aksi berikutnya.
     */
    fun setRemindLater(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_ACTION_COUNT, 0).apply()
    }

    /**
     * Pengguna menolak untuk diminta rating lagi.
     */
    fun setNeverShowAgain(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_NEVER_SHOW, true).apply()
    }

    /**
     * Buka halaman aplikasi di Google Play Store untuk memberi bintang / review.
     */
    fun openPlayStore(context: Context) {
        openAppOnPlayStore(context, context.packageName)
    }

    /**
     * Buka aplikasi tertentu berdasarkan package name di Google Play Store.
     */
    fun openAppOnPlayStore(context: Context, targetPackageName: String) {
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$targetPackageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
            context.startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$targetPackageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }
}
