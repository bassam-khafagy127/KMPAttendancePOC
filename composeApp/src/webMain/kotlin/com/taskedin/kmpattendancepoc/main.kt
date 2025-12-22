package com.taskedin.kmpattendancepoc

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.taskedin.kmpattendancepoc.di.allModules
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(allModules)
        }
    }
    
    ComposeViewport {
        App()
    }
}