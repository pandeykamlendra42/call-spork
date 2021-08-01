package com.custom.managecalls

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private var pref: SharedPreferences =
        context.getSharedPreferences(AppConstants.APP_PREFERENCES_NAME, AppConstants.APP_PREFERENCES_MODE)
    private var editor: SharedPreferences.Editor = pref.edit()

    fun getIncomingTone(): String {
        return pref.getString(AppConstants.APP_CALLING_MESSAGE, "Hey, ${AppConstants.APP_CONTACT_NAME} is calling you.") ?:
                "Hey, ${AppConstants.APP_CONTACT_NAME} is calling you."
    }

    fun setIncomingTone(incomingTone: String) {
        editor.putString(AppConstants.APP_CALLING_MESSAGE, incomingTone)
        editor.commit()
    }

    fun clearPreferences() {
        editor.clear()
        editor.commit()
    }
}