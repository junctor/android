package com.advice.reminder

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.advice.core.local.Document
import com.advice.core.local.Event
import com.advice.reminder.R

@SuppressLint("MissingPermission")
class NotificationHelper(
    private val context: Context,
) {
    private val manager = NotificationManagerCompat.from(context)

    init {
        val channel =
            NotificationChannel(
                CHANNEL_UPDATES,
                "Schedule Updates",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Notifications about changes within the events"
                enableLights(true)
                lightColor = Color.MAGENTA
            }

        manager.createNotificationChannel(channel)
    }

    private fun getStartingSoonNotification(event: Event): Notification =
        notification {
            setContentTitle(event.title)
            setContentText(context.getString(R.string.notification_text, event.session.location.name))
            setContentIntent(getPendingIntent(event))
        }

    private fun getUpdatedNotification(event: Event): Notification =
        notification {
            setContentTitle(event.title)
            setContentText("Heads up, session details has been updated!")
            setContentIntent(getPendingIntent(event))
        }

    private fun getFeedbackReminderNotification(event: Event): Notification =
        notification {
            setContentTitle(event.title)
            setContentText("Enjoying the session? Leave us feedback!")
            setContentIntent(getPendingIntent(event))
        }

    private fun getDocumentNotification(
        document: Document,
        conferenceCode: String,
    ): Notification =
        notification {
            setContentTitle(document.title)
            setContentText("Tap here for more details")
            setContentIntent(getPendingIntent(document.id, conferenceCode))
        }

    private fun notification(block: NotificationCompat.Builder.() -> Unit): Notification =
        NotificationCompat
            .Builder(context, CHANNEL_UPDATES)
            .apply {
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                setVibrate(longArrayOf(0, 250, 500, 250))
                setLights(Color.MAGENTA, 3000, 1000)
                setSmallIcon(R.drawable.ic_notification)
                color = ContextCompat.getColor(context, R.color.colorPrimary)
                setAutoCancel(true)
                block()
            }.build()

    private fun getPendingIntent(event: Event): PendingIntent {
        val deepLink =
            "https://hackertracker.app/event?c=${event.conference}&e=${event.eventId}".toUri()

        val intent =
            Intent(Intent.ACTION_VIEW, deepLink).apply {
                setPackage("com.shortstack.hackertracker")
            }
        return PendingIntent.getActivity(
            context,
            pendingIntentRequestCode(kind = "event", id = event.id),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun getPendingIntent(
        documentId: Long,
        conferenceCode: String,
    ): PendingIntent {
        val deepLink =
            "https://hackertracker.app/document?c=$conferenceCode&id=$documentId".toUri()

        val intent =
            Intent(Intent.ACTION_VIEW, deepLink).apply {
                setPackage("com.shortstack.hackertracker")
            }
        return PendingIntent.getActivity(
            context,
            pendingIntentRequestCode(kind = "document", id = documentId),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
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

    fun notifyEmergency(
        document: Document,
        conferenceCode: String,
    ) {
        manager.notify(
            911 + document.id.toInt(),
            getDocumentNotification(document, conferenceCode),
        )
    }

    companion object {
        private const val CHANNEL_UPDATES = "updates_channel"

        /** Stable unique request codes so PendingIntents do not overwrite each other. */
        fun pendingIntentRequestCode(
            kind: String,
            id: Long,
        ): Int = 31 * kind.hashCode() + id.hashCode()
    }
}
