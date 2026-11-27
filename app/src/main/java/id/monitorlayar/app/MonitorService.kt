package id.monitorlayar.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.content.Context
import android.os.Build

class MonitorService : Service() {

    private var mediaProjection: MediaProjection? = null

    private val CHANNEL_ID = "monitor_layar_channel"
    private val NOTIFICATION_ID = 100

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent?.action == "STOP") {
            stopMonitoring()
            return START_NOT_STICKY
        }

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )

        val resultCode = intent?.getIntExtra(
            "resultCode",
            0
        ) ?: 0

        val projectionData = intent?.getParcelableExtra<Intent>(
            "projectionData"
        )

        if (projectionData != null) {

            val manager = getSystemService(
                Context.MEDIA_PROJECTION_SERVICE
            ) as MediaProjectionManager

            mediaProjection = manager.getMediaProjection(
                resultCode,
                projectionData
            )
        }

        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Monitor Layar Aktif")
                .setContentText(
                    "Berbagi layar sedang berlangsung"
                )
                .setSmallIcon(
                    android.R.drawable.ic_menu_view
                )
                .setOngoing(true)
                .build()

        } else {

            Notification.Builder(this)
                .setContentTitle("Monitor Layar Aktif")
                .setContentText(
                    "Berbagi layar sedang berlangsung"
                )
                .setSmallIcon(
                    android.R.drawable.ic_menu_view
                )
                .build()
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitoring Layar",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(channel)
        }
    }

    private fun stopMonitoring() {

        mediaProjection?.stop()
        mediaProjection = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        mediaProjection?.stop()
        mediaProjection = null

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
