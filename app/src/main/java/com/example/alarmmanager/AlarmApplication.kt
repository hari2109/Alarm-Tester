package com.example.alarmmanager

import android.app.Application
import timber.log.Timber

class AlarmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        /*if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(FileLoggingTree())
        }*/
        Timber.plant(FileLoggingTree(this))
    }
}