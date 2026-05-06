package com.seal.hppcalculator.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.seal.hppcalculator.R

@Composable
fun SplashScreen(nextDestination: String, onTimeout: (String) -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000L) // Delay 2 detik
        onTimeout(nextDestination)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Background warna putih
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = "Splash Screen Logo",
            modifier = Modifier.size(240.dp)
        )
    }
}
