package com.nexgen.fitnest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nexgen.fitnest.R

@Composable
fun DrawerContent(navController: NavHostController, onLogout: () -> Unit) {
    val customFontFamily = FontFamily(Font(R.font.robotomedium)) // Using the same font family
    val drawerItemColor = Color(0xFF025D93) // Consistent text color
    val backgroundColor = Color(0xFFFFFFF7) // Drawer background color

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(backgroundColor) // Apply background color
    ) {
        listOf("Home", "About Us","Fitness Assistant").forEach { screen ->
            Text(
                text = screen,
                color = drawerItemColor, // Text color
                fontFamily = customFontFamily, // Apply custom font
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (screen) {
                            "Home" -> navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }

                            "About Us" -> navController.navigate("aboutus")
                            "Fitness Assistant" -> navController.navigate("fitness_assistant")
                        }
                    }
                    .padding(16.dp)
            )
            Divider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 8.dp)) // Divider color
        }
    }
}