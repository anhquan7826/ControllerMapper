package com.touchmapper.app.overlay

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.touchmapper.app.NotificationProvider

class OverlayService : Service() {
    companion object {
        private const val ACTION_STOP = "STOP"

        fun start(context: Context) {
            context.startForegroundService(
                Intent(context, OverlayService::class.java)
            )
        }
    }

    private lateinit var overlayView: OverlayView

    override fun onCreate() {
        super.onCreate()
        getSystemService(NotificationManager::class.java).apply {
            createNotificationChannel(
                NotificationProvider.overlayChannel
            )
        }
        startForeground(
            1,
            NotificationProvider.buildNotification(
                this,
                NotificationProvider.overlayChannel,
                smallIcon = android.R.drawable.ic_dialog_info,
                title = "Touch Mapper overlay is present.",
                actions = listOf(
                    Notification.Action.Builder(
                        null,
                        "Stop",
                        PendingIntent.getService(
                            this,
                            0,
                            Intent(this, OverlayService::class.java).apply {
                                action = ACTION_STOP
                            },
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    ).build()
                )
            )
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
        } else {
            if (!::overlayView.isInitialized) {
                overlayView = OverlayView(this)
            }
            overlayView.show()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        overlayView.hide()
        super.onDestroy()
    }
}