package com.seal.hppcalculator.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object CashflowReminderManager {

    private const val PREFS_NAME = "cashflow_settings"
    private const val KEY_REMINDER_ENABLED = "key_reminder_enabled"
    private const val KEY_REMINDER_HOUR = "key_reminder_hour"
    private const val DEFAULT_REMINDER_HOUR = 20 // 20:00 WIB (8 Malam)

    fun isReminderEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_REMINDER_ENABLED, false)
    }

    fun getReminderHour(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR)
    }

    fun setReminder(context: Context, enabled: Boolean, hour: Int = DEFAULT_REMINDER_HOUR) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_REMINDER_ENABLED, enabled)
            .putInt(KEY_REMINDER_HOUR, hour)
            .apply()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CashflowReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            CashflowReminderReceiver.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (enabled) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun triggerTestNotification(context: Context) {
        CashflowReminderReceiver.showReminderNotification(context)
    }
}
