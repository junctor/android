package com.advice.core.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.shortstack.core.R

@SuppressLint("MissingPermission")
class NotificationHelper(private val context: Context) {

    private val manager = NotificationManagerCompat.from(context)

    init {
        val channel = NotificationChannel(
            CHANNEL_UPDATES,
            "Schedule Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications about changes within the events"
            enableLights(true)
            lightColor = Color.MAGENTA
        }

        manager.createNotificationChannel(channel)
    }

    private fun getStartingSoonNotification(
        conference: String,
        content: Long,
        event: Long,
        title: String,
        location: String,
    ): Notification = notification {
        setContentTitle(title)
        setContentText(context.getString(R.string.notification_text, location))
        setContentIntent(getPendingIntent(conference, content, event))
    }

    private fun getUpdatedNotification(
        conference: String,
        content: Long,
        event: Long,
        title: String,
    ): Notification = notification {
        setContentTitle(title)
        setContentText("Heads up, session details has been updated!")
        setContentIntent(getPendingIntent(conference, content, event))
    }

    private fun getFeedbackReminderNotification(
        conference: String,
        content: Long,
        event: Long,
        title: String,
    ): Notification = notification {
        setContentTitle(title)
        setContentText("Enjoying the session? Leave us feedback!")
        setContentIntent(getPendingIntent(conference, content, event))
    }

    private fun notification(block: NotificationCompat.Builder.() -> Unit): Notification {
        return NotificationCompat.Builder(context, CHANNEL_UPDATES).apply {
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            setVibrate(longArrayOf(0, 250, 500, 250))
            setLights(Color.MAGENTA, 3000, 1000)
            setSmallIcon(R.drawable.ic_notification)
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            setAutoCancel(true)
            block()
        }.build()
    }

    private fun getPendingIntent(conference: String, content: Long, event: Long): PendingIntent {
        val deepLink =
            Uri.parse("https://hackertracker.app/event?c=${conference}&e=$content:$event")

        val intent = Intent(Intent.ACTION_VIEW, deepLink).apply {
            setPackage("com.shortstack.hackertracker")
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun notifyStartingSoon(
        conference: String,
        content: Long,
        event: Long,
        title: String,
        location: String,
    ) {
        manager.notify(
            event.toInt(),
            getStartingSoonNotification(conference, content, event, title, location)
        )
    }

    fun notifySessionUpdated(
        conference: String,
        content: Long,
        event: Long,
        title: String,
    ) {
        manager.notify(event.toInt(), getUpdatedNotification(conference, content, event, title))
    }

    fun notifyFeedbackAvailable(
        conference: String,
        content: Long,
        event: Long,
        title: String,
    ) {
        manager.notify(
            1001 + event.toInt(),
            getFeedbackReminderNotification(conference, content, event, title)
        )
    }

    companion object {
        private const val CHANNEL_UPDATES = "updates_channel"
    }
}
