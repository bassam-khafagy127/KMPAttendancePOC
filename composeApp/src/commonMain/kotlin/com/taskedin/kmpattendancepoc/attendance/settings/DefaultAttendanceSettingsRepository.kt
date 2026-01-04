package com.taskedin.kmpattendancepoc.attendance.settings

import com.taskedin.kmpattendancepoc.attendance.platform.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultAttendanceSettingsRepository(
    private val settingsStore: SettingsStore
) : AttendanceSettingsRepository {
    private val initialSettings: AttendanceSettings = AttendanceSettings(
        baseUrl = settingsStore.getString(KEY_BASE_URL, DEFAULT_BASE_URL),
        spaceId = settingsStore.getString(KEY_SPACE_ID, ""),
        authToken = settingsStore.getString(KEY_AUTH_TOKEN, "")
    )

    private val internalSettingsFlow: MutableStateFlow<AttendanceSettings> =
        MutableStateFlow(initialSettings)

    override val settingsFlow: StateFlow<AttendanceSettings> =
        internalSettingsFlow.asStateFlow()

    override fun getSettings(): AttendanceSettings = internalSettingsFlow.value

    override fun saveSettings(settings: AttendanceSettings) {
        settingsStore.putString(KEY_BASE_URL, settings.baseUrl)
        settingsStore.putString(KEY_SPACE_ID, settings.spaceId)
        settingsStore.putString(KEY_AUTH_TOKEN, settings.authToken)
        internalSettingsFlow.value = settings
    }

    private companion object {
        private const val KEY_BASE_URL: String = "base_url"
        private const val KEY_SPACE_ID: String = "space_id"
        private const val KEY_AUTH_TOKEN: String = "auth_token"
        private const val DEFAULT_BASE_URL: String = "https://env.taskedin.net/api"
    }
}


