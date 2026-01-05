package com.taskedin.kmpattendancepoc.di

import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.SIMPLE

actual fun getLogger(): Logger {
    return Logger.SIMPLE
}

