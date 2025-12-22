package com.taskedin.kmpattendancepoc

import android.app.Application
import com.taskedin.kmpattendancepoc.di.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KMPAttendanceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KMPAttendanceApplication)
            modules(allModules)
        }
    }
}

