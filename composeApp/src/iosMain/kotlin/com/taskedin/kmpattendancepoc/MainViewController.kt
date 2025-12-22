package com.taskedin.kmpattendancepoc

import androidx.compose.ui.window.ComposeUIViewController
import com.taskedin.kmpattendancepoc.di.allModules
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(allModules)
        }
    }
    App()
}