package com.russhwolf.soluna.android.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.russhwolf.soluna.android.R

class NotificationBroadcastReceiver : BroadcastReceiver() {

    companion object {
        fun getShowNotificationIntent(context: Context, notificationId: Int, message: String, channel: String) =
            Intent(context, NotificationBroadcastReceiver::class.java)
                .putExtra("show", true)
                .putExtra("id", notificationId)
                .putExtra("message", message)
                .putExtra("channel", channel)

        fun getHideNotificationIntent(context: Context, notificationId: Int) =
            Intent(context, NotificationBroadcastReceiver::class.java)
                .putExtra("show", false)
                .putExtra("id", notificationId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(context.applicationContext)
        if (intent.getBooleanExtra("show", false)) {
            val notification = NotificationCompat.Builder(
                context.applicationContext,
                intent.getStringExtra("channel") ?: NotificationChannelCompat.DEFAULT_CHANNEL_ID
            )
                .setContentText(intent.getStringExtra("message"))
                .setStyle(NotificationCompat.BigTextStyle().bigText(intent.getStringExtra("message")))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()

            notificationManager.notify(intent.getIntExtra("id", 0), notification)
        } else {
            notificationManager.cancel(intent.getIntExtra("id", 0))
        }
    }
}
