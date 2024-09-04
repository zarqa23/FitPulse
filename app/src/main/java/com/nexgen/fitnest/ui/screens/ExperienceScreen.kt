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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nexgen.fitnest.R

@Composable
fun ExperienceScreen(navController: NavHostController) {
    val customFontFamily = FontFamily(
        Font(R.font.robotomedium) // Replace with the name of your font file
    )
    val buttonHeight = 100.dp
    val buttonPadding = 12.dp
    val fontSize = 24.sp
    var selectedExperience by remember { mutableStateOf<String?>(null) }
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
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Choose Your Fitness Level",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    "Select your current fitness level to get started with personalized workouts and goals.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Option Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val borderColor = Color.White // Default border color

                Button(
                    onClick = { selectedExperience = "Beginner" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedExperience == "Beginner") Color(0xFFD9D9D9) else Color.Transparent,
                        contentColor = if (selectedExperience == "Beginner") Color(0xFF025D93) else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .padding(vertical = buttonPadding)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (selectedExperience == "Beginner") Color.Transparent else borderColor
                            ), shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Text(
                        text = "Beginner",
                        fontSize = fontSize,
                        color = if (selectedExperience == "Beginner") Color(0xFF025D93) else Color.White,
                        fontFamily = customFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { selectedExperience = "Intermediate" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedExperience == "Intermediate") Color(0xFFD9D9D9) else Color.Transparent,
                        contentColor = if (selectedExperience == "Intermediate") Color(0xFF025D93) else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .padding(vertical = buttonPadding)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (selectedExperience == "Intermediate") Color.Transparent else borderColor
                            ), shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Text(
                        text = "Intermediate",
                        fontSize = fontSize,
                        color = if (selectedExperience == "Intermediate") Color(0xFF025D93) else Color.White,
                        fontFamily = customFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { selectedExperience = "Advanced" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedExperience == "Advanced") Color(0xFFD9D9D9) else Color.Transparent,
                        contentColor = if (selectedExperience == "Advanced") Color(0xFF025D93) else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .padding(vertical = buttonPadding)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (selectedExperience == "Advanced") Color.Transparent else borderColor
                            ), shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Text(
                        text = "Advanced",
                        fontSize = fontSize,
                        color = if (selectedExperience == "Advanced") Color(0xFF025D93) else Color.White,
                        fontFamily = customFontFamily
                    )
                }
            }

            // Spacer to push the Next button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Next Button
            Button(
                onClick = {
                    if (selectedExperience != null) {
                        sharedPreferences.edit().putString("selectedExperience", selectedExperience).apply()
                        navController.navigate("targetareas") {
                            popUpTo("experience") { inclusive = true }
                        }
                    } else {
                        // Show Toast if no experience level is selected
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
                    Toast.makeText(context, "Please select an experience level.", Toast.LENGTH_SHORT).show()
                    showToast = false
                }
            }
        }
    }
}