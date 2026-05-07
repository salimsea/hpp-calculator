package com.seal.hppcalculator

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// import com.google.android.gms.ads.MobileAds  // IKLAN DIMATIKAN
import com.seal.hppcalculator.ui.AppNavigation
import com.seal.hppcalculator.ui.theme.HppCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // IKLAN DIMATIKAN SEMENTARA
        // MobileAds.initialize(this) {}
        
        val sharedPrefs = getSharedPreferences("hpp_prefs", Context.MODE_PRIVATE)
        val hasSeenOnboarding = sharedPrefs.getBoolean("hasSeenOnboarding", false)
        
        enableEdgeToEdge()
        setContent {
            HppCalculatorTheme {
                val repository = (application as com.seal.hppcalculator.HppApplication).repository
                AppNavigation(
                    repository = repository,
                    startDestination = if (hasSeenOnboarding) "home" else "onboarding",
                    onOnboardingFinished = {
                        sharedPrefs.edit().putBoolean("hasSeenOnboarding", true).apply()
                    }
                )
            }
        }
    }
}