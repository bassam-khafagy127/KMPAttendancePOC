package com.taskedin.kmpattendancepoc.attendance.settings

import kotlinx.coroutines.flow.StateFlow

interface AttendanceSettingsRepository {
    val settingsFlow: StateFlow<AttendanceSettings>
    fun getSettings(): AttendanceSettings
    fun saveSettings(settings: AttendanceSettings)
}


