package com.taskedin.kmpattendancepoc.di

import com.taskedin.kmpattendancepoc.Greeting
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::Greeting)
}

val allModules = listOf(
    networkModule,
    appModule
)

