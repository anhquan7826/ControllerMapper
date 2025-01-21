package com.touchmapper.app

import android.app.Notification
import android.app.Notification.Action
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context

object NotificationProvider {
    val overlayChannel = NotificationChannel(
        "CHANNEL_ID_OVERLAY_SERVICE",
        "Overlay Service",
        NotificationManager.IMPORTANCE_LOW,
    ).apply {
        description = "Touch Mapper Overlay Service"
    }

    fun buildNotification(
        context: Context,
        channel: NotificationChannel,
        title: String? = null,
        text: String? = null,
        smallIcon: Int? = null,
        intent: PendingIntent? = null,
        actions: List<Action>? = null
    ): Notification {
        return Notification.Builder(context, channel.id).apply {
            if (title != null) setContentTitle(title)
            if (text != null) setContentText(text)
            if (smallIcon != null) setSmallIcon(smallIcon)
            if (intent != null) this.setContentIntent(intent)
            actions?.forEach { addAction(it) }
        }
            .build()
    }
}