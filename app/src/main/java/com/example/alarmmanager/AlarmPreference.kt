package com.example.alarmmanager

import android.content.Context
import android.content.SharedPreferences

const val ALARM_DISABLED = 0
const val ALARM_SPECIFIC_TIME = 1
const val ALARM_INTERVAL_BASED = 2

private const val KEY_ALARM_PREF = "alarm_preferences"
private const val KEY_ALARM_STATE = "alarm_state"
private const val KEY_ALARM_SPECIFIC_TIME = "specific_time"
private const val KEY_ALARM_INTERVAL_TIME = "specific_time"

class AlarmPreference(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(KEY_ALARM_PREF, Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    fun isAlarmEnabled() = (ALARM_DISABLED != getAlarmState())

    fun setAlarmState(alarmState: Int): AlarmPreference {
        editor.putInt(KEY_ALARM_STATE, alarmState)
        return this
    }

    fun getAlarmState() = prefs.getInt(KEY_ALARM_STATE, ALARM_DISABLED)

    fun setAlarmTriggerMillis(targetTimeInMillis: Long): AlarmPreference {
        editor.putLong(KEY_ALARM_SPECIFIC_TIME, targetTimeInMillis)
        return this
    }

    fun getAlarmTriggerMillis() = prefs.getLong(KEY_ALARM_SPECIFIC_TIME, 0)

    fun setAlarmIntervalMins(intervalInMins: Int): AlarmPreference {
        editor.putInt(KEY_ALARM_INTERVAL_TIME, intervalInMins)
        return this
    }

    fun getAlarmIntervalMins() = prefs.getInt(KEY_ALARM_INTERVAL_TIME, 0)

    fun commit() {
        editor.apply()
    }
}