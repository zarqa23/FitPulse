package com.nexgen.fitnest.ui.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.work.WorkManager
import com.nexgen.fitnest.data.viewmodel.UserViewModel
import com.nexgen.fitnest.ui.components.AppBackground
import com.nexgen.fitnest.ui.components.BannerAdView
import com.nexgen.fitnest.ui.components.BottomNavigationBar
import com.nexgen.fitnest.ui.components.DrawerContent
import com.nexgen.fitnest.ui.components.SettingsButton
import com.nexgen.fitnest.utilis.AdManager
import com.nexgen.fitnest.utilis.scheduleNotifications

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var editProfileClicked by remember { mutableStateOf(false) }
    var changeThemeClicked by remember { mutableStateOf(false) }
    var deleteAccountClicked by remember { mutableStateOf(false) }
    var showComingSoonDialog by remember { mutableStateOf(false) }
    var showNotImplementedDialog by remember { mutableStateOf(false) }

    // State for the Reminder switch
    var isReminderEnabled by remember { mutableStateOf(false) }

    // Function to show interstitial ad
    fun showInterstitialAd() {
        val activity = context as? Activity
        if (activity != null) {
            AdManager.showAd(activity)
        }
    }

    ModalNavigationDrawer(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(navController, onLogout = {})
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings", fontSize = 26.sp) },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate("home") {
                                popUpTo("settings") { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                Column {
                    // Banner Ad above the BottomNavigationBar
                    BannerAdView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        adUnitId = "ca-app-pub-3940256099942544/6300978111" // Replace with your actual Ad Unit ID
                    )
                    BottomNavigationBar(
                        selectedItem = remember { mutableStateOf(2) }.value,
                        onItemSelected = { index ->
                            when (index) {
                                0 -> navController.navigate("profile") {
                                    launchSingleTop = true
                                    popUpTo("profile") { inclusive = false }
                                }
                                1 -> navController.navigate("home") {
                                    launchSingleTop = true
                                    popUpTo("home") { inclusive = false }
                                    showInterstitialAd()
                                }
                                2 -> navController.navigate("settings") {
                                    launchSingleTop = true
                                    popUpTo("settings") { inclusive = false }
                                }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            AppBackground {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    SettingsButton(
                        text = "Rate Us",
                        onClick = {
                            navController.navigate("rateus") {
                                launchSingleTop = true
                                popUpTo("settings") { inclusive = false }
                            }
                        },
                        isClicked = editProfileClicked,
                        onClickStateChange = { editProfileClicked = !editProfileClicked }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Reminder button with switch
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Handle click if needed */ }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Reminder",
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )
                        // Inside SettingsScreen Composable
                        Switch(
                            checked = isReminderEnabled,
                            onCheckedChange = { isEnabled ->
                                isReminderEnabled = isEnabled
                                sharedPreferences.edit().putBoolean("isReminderEnabled", isEnabled).apply()

                                // Schedule or cancel notifications based on switch state
                                if (isEnabled) {
                                    scheduleNotifications(context)
                                } else {
                                    WorkManager.getInstance(context).cancelUniqueWork("fitness_reminder")
                                }
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsButton(
                        text = "Privacy Policy",
                        onClick = {
                            navController.navigate("privacypolicy")
                        }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsButton(
                        text = "Contact Us",
                        onClick = {
                            navController.navigate("contact_us")
                        }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsButton(
                        text = "Log Out",
                        onClick = {
                            navController.navigate("yourgoals")
                        }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    // Confirmation Dialog for Account Deletion
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    userViewModel.getUserPhoneNumber { phoneNumber ->
                        phoneNumber?.let {
                            userViewModel.deleteUser(it) { success ->
                                if (success) {
                                    sharedPreferences.edit().apply {
                                        putBoolean("isRegistered", false)
                                        apply()
                                    }
                                    navController.navigate("yourgoals") {
                                        popUpTo("settings") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Failed to delete account", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    showDeleteConfirmDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}