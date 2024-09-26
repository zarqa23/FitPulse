package com.nexgen.fitpulse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit, navController: NavController) {
    // Direct download link to Google Drive
    val privacyPolicyUrl = "https://umar-maaz.github.io/fit_pulse_privacy_policy/"

    // Remember the state of the WebView
    val webViewState = rememberWebViewState(url = privacyPolicyUrl)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF025D93),
                        Color(0xFF024974),
                        Color(0xFF011C2D)
                    )
                )
            )
    ) {
        // Top bar with a back button
        TopAppBar(
            title = { Text("Privacy Policy") },
            navigationIcon = {
                IconButton(onClick = onBackClick ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back" , tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF025D93),
                titleContentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Display WebView
        WebView(state = webViewState)
    }
}
