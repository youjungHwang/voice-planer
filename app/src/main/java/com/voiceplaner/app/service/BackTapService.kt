package com.voiceplaner.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.voiceplaner.app.R
import com.voiceplaner.app.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.sqrt

@AndroidEntryPoint
class BackTapService : Service(), SensorEventListener {

    @Inject lateinit var speechManager: SpeechManager

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastTapTime = 0L
    private var isRecording = false

    companion object {
        private const val TAP_THRESHOLD = 15f
        private const val DOUBLE_TAP_WINDOW = 800L
        private const val MIN_TAP_INTERVAL = 100L
        private const val CHANNEL_ID = "voice_planer_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        startForeground(NOTIFICATION_ID, buildNotification("가계부 대기 중"))
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]; val y = event.values[1]; val z = event.values[2]
        val delta = abs(sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH)
        if (delta > TAP_THRESHOLD) {
            val now = System.currentTimeMillis()
            val elapsed = now - lastTapTime
            if (elapsed in MIN_TAP_INTERVAL..DOUBLE_TAP_WINDOW) {
                onDoubleTap()
                lastTapTime = 0L
            } else {
                lastTapTime = now
            }
        }
    }

    private fun onDoubleTap() {
        vibrate()
        if (!isRecording) {
            isRecording = true
            speechManager.startListening()
            updateNotification("듣는 중... (두 번 두드리면 완료)")
        } else {
            isRecording = false
            speechManager.stopListening()
            updateNotification("가계부 대기 중")
        }
    }

    private fun vibrate() {
        (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
            .vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun buildNotification(text: String): Notification {
        val channel = NotificationChannel(CHANNEL_ID, "가계부 서비스", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("VoicePlaner")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_mic)
            .setContentIntent(
                PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE)
            ).build()
    }

    private fun updateNotification(text: String) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(text))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { super.onDestroy(); sensorManager.unregisterListener(this) }
}
