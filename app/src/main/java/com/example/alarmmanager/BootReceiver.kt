package com.example.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class BootReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        if (Intent.ACTION_BOOT_COMPLETED.equals(action, ignoreCase = true) ||
            Intent.ACTION_TIMEZONE_CHANGED.equals(action, ignoreCase = true) ||
            Intent.ACTION_MY_PACKAGE_REPLACED.equals(action, ignoreCase = true) ||
            Intent.ACTION_TIME_CHANGED.equals(action, ignoreCase = true)
        ) {
            Timber.d("Boot, Time Change received, action: $action")
            context?.let {
                val alarmPreference = AlarmPreference(it)
                AlarmUtils.setAlarmsIfNeeded(it, alarmPreference, false)
            }
            return
        }
    }
}