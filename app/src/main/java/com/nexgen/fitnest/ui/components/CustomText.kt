package com.nexgen.fitnest.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.nexgen.fitnest.R

@Composable
fun CustomText(text: String, fontSize: TextUnit = 24.sp, color: Color = Color.White, fontFamily: FontFamily) {
    val customFontFamily = FontFamily(
        Font(R.font.robotomedium) // Replace with your actual font
    )
    Text(
        text = text,
        fontFamily = customFontFamily,
        fontSize = fontSize,
        color = color
    )
}