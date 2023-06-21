package com.example.quitnow.data.broadcasts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.quitnow.MainActivity
import com.example.quitnow.MainApplication.Companion.GOAL_CHANNEL_ID
import com.example.quitnow.MainApplication.Companion.GOAL_NOTIFICATION_ID
import com.example.quitnow.R

class GoalBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.run {
            createNotification(this)
        }
    }

    private fun createNotification(context: Context) {
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val notification = NotificationCompat.Builder(context, GOAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.mp_ic_smoke_free_black)
            .setContentText(context.getString(R.string.notification_goal_title))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Issue the notification.
        val notificationManager = createNotificationChannel(context)
        notificationManager.notify(GOAL_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context): NotificationManager {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val name = context.getString(R.string.channel_name)
                val descriptionText = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(GOAL_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

                notificationManager
            }
            else -> {
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
        }
    }

}
