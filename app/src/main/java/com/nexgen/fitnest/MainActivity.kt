package com.nexgen.fitnest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.ads.MobileAds
import com.nexgen.fitnest.data.database.AppDatabase
import com.nexgen.fitnest.data.viewmodel.UserViewModel
import com.nexgen.fitnest.ui.Navigation.MyApp
import com.nexgen.fitnest.utilis.AdManager
import com.nexgen.fitnest.utilis.ReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getDatabase(applicationContext)
                return UserViewModel(db.userDao()) as T
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "Notification permission already granted")
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotificationPermission()
        MobileAds.initialize(this) { }
        AdManager.loadAd(this)
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("isRegistered", false)
        val isReminderEnabled = sharedPreferences.getBoolean("isReminderEnabled", false)

        if (isReminderEnabled) {
            scheduleNotifications(context = this)
        }
        setContent {
            val navController = rememberNavController()
            MyApp(navController = navController, userViewModel = userViewModel)
        }
    }
    private fun scheduleNotifications(context: Context) {
        val workRequest = PeriodicWorkRequest.Builder(ReminderWorker::class.java, 12, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "fitness_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    override fun onResume() {
        super.onResume()
        // Optionally reload or prepare the ad again if needed
        AdManager.loadAd(this)
    }
}

