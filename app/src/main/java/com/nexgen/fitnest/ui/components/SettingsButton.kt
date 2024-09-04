package com.nexgen.fitnest.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsButton(
    text: String,
    onClick: () -> Unit,
    isClicked: Boolean = false,
    onClickStateChange: (() -> Unit)? = null
) {
    TextButton(
        onClick = {
            onClick()
            onClickStateChange?.invoke()
        },
        modifier = Modifier
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = if (isClicked) Color(0xFF025D93) else Color.White
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = if (isClicked) Color(0xFF025D93) else Color.White,
        )
    }
}