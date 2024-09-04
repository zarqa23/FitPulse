package com.nexgen.fitnest.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nexgen.fitnest.R

@Composable
fun TargetAreasScreen(navController: NavHostController) {
    val customFontFamily = FontFamily(
        Font(R.font.robotomedium) // Replace with the name of your font file
    )
    val buttonHeight = 100.dp
    val buttonPadding = 12.dp
    val fontSize = 24.sp
    var selectedArea by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var showToast by remember { mutableStateOf(false) }

    // Gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF025D93),
                        Color(0xFF024974),
                        Color(0xFF012033),
                        Color(0xFF011C2D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween, // Space between elements to push button to the bottom
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.Start // Align text to the start (left)
            ) {
                Text(
                    "Target Your Muscle Growth",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    "Select the body part you want to focus on for muscle gain. This will help us create a plan that enhances your desired muscle group.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.weight(.2f))

            // Option Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val borderColor = Color.White // Default border color

                Button(
                    onClick = { selectedArea = "Upper Body" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedArea == "Upper Body") Color(0xFFD9D9D9) else Color.Transparent,
                        contentColor = if (selectedArea == "Upper Body") Color(0xFF025D93) else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .padding(vertical = buttonPadding)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (selectedArea == "Upper Body") Color.Transparent else borderColor
                            ), shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Text(
                        text = "Upper Body",
                        fontSize = fontSize,
                        color = if (selectedArea == "Upper Body") Color(0xFF025D93) else Color.White,
                        fontFamily = customFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { selectedArea = "Lower Body" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedArea == "Lower Body") Color(0xFFD9D9D9) else Color.Transparent,
                        contentColor = if (selectedArea == "Lower Body") Color(0xFF025D93) else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .padding(vertical = buttonPadding)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (selectedArea == "Lower Body") Color.Transparent else borderColor
                            ), shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Text(
                        text = "Lower Body",
                        fontSize = fontSize,
                        color = if (selectedArea == "Lower Body") Color(0xFF025D93) else Color.White,
                        fontFamily = customFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { selectedArea = "Full Body" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedArea == "Full Body") Color(0xFFD9D9D9) else Color.Transparent,
                        contentColor = if (selectedArea == "Full Body") Color(0xFF025D93) else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .padding(vertical = buttonPadding)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (selectedArea == "Full Body") Color.Transparent else borderColor
                            ), shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Text(
                        text = "Full Body",
                        fontSize = fontSize,
                        color = if (selectedArea == "Full Body") Color(0xFF025D93) else Color.White,
                        fontFamily = customFontFamily
                    )
                }
            }

            // Spacer to push the Next button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Next Button
            Button(
                onClick = {
                    if (selectedArea != null) {
                        sharedPreferences.edit().putString("selectedArea", selectedArea).apply()
                        navController.navigate("register") {
                            popUpTo("targetareas") { inclusive = true }
                        }
                    } else {
                        // Show Toast if no target area is selected
                        showToast = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD9D9D9),
                    contentColor = Color(0xFF025D93)
                ),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Next",
                    fontSize = fontSize,
                    fontFamily = customFontFamily
                )
            }

            // Show toast message if needed
            if (showToast) {
                LaunchedEffect(context) {
                    Toast.makeText(context, "Please select a target area.", Toast.LENGTH_SHORT).show()
                    showToast = false
                }
            }
        }
    }
}