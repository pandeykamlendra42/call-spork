package com.custom.managecalls

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    var textToSpeech: TextToSpeech? = null

    companion object {
        var activity: MainActivity? = null
    }

    var timer: Timer? = null
    var mServiceConnection: ServiceConnection? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clMainContainer.setOnClickListener {
            startActivity(Intent(this, IncomingCallActivity::class.java))
        }
        mServiceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                println("service data disconnected")
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                println("service data connected")
            }
        }
        etCallingSpeech.setText(PreferencesManager(this).getIncomingTone())
        val drawable = btnSaveChanges.background?.mutate()
        drawable?.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            ), PorterDuff.Mode.SRC_ATOP
        )

        btnSaveChanges.setOnClickListener {
            val message = etCallingSpeech.text.toString()
            if (message.isNotEmpty() && message.contains(AppConstants.APP_CONTACT_NAME)) {
                PreferencesManager(this).setIncomingTone(message)
                tvErrorMessage.visibility = View.GONE
                etCallingSpeech.clearFocus()
            } else {
                tvErrorMessage.visibility = View.VISIBLE
            }
        }
        val mCallServiceIntent = Intent(this, YourCallScreeningServiceImplementation::class.java)
        startService(Intent(this, YourCallScreeningServiceImplementation::class.java))
        if (!isServiceRunning(YourCallScreeningServiceImplementation::class.java.name)) {
            bindService(mCallServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }


    fun stop() {
        timer?.cancel()
        textToSpeech?.let { textToSpeech ->
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        textToSpeech = null
        timer = null
        activity = null
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    private fun isServiceRunning(serviceClassName: String): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Integer.MAX_VALUE)
        for (runningServiceInfo in services) {
            if (runningServiceInfo.service.className.equals(serviceClassName)) {
                return true
            }
        }
        return false
    }
}
