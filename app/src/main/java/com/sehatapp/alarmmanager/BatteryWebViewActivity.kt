package com.sehatapp.alarmmanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_battery_web_view.*

class BatteryWebViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_web_view)
        web_view_instructions.loadUrl("file:///android_asset/battery.html")
    }
}