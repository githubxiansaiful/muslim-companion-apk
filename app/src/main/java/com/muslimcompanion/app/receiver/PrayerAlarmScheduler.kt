package com.muslimcompanion.app.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.muslimcompanion.app.data.repository.PrayerRepository
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.MainActivity
import java.util.Calendar
import java.util.Date

object PrayerAlarmScheduler {

    private const val TAG = "PrayerAlarmScheduler"

    fun getRequestCode(prayerId: String): Int {
        return when (prayerId.lowercase()) {
            "fajr" -> 1001
            "dhuhr" -> 1002
            "asr" -> 1003
            "maghrib" -> 1004
            "isha" -> 1005
            "tahajjud" -> 1006
            "ishraq" -> 1007
            "chasht" -> 1008
            "zawal" -> 1009
            else -> prayerId.hashCode() and 0xFFFF
        }
    }

    /**
     * Schedules an exact system alarm for the specified prayer time.
     * If the time today has passed, it automatically targets tomorrow.
     */
    fun scheduleAlarm(
        context: Context,
        prayerId: String,
        prayerNameBn: String,
        triggerTimeMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        var actualTriggerTime = triggerTimeMillis
        val now = System.currentTimeMillis()

        // If time already passed today, advance by 24 hours
        while (actualTriggerTime <= now) {
            actualTriggerTime += 24 * 60 * 60 * 1000L
        }

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_ID, prayerId)
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayerNameBn)
            putExtra(PrayerAlarmReceiver.EXTRA_TRIGGER_TIME, actualTriggerTime)
        }

        val requestCode = getRequestCode(prayerId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    val showIntent = PendingIntent.getActivity(
                        context,
                        requestCode + 5000,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(actualTriggerTime, showIntent),
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, actualTriggerTime, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, actualTriggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, actualTriggerTime, pendingIntent)
            }
            Log.d(TAG, "Scheduled alarm for $prayerId ($prayerNameBn) at ${Date(actualTriggerTime)}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule exact alarm for $prayerId: ${e.message}")
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, actualTriggerTime, pendingIntent)
            } catch (ignored: Exception) {}
        }
    }

    /**
     * Cancels any scheduled alarm for the given prayer.
     */
    fun cancelAlarm(context: Context, prayerId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
        }
        val requestCode = getRequestCode(prayerId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled alarm for $prayerId")
        }
    }

    /**
     * Reschedules all prayer alarms that are marked active in user preferences.
     */
    fun rescheduleAllActiveAlarms(context: Context) {
        val prefsRepo = PreferencesRepository(context)
        val currentCity = prefsRepo.currentCity.value
        val calcMethod = prefsRepo.calculationMethod.value
        val madhab = prefsRepo.madhab.value

        val schedule = PrayerRepository.calculateSchedule(
            currentCity.latitude,
            currentCity.longitude,
            currentCity.nameBn,
            currentCity.country,
            calcMethod,
            madhab
        )

        val prayers = listOf(
            schedule.fajr,
            schedule.dhuhr,
            schedule.asr,
            schedule.maghrib,
            schedule.isha
        )

        for (prayer in prayers) {
            if (prefsRepo.isPrayerAlertEnabled(prayer.id)) {
                scheduleAlarm(
                    context = context,
                    prayerId = prayer.id,
                    prayerNameBn = prayer.nameBn,
                    triggerTimeMillis = prayer.date.time
                )
            } else {
                cancelAlarm(context, prayer.id)
            }
        }
    }
}
