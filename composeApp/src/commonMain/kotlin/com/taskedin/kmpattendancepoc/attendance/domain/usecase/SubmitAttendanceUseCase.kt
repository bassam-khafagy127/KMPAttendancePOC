package com.taskedin.kmpattendancepoc.attendance.domain.usecase

import arrow.core.Either
import com.taskedin.kmpattendancepoc.attendance.domain.AttendanceRepository
import com.taskedin.kmpattendancepoc.data.models.ApiResponseV2
import com.taskedin.kmpattendancepoc.data.models.Failure
import kotlinx.serialization.json.JsonElement

class SubmitAttendanceUseCase(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(
        status: Int,
        macAddress: String,
        latitude: Double,
        longitude: Double
    ): Either<Failure, ApiResponseV2<JsonElement?>> {
        return repository.submitAttendance(
            status = status,
            macAddress = macAddress,
            latitude = latitude,
            longitude = longitude
        )
    }
}


