package com.custom.managecalls

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.speech.tts.TextToSpeech
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import java.util.*
import kotlin.concurrent.timerTask
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent




class YourCallScreeningServiceImplementation : CallScreeningService(), TextToSpeech.OnInitListener,
TextToSpeech.OnUtteranceCompletedListener{
    override fun onUtteranceCompleted(utteranceId: String?) {
        stopSelf()
    }

    override fun onCreate() {
        super.onCreate()
        println("onCreate Data :: status ")

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            println("onInit Data :: status $status")
            val result = textToSpeech?.setLanguage(Locale.US)
        } else {
            println("onInit else Data :: status $status")
        }
    }

    var incomingFlag = false
    var incoming_number = ""
    val TAG = "CallScreeningService"
    var textToSpeech: TextToSpeech? = null
    var timer: Timer? = null
    val onlineSpeech  =  Bundle()
    var recognizer: SpeechRecognizer? = null

    override fun onScreenCall(callDetails: Call.Details?) {

        println(" Data Set Calling ....... ${callDetails?.handle}")
        onlineSpeech.putBoolean(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, true)

        var callingNumber = callDetails?.handle.toString().replaceFirst("tel:", "")
        callingNumber = callingNumber.replaceFirst("%2B91", "")
        val tm =
            applicationContext?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        if(timer == null) {
            textToSpeech = TextToSpeech(this, this)
        }
        tm.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)
                println("Incoming PhoneNumber ::: $phoneNumber")
                val incomingTune = PreferencesManager(applicationContext).getIncomingTone()
                when (state) {

                    TelephonyManager.CALL_STATE_RINGING -> {
                        incomingFlag = true

                        val callingName = getContactName(callingNumber, applicationContext)
                        if (timer == null) {
                            timer = Timer("repeat")
                            timer?.schedule(timerTask {
                                textToSpeech?.speak(
                                    incomingTune.replace(AppConstants.APP_CONTACT_NAME, "$callingName"),
                                    TextToSpeech.QUEUE_FLUSH,
                                    onlineSpeech,
                                    "123456"
                                )
                            }, 1100, 5000)
                        }
                        //startVoiceListener()
                        Log.i(
                            TAG, " =================================================== " +
                                    "RINGING ::: $incoming_number" + callingNumber
                        )
                    }

                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        if (incomingFlag) {

                            Log.i(TAG, "incoming ACCEPT :" + incoming_number)

                        }
                        if (timer != null) {
                            textToSpeech?.let {
                                it.stop()
                                it.shutdown()
                            }
                            timer?.cancel()
                            timer = null
                            recognizer?.stopListening()
                        }
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (timer != null) {
                            textToSpeech?.let {
                                it.stop()
                                it.shutdown()
                            }
                            timer?.cancel()
                            timer = null
                            recognizer?.stopListening()
                        }
                        if (incomingFlag) {
                            Log.i(TAG, "incoming IDLE")

                        }

                    }
                }

            }
        }, PhoneStateListener.LISTEN_CALL_STATE)

    }

    private fun getContactName(phoneNumber: String?, context: Context): String {

        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        var contactName = ""
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }

        if (contactName.isEmpty()) {
            contactName = "Unknown Number"
        }
        return contactName
    }

    override fun onDestroy() {
        println("onDestroy onDestroy")
        textToSpeech?.let {
            it.stop()
            it.shutdown()
            timer?.cancel()
        }
    }

    private fun startVoiceListener() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            "com.custom.managecalls"
        )

        recognizer = SpeechRecognizer
            .createSpeechRecognizer(this.applicationContext)
        val listener = object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val voiceResults = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (voiceResults == null) {
                    println("No voice results")
                } else {
                    println("Printing matches: ")
                    for (match in voiceResults) {
                        println(match)
                    }
                }
            }

            override fun onReadyForSpeech(params: Bundle) {
                println("Ready for speech")
            }

            /**
             * ERROR_NETWORK_TIMEOUT = 1;
             * ERROR_NETWORK = 2;
             * ERROR_AUDIO = 3;
             * ERROR_SERVER = 4;
             * ERROR_CLIENT = 5;
             * ERROR_SPEECH_TIMEOUT = 6;
             * ERROR_NO_MATCH = 7;
             * ERROR_RECOGNIZER_BUSY = 8;
             * ERROR_INSUFFICIENT_PERMISSIONS = 9;
             *
             * @param error code is defined in SpeechRecognizer
             */
            override fun onError(error: Int) {
                System.err.println("Error listening for speech: $error")
            }

            override fun onBeginningOfSpeech() {
                println("Speech starting")
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // TODO Auto-generated method stub

            }

            override fun onEndOfSpeech() {
                // TODO Auto-generated method stub

            }

            override fun onEvent(eventType: Int, params: Bundle) {
                // TODO Auto-generated method stub

            }

            override fun onPartialResults(partialResults: Bundle) {
                // TODO Auto-generated method stub

            }

            override fun onRmsChanged(rmsdB: Float) {
                // TODO Auto-generated method stub

            }
        }
        recognizer?.setRecognitionListener(listener)
        recognizer?.startListening(intent)

    }
}