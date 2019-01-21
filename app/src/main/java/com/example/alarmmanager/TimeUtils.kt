package com.example.alarmmanager

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    const val FILE_NAME_DATE_FORMAT = "dd-MM-yyyy"
    const val LOG_DATE_FORMAT = "E MMM dd yyyy 'at' hh:mm:ss:SSS aaa"
    private const val TIME_DISPLAY_FORMAT = "hh:mm a, dd MMM yy"

    fun getTimeToDisplay(timeInMillis: Long): String {
        if (0L == timeInMillis) return ""
        val formatter = SimpleDateFormat(TIME_DISPLAY_FORMAT, Locale.getDefault())
        val date = Date(timeInMillis)
        return formatter.format(date)
    }
}