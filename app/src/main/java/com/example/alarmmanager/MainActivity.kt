package com.example.alarmmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var alarmPreference: AlarmPreference
    private var timerDialog: TimePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUpNotifChannel()
        alarmPreference = AlarmPreference(this)
        initViews()
        initListeners()
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Sending feedback to developer", Snackbar.LENGTH_LONG).show()
            Timber.d("Send feedback clicked")
            openFeedbackMail()
        }
        bt_battery_op.setOnClickListener {
            Timber.d("Battery doc opened")
            startActivity(Intent(this@MainActivity, BatteryWebViewActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_send_log -> {
                Timber.d("Send feedback clicked")
                openFeedbackMail()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpNotifChannel() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager? ?: return
        val reminderChannel = NotificationChannel(CHANNEL_REMINDER, getString(R.string.reminder), NotificationManager.IMPORTANCE_HIGH)
        reminderChannel.enableLights(true)
        reminderChannel.enableVibration(true)
        mNotificationManager.createNotificationChannel(reminderChannel)
    }

    private fun initListeners() {
        switch_alarm.setOnCheckedChangeListener { button, isChecked ->
            if (!button.isPressed) return@setOnCheckedChangeListener
            if (!isChecked) {
                Timber.d("Alarm disabled")
                AlarmUtils.cancelAllAlarms(this@MainActivity)
                alarmPreference.setAlarmState(ALARM_DISABLED).commit()
            } else {
                Timber.d("Alarm enabled")
            }
            rg_alarm_options.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        rg_alarm_options.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.rb_trigger_interval -> {
                    Timber.d("Show interval picker")
                    showMinuteList()
                }

                R.id.rb_trigger_specific_time -> {
                    Timber.d("Show time picker")
                    showTimePicker()
                }
            }
        }
    }

    private fun showMinuteList() {
        val array = resources.getStringArray(R.array.minutes_list)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.interval)
            .setItems(array) { _, selectedIndex ->
                val selectedInterval = array[selectedIndex].toInt()
                Timber.d("Interval alarm selected. Interval: $selectedInterval minutes")
                alarmPreference.setAlarmState(ALARM_INTERVAL_BASED)
                    .setAlarmIntervalMins(selectedInterval)
                    .commit()

                rb_trigger_interval.text = getString(R.string.trigger_at_minutes, selectedInterval)
                setAlarmsIfNeeded(true)
            }
        builder.create().show()
    }

    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        if (timerDialog != null) {
            if (timerDialog?.isShowing == false) {
                timerDialog?.show()
            }
        }
        timerDialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, selectedHour)
                today.set(Calendar.MINUTE, selectedMinute)

                alarmPreference.setAlarmState(ALARM_SPECIFIC_TIME)
                    .setAlarmTriggerMillis(today.timeInMillis)
                    .commit()

                rb_trigger_specific_time.text = getString(R.string.trigger_specific_time,
                    TimeUtils.getTimeToDisplay(alarmPreference.getAlarmTriggerMillis()))
                Timber.d("Time alarm selected. Time to trigger: ${TimeUtils.getTimeToDisplay(alarmPreference.getAlarmTriggerMillis())}")
                setAlarmsIfNeeded(true)
            }, hour, minute, false)// true for 24 hour time
        timerDialog?.setTitle("")
        timerDialog?.show()
    }

    private fun initViews() {
        if (!alarmPreference.isAlarmEnabled()) {
            switch_alarm.isChecked = false
            rg_alarm_options.visibility = View.GONE
            return
        }
        switch_alarm.isChecked = true
        rg_alarm_options.visibility = View.VISIBLE
        val alarmState = alarmPreference.getAlarmState()
        if (ALARM_SPECIFIC_TIME == alarmState) {
            rb_trigger_specific_time.isChecked = true
            rb_trigger_specific_time.text = getString(R.string.trigger_specific_time,
                TimeUtils.getTimeToDisplay(alarmPreference.getAlarmTriggerMillis()))
            setAlarmsIfNeeded(false)
        }

        if (ALARM_INTERVAL_BASED == alarmState) {
            rb_trigger_interval.isChecked = true
            val intervalMins = alarmPreference.getAlarmIntervalMins()
            rb_trigger_interval.text = getString(R.string.trigger_at_minutes, intervalMins)
            setAlarmsIfNeeded(false)
        }
    }

    private fun setAlarmsIfNeeded(force: Boolean) {
       AlarmUtils.setAlarmsIfNeeded(this, alarmPreference, force)
    }

    private fun openFeedbackMail() {
        val logEmailIntent = Intent(Intent.ACTION_SEND)
        logEmailIntent.type = "text/plain" //NON-NLS
        logEmailIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf("android-support@healthify.co")
        )
        val emailBody: String = getString(R.string.reminder_mail_body)
        val emailSubject = getString(R.string.reminder_mail_subject)
        logEmailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        logEmailIntent.putExtra(Intent.EXTRA_TEXT, emailBody)

        val root = File(filesDir.absolutePath)

        val fileNameTimeStamp = SimpleDateFormat(TimeUtils.FILE_NAME_DATE_FORMAT, Locale.getDefault()).format(Date())
        val pathToMyAttachedFile = "alarm-$fileNameTimeStamp.html"
        val file = File(root, pathToMyAttachedFile)
        if (!file.exists() || !file.canRead()) {
            return
        }
        val uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".com.example.alarmmanager.provider", file)
        logEmailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        logEmailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val mailer = Intent.createChooser(logEmailIntent, getString(R.string.choose_email_client))
        startActivity(mailer)
    }
}
