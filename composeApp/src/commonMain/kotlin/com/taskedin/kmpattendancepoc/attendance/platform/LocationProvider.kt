package com.taskedin.kmpattendancepoc.attendance.platform

import com.taskedin.kmpattendancepoc.attendance.model.GeoLocation

interface LocationProvider {
    suspend fun getCurrentLocation(): GeoLocation?
}


