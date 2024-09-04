package com.nexgen.fitnest.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.nexgen.fitnest.R

@Composable
fun LogoutDialog(onDismiss: () -> Unit, onLogout: () -> Unit) {
    val customFontFamily = FontFamily(Font(R.font.robotomedium)) // Using the same font family

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Logout",
                fontFamily = customFontFamily, // Apply custom font
                color = Color.White // Consistent text color
            )
        },
        text = {
            Text(
                text = "Are you sure you want to log out?",
                fontFamily = customFontFamily, // Apply custom font
                color = Color.LightGray // Text color
            )
        },
        confirmButton = {
            TextButton(onClick = onLogout) {
                Text(
                    "Yes",
                    color = Color(0xFF025D93) // Button text color
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "No",
                    color = Color(0xFF025D93) // Button text color
                )
            }
        },
        containerColor = Color(0xFF012033) // Dialog background color
    )
}