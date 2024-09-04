package com.nexgen.fitnest.utilis

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleNotifications(context: Context) {
    val workRequest = PeriodicWorkRequest.Builder(ReminderWorker::class.java, 12, TimeUnit.HOURS)
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "fitness_reminder",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}