package com.taskedin.kmpattendancepoc.attendance.model

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRequest(
    val status: Int,
    val macAddress: String,
    val latitude: Double,
    val longitude: Double
)


