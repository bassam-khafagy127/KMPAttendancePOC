package com.taskedin.kmpattendancepoc.attendance.platform

interface SettingsStore {
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
}


