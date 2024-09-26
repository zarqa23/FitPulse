package com.nexgen.fitpulse.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.nexgen.fitpulse.R
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel

@Composable
fun YourTargetAreaScreen(navController: NavController, viewModel: UserSelectionViewModel) {
    // State variable for selected target area
    val selectedTargetArea = viewModel.selectedTargetArea.value
    val context = LocalContext.current
    // State for showing the dialog
    val showDialog2 = remember { mutableStateOf(false) }

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
            // Back Button with Title
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
                    text = "Target Your Muscle",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.roboto_bold)),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select the body part you want to focus on for muscle gain. This will help us create a plan that enhances your desired muscle group.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.LightGray,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Upper Body Button
            GoalButton(
                text = "Upper Body",
                isSelected = selectedTargetArea == "Upper Body",
                onClick = { viewModel.setSelectedTargetArea("Upper Body") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Lower Body Button
            GoalButton(
                text = "Lower Body",
                isSelected = selectedTargetArea == "Lower Body",
                onClick = { viewModel.setSelectedTargetArea("Lower Body") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Full Body Button
            GoalButton(
                text = "Full Body",
                isSelected = selectedTargetArea == "Full Body",
                onClick = { viewModel.setSelectedTargetArea("Full Body") }
            )

            Spacer(modifier = Modifier.weight(1f)) // Push the Next button to the bottom

            // Next Button
            Button(
                onClick = {
                    if (selectedTargetArea == null) {
                        Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
                    } else {
                        showDialog2.value = true
                    }
                },
                enabled = selectedTargetArea != null, // Button is only enabled if an option is selected
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp)
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTargetArea != null) Color(0xFFD9D9D9) else Color.Transparent,
                    contentColor = if (selectedTargetArea != null) Color(0xFF025D93) else Color.White,
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

            // Display the dialog when showDialog2 is true
            if (showDialog2.value) {
                Dialog(onDismissRequest = { showDialog2.value = false }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.background(Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // First Card with Image and Text
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ai_icon), // Replace with your image resource
                                            contentDescription = "Image 1",
                                            modifier = Modifier.size(60.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = "Ai Fitness Assistant",
                                                color = Color.Black,
                                                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Ask personalized questions and get advice",
                                                color = Color.DarkGray,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                Divider(color = Color.Gray, thickness = 1.dp) // Horizontal Divider

                                // Second Card with Image and Text
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.plan_icon), // Replace with your image resource
                                            contentDescription = "Image 2",
                                            modifier = Modifier.size(60.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = "Diet Plans",
                                                color = Color.Black,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily(Font(R.font.roboto_bold))
                                            )
                                            Text(
                                                text = "Your personalized diet plans",
                                                color = Color.DarkGray,
                                                fontSize = 10.sp,
                                            )
                                        }
                                    }
                                }
                                Divider(color = Color.Gray, thickness = 1.dp) // Horizontal Divider
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.workout_icon), // Replace with your image resource
                                            contentDescription = "Image 2",
                                            modifier = Modifier.size(60.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = "Workout Plans",
                                                color = Color.Black,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily(Font(R.font.roboto_bold))
                                            )
                                            Text(
                                                text = "Your personalized workout plans",
                                                color = Color.DarkGray,
                                                fontSize = 10.sp,
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Close Button
                                Button(
                                    onClick = {
                                        showDialog2.value = false
                                        navController.navigate("login") },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = "Next",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily(Font(R.font.roboto_regular))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}