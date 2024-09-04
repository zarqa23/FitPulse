package com.nexgen.fitnest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nexgen.fitnest.R

@Composable
fun BottomNavigationBar(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (selectedItem == 1) Color(0xFF025D93) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (selectedItem == 1) Color.White else Color.DarkGray
                    )
                }
            },
            label = {
                Text(
                    text = "Home",
                    color = if (selectedItem == 1) Color(0xFF025D93) else Color.DarkGray
                )
            },
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Transparent, // We are using our custom circle background
                unselectedIconColor = Color.DarkGray,
                indicatorColor = Color.Transparent // Disable default indicator
            )
        )
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (selectedItem == 0) Color(0xFF025D93) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile",
                        tint = if (selectedItem == 0) Color.White else Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "Profile",
                    color = if (selectedItem == 0) Color(0xFF025D93) else Color.DarkGray
                )
            },
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Transparent,
                unselectedIconColor = Color.DarkGray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (selectedItem == 2) Color(0xFF025D93) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = if (selectedItem == 2) Color.White else Color.DarkGray
                    )
                }
            },
            label = {
                Text(
                    text = "Settings",
                    color = if (selectedItem == 2) Color(0xFF025D93) else Color.DarkGray
                )
            },
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Transparent,
                unselectedIconColor = Color.DarkGray,
                indicatorColor = Color.Transparent
            )
        )
    }
}