package com.nexgen.fitpulse.notification

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nexgen.fitpulse.R

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Check if the notification permission is granted
        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            val notification = NotificationCompat.Builder(applicationContext, "fitness_reminder_channel")
                .setSmallIcon(R.drawable.workout_icon) // Replace with your notification icon
                .setContentTitle("Fitness Reminder")
                .setContentText("Achieve your fitness goals today!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notificationManager.notify(1, notification)
            return Result.success()
        } else {
            // Handle the case where notifications are not enabled
            // For example, you could log a message or notify the user
            return Result.failure()
        }
    }
}
