package com.taskedin.kmpattendancepoc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.taskedin.kmpattendancepoc.di.allModules
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun main() {
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(allModules)
        }
    }
    
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KMPAttendancePOC",
        ) {
            App()
        }
    }
}