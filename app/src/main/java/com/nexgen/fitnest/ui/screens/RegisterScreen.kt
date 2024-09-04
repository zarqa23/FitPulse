package com.nexgen.fitnest.ui.screens

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nexgen.fitnest.R
import com.nexgen.fitnest.data.viewmodel.UserViewModel
import com.nexgen.fitnest.utilis.showNotification2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(userViewModel: UserViewModel, navController: NavHostController) {
    // State variables for input fields and validation
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // Custom font family
    val customFontFamily = FontFamily(
        Font(R.font.robotomedium) // Replace with the name of your font file
    )

    // Background with gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF025D93),
                        Color(0xFF024974),
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
                horizontalAlignment = Alignment.Start // Aligns text to the start
            ) {
                Text(
                    "Stay Updated with Fit Pulse",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    "Enter your details to get notified about updates, personalized plans, and more.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(.1f))

            // First Name Input
            OutlinedTextField(
                value = firstName,
                onValueChange = { newValue ->
                    firstName = newValue.uppercase()
                },
                label = {
                    Text(
                        text = "First Name",
                        color = Color.White
                    )
                },
                textStyle = TextStyle(color = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White
                )
            )

            // Last Name Input
            OutlinedTextField(
                value = lastName,
                onValueChange = { newValue ->
                    lastName = newValue.uppercase()
                },
                label = {
                    Text(
                        text = "Last Name",
                        color = Color.White
                    )
                },
                textStyle = TextStyle(color = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White
                )
            )

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { newValue ->
                    email = newValue
                },
                label = {
                    Text(
                        text = "Email",
                        color = Color.White
                    )
                },
                textStyle = TextStyle(color = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White
                )
            )

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { newValue ->
                    password = newValue
                },
                label = {
                    Text(
                        text = "Password",
                        color = Color.White
                    )
                },
                textStyle = TextStyle(color = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White
                ),
                visualTransformation = PasswordVisualTransformation()
            )

            // Display error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Spacer to push the Register button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Register Button
            Button(
                onClick = {
                    if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        userViewModel.signUpUser(email, password) { success ->
                            if (success) {
                                // Save registration status and new user details
                                sharedPreferences.edit().apply {
                                    putBoolean("isRegistered", true)
                                    putString("firstName", firstName)
                                    putString("lastName", lastName)
                                    putString("email", email) // Save the new email
                                    putString("aboutText", "") // Clear "About" text
                                    apply()
                                }

                                showNotification2(context)
                                // Navigate to HomeScreen instead of LoginScreen
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            } else {
                                errorMessage = "Registration failed. Please try again."
                            }
                        }
                    } else {
                        errorMessage = "Please fill in all fields correctly."
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD9D9D9),
                    contentColor = Color(0xFF025D93)
                ),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Register",
                    fontSize = 24.sp,
                    fontFamily = customFontFamily
                )
            }


            // Already have an account? Login Button
            TextButton(
                onClick = {
                    // Navigate to Login screen
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Already have an account? Login",
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontFamily = customFontFamily)
                )
            }
        }
    }
}
