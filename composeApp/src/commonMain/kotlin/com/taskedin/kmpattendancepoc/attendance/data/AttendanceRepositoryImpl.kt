package com.taskedin.kmpattendancepoc.attendance.data

import arrow.core.Either
import com.taskedin.kmpattendancepoc.attendance.domain.AttendanceRepository
import com.taskedin.kmpattendancepoc.attendance.model.AttendanceRequest
import com.taskedin.kmpattendancepoc.attendance.model.GeoLocation
import com.taskedin.kmpattendancepoc.attendance.platform.LocationProvider
import com.taskedin.kmpattendancepoc.attendance.settings.AttendanceSettingsRepository
import com.taskedin.kmpattendancepoc.data.SafeApiCall
import com.taskedin.kmpattendancepoc.data.models.ApiResponseV2
import com.taskedin.kmpattendancepoc.data.models.Failure
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonElement

class AttendanceRepositoryImpl(
    private val client: HttpClient,
    private val safeApiCall: SafeApiCall,
    private val settingsRepository: AttendanceSettingsRepository,
    private val locationProvider: LocationProvider
) : AttendanceRepository {
    override suspend fun getCurrentLocation(): GeoLocation? = locationProvider.getCurrentLocation()

    override suspend fun submitAttendance(
        status: Int,
        macAddress: String,
        latitude: Double,
        longitude: Double
    ): Either<Failure, ApiResponseV2<JsonElement?>> {
        val settings = settingsRepository.getSettings()
        val request = AttendanceRequest(
            status = status,
            macAddress = macAddress,
            latitude = latitude,
            longitude = longitude
        )
        val fullUrl: String = buildEndpointUrl(baseUrl = settings.baseUrl, path = "Attendance/Create")
        return safeApiCall.execute {
            client.post {
                url(fullUrl)
                contentType(ContentType.Application.Json)
                header("devicetype", DEVICE_TYPE)
                header("language", LANGUAGE)
                if (settings.authToken.isNotBlank()) {
                    header("Authorization", "Bearer ${settings.authToken}")
                }
                if (settings.spaceId.isNotBlank()) {
                    header("spaceid", settings.spaceId)
                }
                setBody(request)
            }
        }
    }

    private fun buildEndpointUrl(baseUrl: String, path: String): String {
        val sanitizedBaseUrl: String = baseUrl.trim().trimEnd('/')
        val sanitizedPath: String = path.trim().trimStart('/')
        return "$sanitizedBaseUrl/$sanitizedPath"
    }

    private companion object {
        private const val DEVICE_TYPE: String = "1"
        private const val LANGUAGE: String = "en"
    }
}


