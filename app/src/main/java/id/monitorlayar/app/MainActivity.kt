package id.monitorlayar.app

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var statusText: TextView

    private val REQUEST_SCREEN_CAPTURE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 60, 40, 40)

        statusText = TextView(this)
        statusText.text = "Monitor Layar\n\nStatus: Tidak aktif"
        statusText.textSize = 22f

        val startButton = Button(this)
        startButton.text = "MULAI MONITORING"

        val stopButton = Button(this)
        stopButton.text = "STOP MONITORING"

        layout.addView(statusText)
        layout.addView(startButton)
        layout.addView(stopButton)

        setContentView(layout)

        startButton.setOnClickListener {
            startMonitoring()
        }

        stopButton.setOnClickListener {
            stopMonitoring()
        }
    }

    private fun startMonitoring() {
        val manager = getSystemService(
            Context.MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager

        val intent = manager.createScreenCaptureIntent()

        startActivityForResult(
            intent,
            REQUEST_SCREEN_CAPTURE
        )
    }

    @Deprecated("Deprecated Android callback")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SCREEN_CAPTURE) {

            if (resultCode == RESULT_OK && data != null) {

                val serviceIntent = Intent(
                    this,
                    MonitorService::class.java
                )

                serviceIntent.putExtra(
                    "resultCode",
                    resultCode
                )

                serviceIntent.putExtra(
                    "projectionData",
                    data
                )

                startForegroundService(serviceIntent)

                statusText.text =
                    "Monitor Layar\n\nStatus: Monitoring aktif"

                Toast.makeText(
                    this,
                    "Monitoring dimulai",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                statusText.text =
                    "Monitor Layar\n\nStatus: Izin ditolak"
            }
        }
    }

    private fun stopMonitoring() {

        val intent = Intent(
            this,
            MonitorService::class.java
        )

        intent.action = "STOP"

        startService(intent)

        statusText.text =
            "Monitor Layar\n\nStatus: Tidak aktif"

        Toast.makeText(
            this,
            "Monitoring dihentikan",
            Toast.LENGTH_SHORT
        ).show()
    }
}
