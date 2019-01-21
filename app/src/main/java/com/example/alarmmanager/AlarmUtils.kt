package com.example.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.util.*

const val CHANNEL_REMINDER = "reminder"
const val REMINDER_NOTIFICATION_ID = "notification_id"

private const val REMINDER_NOTIFICATION_MESSAGE = "message"
private const val REMINDER_NOTIFICATION_INTERVAL = "interval"

const val NOTIF_ID_SPECIFIC_TIME = 1001
const val NOTIF_ID_INTERVAL_TIME = 1002

object AlarmUtils {

    fun setAlarmManager(context: Context, message: String,
                        calendar: Calendar, interval: Long,
                        notificationId: Int, forced: Boolean) {

        if (isAlarmActive(context, notificationId, message, interval) && !forced) {
            Timber.d("Time based alarm already active")
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager? ?: return
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(REMINDER_NOTIFICATION_MESSAGE, message)
        intent.putExtra(REMINDER_NOTIFICATION_ID, notificationId)
        intent.putExtra(REMINDER_NOTIFICATION_INTERVAL, interval)
        intent.setClass(context, AlarmReceiver::class.java)

        val pendingIntent: PendingIntent
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        try {
            pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } catch (e: Exception) {
            Timber.tag("AlarmTest").d("Alarm manager exception: " + e.message)
        }

    }

    private fun isAlarmActive(context: Context, id: Int, message: String, interval: Long): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(REMINDER_NOTIFICATION_MESSAGE, message)
        intent.putExtra(REMINDER_NOTIFICATION_ID, id)
        intent.putExtra(REMINDER_NOTIFICATION_INTERVAL, interval)
        intent.setClass(context, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE) != null
    }

    fun setRepeatingAlarm(context: Context, message: String, initialCalendar: Calendar,
                          interval: Long, notificationId: Int) {
        if (isAlarmActive(context, notificationId, message, interval)) {
            Timber.d("Interval based alarm already active")
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager? ?: return
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(REMINDER_NOTIFICATION_MESSAGE, message)
        intent.putExtra(REMINDER_NOTIFICATION_ID, notificationId)
        intent.putExtra(REMINDER_NOTIFICATION_INTERVAL, interval)
        intent.setClass(context, AlarmReceiver::class.java)

        val pendingIntent: PendingIntent
        initialCalendar.set(Calendar.SECOND, 0)
        initialCalendar.set(Calendar.MILLISECOND, 0)
        try {
            pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, initialCalendar.timeInMillis, interval,
                pendingIntent)
        } catch (e: Exception) {
            Timber.tag("AlarmTest").d("Alarm manager exception: " + e.message)
        }
    }

    fun cancelAlarm(context: Context, id: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getService(context, id, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent?.cancel()
    }
}