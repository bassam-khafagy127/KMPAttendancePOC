package com.taskedin.kmpattendancepoc.attendance.di

import com.taskedin.kmpattendancepoc.attendance.model.GeoLocation
import com.taskedin.kmpattendancepoc.attendance.platform.DeviceIdentifierProvider
import com.taskedin.kmpattendancepoc.attendance.platform.LocationProvider
import com.taskedin.kmpattendancepoc.attendance.platform.SettingsStore
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

actual fun platformModule(): Module {
    return module {
        single<SettingsStore> { IosSettingsStore(userDefaults = NSUserDefaults.standardUserDefaults) }
        single<DeviceIdentifierProvider> { IosDeviceIdentifierProvider() }
        single<LocationProvider> { IosLocationProvider() }
    }
}

private class IosSettingsStore(
    private val userDefaults: NSUserDefaults
) : SettingsStore {
    override fun getString(key: String, defaultValue: String): String {
        val storedValue: String? = userDefaults.stringForKey(key)
        return storedValue ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }
}

private class IosDeviceIdentifierProvider : DeviceIdentifierProvider {
    override fun getDeviceIdentifier(): String {
        val identifier = UIDevice.currentDevice.identifierForVendor?.UUIDString
        return identifier ?: "unknown-ios-device"
    }
}

private class IosLocationProvider : LocationProvider {
    override suspend fun getCurrentLocation(): GeoLocation? = null
}


