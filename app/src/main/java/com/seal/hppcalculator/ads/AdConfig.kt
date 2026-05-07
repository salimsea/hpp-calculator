package com.seal.hppcalculator.ads

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
// IKLAN DIMATIKAN - IMPORT DIBAWAH DICOMMENT
// import com.google.android.gms.ads.AdError
// import com.google.android.gms.ads.AdRequest
// import com.google.android.gms.ads.AdSize
// import com.google.android.gms.ads.AdView
// import com.google.android.gms.ads.FullScreenContentCallback
// import com.google.android.gms.ads.LoadAdError
// import com.google.android.gms.ads.rewarded.RewardedAd
// import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdConfig {
    // Flag untuk menyalakan atau mematikan seluruh iklan di aplikasi
    // IKLAN DIMATIKAN
    const val ENABLE_ADS = false

    // Ganti dengan App ID milikmu di AndroidManifest.xml
    // Saat ini menggunakan Test Ad Unit ID dari Google untuk testing
    
    // Banner Test ID
    const val BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    
    // Rewarded Video Test ID
    const val REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
}

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    // IKLAN DIMATIKAN
    if (!AdConfig.ENABLE_ADS) return
    
    // AndroidView(
    //     modifier = modifier.fillMaxWidth(),
    //     factory = { context ->
    //         AdView(context).apply {
    //             setAdSize(AdSize.BANNER)
    //             adUnitId = AdConfig.BANNER_ID
    //             loadAd(AdRequest.Builder().build())
    //         }
    //     }
    // )
}

fun loadAndShowRewardedAd(context: Context, onAdDismissed: () -> Unit) {
    // IKLAN DIMATIKAN
    if (!AdConfig.ENABLE_ADS) {
        onAdDismissed()
        return
    }
    
    // val adRequest = AdRequest.Builder().build()
    // RewardedAd.load(context, AdConfig.REWARDED_ID, adRequest, object : RewardedAdLoadCallback() {
    //     override fun onAdFailedToLoad(adError: LoadAdError) {
    //         // Jika gagal load (misal tidak ada internet), langsung jalankan fungsi selanjutnya
    //         onAdDismissed()
    //     }
    //
    //     override fun onAdLoaded(rewardedAd: RewardedAd) {
    //         rewardedAd.fullScreenContentCallback = object: FullScreenContentCallback() {
    //             override fun onAdDismissedFullScreenContent() {
    //                 // Ketika iklan ditutup
    //                 onAdDismissed()
    //             }
    //
    //             override fun onAdFailedToShowFullScreenContent(adError: AdError) {
    //                 onAdDismissed()
    //             }
    //         }
    //         
    //         val activity = context as? Activity
    //         if (activity != null) {
    //             rewardedAd.show(activity) { rewardItem ->
    //                 // User mendapatkan reward (jika butuh dicatat)
    //             }
    //         } else {
    //             onAdDismissed()
    //         }
    //     }
    // })
}
