package com.sehatapp.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.tag("AlarmTest").d("Alarm received")
        if (intent == null || context == null) return

        val notifId = intent.getIntExtra(REMINDER_NOTIFICATION_ID, 1000)
        val msg = if (notifId == NOTIF_ID_SPECIFIC_TIME)
            context.getString(R.string.reminder_message)
        else context.getString(R.string.reminder_message_interval)
        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDER)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle("Test notification")
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setSummaryText(context.getString(R.string.reminder))
                    .bigText(msg)
            )
            .setContentText(msg)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notifId, builder.build())
    }
}