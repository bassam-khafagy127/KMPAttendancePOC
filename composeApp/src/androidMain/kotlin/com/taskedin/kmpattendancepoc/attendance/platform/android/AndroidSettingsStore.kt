package com.taskedin.kmpattendancepoc.attendance.platform.android

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.taskedin.kmpattendancepoc.attendance.platform.SettingsStore

class AndroidSettingsStore(
    context: Context
) : SettingsStore {
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    private companion object {
        private const val PREFERENCES_NAME: String = "attendance_settings"
    }
}


