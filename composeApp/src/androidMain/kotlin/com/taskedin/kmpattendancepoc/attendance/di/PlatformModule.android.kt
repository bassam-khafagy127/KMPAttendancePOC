package com.taskedin.kmpattendancepoc.attendance.di

import com.taskedin.kmpattendancepoc.attendance.platform.DeviceIdentifierProvider
import com.taskedin.kmpattendancepoc.attendance.platform.LocationProvider
import com.taskedin.kmpattendancepoc.attendance.platform.SettingsStore
import com.taskedin.kmpattendancepoc.attendance.platform.android.AndroidDeviceIdentifierProvider
import com.taskedin.kmpattendancepoc.attendance.platform.android.AndroidLocationProvider
import com.taskedin.kmpattendancepoc.attendance.platform.android.AndroidSettingsStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module {
    return module {
        single<SettingsStore> { AndroidSettingsStore(context = androidContext()) }
        single<DeviceIdentifierProvider> { AndroidDeviceIdentifierProvider(context = androidContext()) }
        single<LocationProvider> { AndroidLocationProvider(context = androidContext()) }
    }
}


