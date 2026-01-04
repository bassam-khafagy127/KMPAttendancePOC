package com.taskedin.kmpattendancepoc.attendance.di

import com.taskedin.kmpattendancepoc.attendance.platform.DeviceIdentifierProvider
import com.taskedin.kmpattendancepoc.attendance.platform.LocationProvider
import com.taskedin.kmpattendancepoc.attendance.platform.SettingsStore
import com.taskedin.kmpattendancepoc.attendance.platform.jvm.JvmLocationProvider
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.UUID

actual fun platformModule(): Module {
    return module {
        single<SettingsStore> { InMemorySettingsStore() }
        single<DeviceIdentifierProvider> { JvmDeviceIdentifierProvider(settingsStore = get()) }
        single<LocationProvider> { JvmLocationProvider(client = get()) }
    }
}

private class InMemorySettingsStore : SettingsStore {
    private val values: MutableMap<String, String> = mutableMapOf()

    override fun getString(key: String, defaultValue: String): String {
        return values[key] ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        values[key] = value
    }
}

private class JvmDeviceIdentifierProvider(
    private val settingsStore: SettingsStore
) : DeviceIdentifierProvider {
    override fun getDeviceIdentifier(): String {
        val existingValue: String = settingsStore.getString(DEVICE_ID_KEY, "")
        if (existingValue.isNotBlank()) return existingValue
        val newValue: String = UUID.randomUUID().toString()
        settingsStore.putString(DEVICE_ID_KEY, newValue)
        return newValue
    }

    private companion object {
        private const val DEVICE_ID_KEY: String = "device_identifier"
    }
}


