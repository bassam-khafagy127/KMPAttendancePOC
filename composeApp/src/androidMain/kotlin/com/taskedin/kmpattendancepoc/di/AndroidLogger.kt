package com.taskedin.kmpattendancepoc.di

import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.SIMPLE

actual fun getLogger(): Logger {
    return Logger.SIMPLE

//    return object : Logger {
//        override fun log(message: String) {
//            Log.d("Ktor", message)
//        }
//    }
}

