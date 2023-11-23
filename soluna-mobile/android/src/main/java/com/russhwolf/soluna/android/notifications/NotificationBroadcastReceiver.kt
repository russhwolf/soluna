package com.russhwolf.soluna.android.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
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

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO we never request this so we'll always hit this
                Log.w(LOG_TAG, "Attempted to show notification, but need runtime permission!")
                return
            }
            notificationManager.notify(intent.getIntExtra("id", 0), notification)
        } else {
            notificationManager.cancel(intent.getIntExtra("id", 0))
        }
    }
}

private const val LOG_TAG = "NotificationBroadcastReceiver"
