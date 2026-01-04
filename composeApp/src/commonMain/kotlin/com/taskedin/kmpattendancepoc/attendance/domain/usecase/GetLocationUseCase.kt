package com.taskedin.kmpattendancepoc.attendance.domain.usecase

import com.taskedin.kmpattendancepoc.attendance.domain.AttendanceRepository
import com.taskedin.kmpattendancepoc.attendance.model.GeoLocation

class GetLocationUseCase(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(): GeoLocation? = repository.getCurrentLocation()
}


