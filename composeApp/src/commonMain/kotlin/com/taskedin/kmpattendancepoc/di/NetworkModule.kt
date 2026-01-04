package com.taskedin.kmpattendancepoc.di

import com.taskedin.kmpattendancepoc.data.AttendanceRepositoryImpl
import com.taskedin.kmpattendancepoc.data.SafeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = false
                    }
                )
            }
        }
    }
    
    single { SafeApiCall }
    
    single {
        AttendanceRepositoryImpl(
            client = get(),
            safeApiCall = get()
        )
    }
}

