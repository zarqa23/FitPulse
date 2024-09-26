package com.nexgen.fitpulse.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nexgen.fitpulse.R
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel

@Composable
fun YourGoalsScreen(navController: NavController , viewModel: UserSelectionViewModel) {
    // State variable for selected goal
    val selectedGoal = viewModel.selectedGoal.value
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
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Identify Your Primary Fitness Goal",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontSize = 26.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Select the goal that best matches your fitness aspirations. This will help us tailor your workout plan to achieve your desired results.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.LightGray,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Cardio Button
            GoalButton(
                text = "Muscle Gain",
                isSelected = selectedGoal == "Muscle Gain",
                onClick = {  viewModel.setSelectedGoal("Muscle Gain") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Strength Button
            GoalButton(
                text = "Weight Gain",
                isSelected = selectedGoal == "Weight Gain",
                onClick = { viewModel.setSelectedGoal("Weight Gain")  }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Flexibility Button
            GoalButton(
                text = "Weight Lose",
                isSelected = selectedGoal == "Weight Lose",
                onClick = { viewModel.setSelectedGoal("Weight Lose")  }
            )

            Spacer(modifier = Modifier.weight(1f)) // Push the Next button to the bottom

            // Next Button
            Button(
                onClick = {
                    if (selectedGoal == null) {
                        Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate("your_experience")
                    }
                },
                enabled = selectedGoal != null, // Button is only enabled if an option is selected
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp)
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedGoal != null) Color(0xFFD9D9D9) else Color.Transparent, // Background when enabled
                    contentColor = if (selectedGoal != null) Color(0xFF025D93) else Color.White, // White when disabled, blue when enabled
                    disabledContentColor = Color.White // Force white text when disabled
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

@Composable
fun GoalButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFD9D9D9) else Color.Transparent
    val textColor = if (isSelected) Color(0xFF025D93) else Color.White

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .border(2.dp, Color.White, shape = RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor, // Background color for the button
            contentColor = textColor // Text color inside the button
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp) // No shadow or elevation for a flat card-like appearance
    ) {
        Text(
            text = text,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}