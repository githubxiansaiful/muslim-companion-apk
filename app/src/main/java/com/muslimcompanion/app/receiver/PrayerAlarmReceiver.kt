package com.muslimcompanion.app.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.muslimcompanion.app.R
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.MainActivity

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PRAYER_ALARM = "com.muslimcompanion.app.ACTION_PRAYER_ALARM"
        const val EXTRA_PRAYER_ID = "extra_prayer_id"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_TRIGGER_TIME = "extra_trigger_time"

        private const val TAG = "PrayerAlarmReceiver"
        const val CHANNEL_ID = "prayer_alarm_channel"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d(TAG, "Received broadcast action: $action")

        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                Log.d(TAG, "Device rebooted or time changed, rescheduling all active alarms.")
                PrayerAlarmScheduler.rescheduleAllActiveAlarms(context)
            }

            ACTION_PRAYER_ALARM -> {
                val prayerId = intent.getStringExtra(EXTRA_PRAYER_ID) ?: return
                val prayerNameBn = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "নামাজ"
                val triggerTime = intent.getLongExtra(EXTRA_TRIGGER_TIME, System.currentTimeMillis())

                val prefsRepo = PreferencesRepository(context)
                if (!prefsRepo.isPrayerAlertEnabled(prayerId)) {
                    Log.d(TAG, "Alert for $prayerId is turned off in preferences, skipping.")
                    return
                }

                showPrayerNotification(context, prayerId, prayerNameBn)

                // Reschedule for tomorrow (+24 hours) so it runs continuously
                val tomorrowTime = triggerTime + 24 * 60 * 60 * 1000L
                PrayerAlarmScheduler.scheduleAlarm(context, prayerId, prayerNameBn, tomorrowTime)
            }
        }
    }

    private fun showPrayerNotification(context: Context, prayerId: String, prayerNameBn: String) {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) ?: return

        // Create high-importance channel if Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "নামাজের সময় অ্যালার্ট",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "প্রতি ওয়াক্ত নামাজের সময়ে নোটিফিকেশন প্রদান"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "prayer")
        }
        val pendingOpenApp = PendingIntent.getActivity(
            context,
            PrayerAlarmScheduler.getRequestCode(prayerId),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$prayerNameBn সালাতের ওয়াক্ত শুরু হয়েছে")
            .setContentText("এখন $prayerNameBn নামাজের সময়। সঠিক সময়ে নামাজ আদায়ের প্রস্তুতি নিন।")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("এখন $prayerNameBn নামাজের সময় হয়েছে। প্রিয় নবী ﷺ বলেছেন, 'সর্বোত্তম আমল হলো যথাসময়ে নামাজ আদায় করা।'")
            )
            .setColor(0xFF0D9488.toInt())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setContentIntent(pendingOpenApp)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "অ্যাপ খুলুন",
                pendingOpenApp
            )
            .build()

        notificationManager.notify(PrayerAlarmScheduler.getRequestCode(prayerId), notification)
    }
}
