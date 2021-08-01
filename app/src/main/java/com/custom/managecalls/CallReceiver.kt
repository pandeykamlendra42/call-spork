package com.custom.managecalls

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import android.provider.ContactsContract
import android.speech.tts.TextToSpeech
import java.util.*
import androidx.core.app.NotificationCompat.getExtras
import android.os.Bundle
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener


class CallReceiver: BroadcastReceiver() {
    private val TAG = "PhoneStatReceiver"

    private var incomingFlag = false
    private var incoming_number: String? = null
    override fun onReceive(context: Context?, intent1: Intent?) {

        val phoneNumber2 = intent1?.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
        Log.i(TAG, "call OUT:" + phoneNumber2)
        if(intent1?.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){

            incomingFlag = false;

            val phoneNumber = intent1?.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            Log.i(TAG, "call OUT:"+phoneNumber);

        } else {

            val tm =
                context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager

            tm.listen(object: PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    super.onCallStateChanged(state, phoneNumber)
                    println("Incoming PhoneNumber ::: $phoneNumber")
                    when (state) {

                        TelephonyManager.CALL_STATE_RINGING -> {
                            incomingFlag = true
                            val bundle = intent1?.extras
                            val intent = Intent(context, MainActivity::class.java)
                            var contactNumber: String? = incoming_number
                            phoneNumber?.let { phoneNumber ->
                                contactNumber = phoneNumber.replaceFirst("+91", "")
                            }
                            intent.putExtra("contactName", getContactName(contactNumber, context!!))
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)

                            Log.i(TAG, " =================================================== RINGING ::: $incoming_number"+ contactNumber)
                            Log.i(TAG, " =================================================== RINGING ::: ${intent1?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)}"+ contactNumber)
                        }

                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            if(incomingFlag){

                                Log.i(TAG, "incoming ACCEPT :"+ incoming_number);

                            }

                        }
                        TelephonyManager.CALL_STATE_IDLE -> {
                            MainActivity.activity?.stop()
                            if(incomingFlag){
                                Log.i(TAG, "incoming IDLE");

                            }

                        }
                    }

                }
            }, PhoneStateListener.LISTEN_CALL_STATE )

        }

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

        return contactName
    }


}