package com.nexgen.fitpulse.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nexgen.fitpulse.notification.NotificationWorker
import com.nexgen.fitpulse.ui.components.BannerAdView
import com.nexgen.fitpulse.ui.viewmodel.UserSelectionViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(selectedItem: Int, onItemSelected: (Int) -> Unit, onBackClick: () -> Unit , navController: NavController , viewModel: UserSelectionViewModel) {
    var context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    val reminderEnabled = remember {
        mutableStateOf(sharedPreferences.getBoolean("reminder_enabled", false))
    }
    var isSkipped = viewModel.isSkipLoggedIn.value
    // Request notification permission if needed
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                // Handle case where permission is not granted (e.g., show a dialog or request permission)
            }
        }
    }


    Scaffold(
        containerColor = Color(0xFF011C2D),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF025D93),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column {
                BannerAdView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    adUnitId = "ca-app-pub-3940256099942544/6300978111" // Replace with your actual Ad Unit ID
                )
                BottomNavigationBar(selectedItem, onItemSelected)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF025D93),
                            Color(0xFF024974),
                            Color(0xFF011C2D)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Reminder Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .weight(.1f)
                        .padding(start = 8.dp)
                )
                Switch(
                    checked = reminderEnabled.value,
                    onCheckedChange = { isChecked ->
                        reminderEnabled.value = isChecked
                        // Save the switch state to SharedPreferences
                        with(sharedPreferences.edit()) {
                            putBoolean("reminder_enabled", isChecked)
                            apply()
                        }
                        if (isChecked) {
                            // Schedule daily notification
                            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                                .build()
                            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                                "daily_notification",
                                ExistingPeriodicWorkPolicy.REPLACE,
                                workRequest
                            )
                        } else {
                            // Cancel scheduled notifications if needed
                            WorkManager.getInstance(context).cancelUniqueWork("daily_notification")
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Green,
                        uncheckedThumbColor = Color.Red
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray, thickness = 1.dp) // Horizontal Divider
            Spacer(modifier = Modifier.height(16.dp))
            // Contact Us Option
            TextButton(
                onClick = { navController.navigate("contact_us") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Contact Us",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray, thickness = 1.dp) // Horizontal Divider
            Spacer(modifier = Modifier.height(16.dp))
            // Privacy Policy Option
            TextButton(
                onClick = { navController.navigate("privacy_policy") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Privacy Policy",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray, thickness = 1.dp) // Horizontal Divider
            Spacer(modifier = Modifier.height(16.dp))
            // Rate us Option
            TextButton(
                onClick = {
                    viewModel.setSkipLoginState(false) // Handle logout logic
                    onItemSelected(0) // Set selected item to Home

                    // Navigate to "your_goals" after setting the bottom navigation state
                    navController.navigate("your_goals") {
                        popUpTo("home") { inclusive = true } // If needed, pop the back stack to "home"
                    }
                          },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Log Out",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray, thickness = 1.dp ,) // Horizontal Divider

        }
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "fitness_reminder_channel"
        val channelName = "Fitness Reminder"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Channel for fitness reminder notifications"
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

