package com.taskedin.kmpattendancepoc.attendance.domain

import arrow.core.Either
import com.taskedin.kmpattendancepoc.attendance.model.GeoLocation
import com.taskedin.kmpattendancepoc.data.models.ApiResponseV2
import com.taskedin.kmpattendancepoc.data.models.Failure
import kotlinx.serialization.json.JsonElement

interface AttendanceRepository {
    suspend fun getCurrentLocation(): GeoLocation?
    suspend fun submitAttendance(
        status: Int,
        macAddress: String,
        latitude: Double,
        longitude: Double
    ): Either<Failure, ApiResponseV2<JsonElement?>>
}


