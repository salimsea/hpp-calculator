package com.seal.hppcalculator.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.seal.hppcalculator.MainActivity
import com.seal.hppcalculator.R

class CashflowReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        showReminderNotification(context)
    }

    companion object {
        const val CHANNEL_ID = "cashflow_reminder_channel"
        const val NOTIFICATION_ID = 1001

        fun showReminderNotification(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Pengingat Buku Kas Harian",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Mengingatkan untuk mencatat pemasukan dan pengeluaran usaha harian"
                }
                notificationManager.createNotificationChannel(channel)
            }

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Sudah Catat Buku Kas Hari Ini? \uD83D\uDCB0")
                .setContentText("Jangan lupa rekap pemasukan & pengeluaran hari ini agar pembukuan tetap rapi!")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "Jangan lupa rekap seluruh pemasukan & pengeluaran hari ini agar pembukuan usaha Anda tetap rapi dan keuntungan terpantau!"
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }
}
