package com.taskedin.kmpattendancepoc

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform