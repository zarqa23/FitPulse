package com.nexgen.fitpulse.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nexgen.fitpulse.MainActivity
import com.nexgen.fitpulse.ui.theme.FitPulseTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitPulseTheme {
                SplashScreen(onSplashComplete = {
                    // Navigate to the main activity after the splash screen
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                })
            }
        }
    }
}
