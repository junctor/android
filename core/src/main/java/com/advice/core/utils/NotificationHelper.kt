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
import com.advice.core.local.Document
import com.advice.core.local.Event
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

    private fun getStartingSoonNotification(event: Event): Notification = notification {
        setContentTitle(event.title)
        setContentText(context.getString(R.string.notification_text, event.session.location.name))
        setContentIntent(getPendingIntent(event))
    }

    private fun getUpdatedNotification(event: Event): Notification = notification {
        setContentTitle(event.title)
        setContentText("Heads up, session details has been updated!")
        setContentIntent(getPendingIntent(event))
    }

    private fun getFeedbackReminderNotification(event: Event): Notification = notification {
        setContentTitle(event.title)
        setContentText("Enjoying the session? Leave us feedback!")
        setContentIntent(getPendingIntent(event))
    }

    private fun getDocumentNotification(document: Document): Notification =
        notification {
            setContentTitle(document.title)
            setContentText("Tape for more details")
            setContentIntent(getPendingIntent(document.id))
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

    private fun getPendingIntent(event: Event): PendingIntent {
        val deepLink =
            Uri.parse("https://hackertracker.app/event?c=${event.conference}&e=${event.eventId}")

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

    private fun getPendingIntent(documentId: Long): PendingIntent {
        val deepLink =
            Uri.parse("https://hackertracker.app/document?id=$documentId")

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

    fun notifyStartingSoon(event: Event) {
        manager.notify(event.id.toInt(), getStartingSoonNotification(event))
    }

    fun notifySessionUpdated(event: Event) {
        manager.notify(event.id.toInt(), getUpdatedNotification(event))
    }

    fun notifyFeedbackAvailable(event: Event) {
        manager.notify(1001 + event.id.toInt(), getFeedbackReminderNotification(event))
    }

    fun notifyEmergency(document: Document) {
        manager.notify(
            911 + document.id.toInt(),
            getDocumentNotification(document)
        )
    }

    companion object {
        private const val CHANNEL_UPDATES = "updates_channel"
    }
}
