package com.nexgen.fitpulse.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.common.Icon
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nexgen.fitpulse.R
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel

@Composable
fun YourExperienceScreen(navController: NavController, viewModel: UserSelectionViewModel) {
    // State variable for selected experience level
    val selectedExperience = viewModel.selectedExperience.value
    val context = LocalContext.current

    Box(
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
            ),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Choose your Fitness Level",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.roboto_bold)),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select your current fitness level to get started with personalized workouts and goals.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.LightGray,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Beginner Button
            GoalButton(
                text = "Beginner",
                isSelected = selectedExperience == "Beginner",
                onClick = { viewModel.setSelectedExperience("Beginner") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Intermediate Button
            GoalButton(
                text = "Intermediate",
                isSelected = selectedExperience == "Intermediate",
                onClick = { viewModel.setSelectedExperience("Intermediate") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Advanced Button
            GoalButton(
                text = "Advanced",
                isSelected = selectedExperience == "Advanced",
                onClick = { viewModel.setSelectedExperience("Advanced") }
            )

            Spacer(modifier = Modifier.weight(1f)) // Push the Next button to the bottom

            // Next Button
            Button(
                onClick = {
                    if (selectedExperience == null) {
                        Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate("your_target_area")
                    }
                },
                enabled = selectedExperience != null, // Button is only enabled if an option is selected
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedExperience != null) Color(0xFFD9D9D9) else Color.Transparent,
                    contentColor = if (selectedExperience != null) Color(0xFF025D93) else Color.White,
                    disabledContentColor = Color.White // White text when disabled
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp) // Flat button without elevation
            ) {
                Text(
                    text = "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Bottom padding
        }
    }
}



