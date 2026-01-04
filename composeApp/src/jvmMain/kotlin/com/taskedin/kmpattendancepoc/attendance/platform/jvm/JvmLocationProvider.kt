package com.taskedin.kmpattendancepoc.attendance.platform.jvm

import com.taskedin.kmpattendancepoc.attendance.model.GeoLocation
import com.taskedin.kmpattendancepoc.attendance.platform.LocationProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class JvmLocationProvider(
    private val client: HttpClient
) : LocationProvider {
    override suspend fun getCurrentLocation(): GeoLocation? {
        return try {
            withContext(Dispatchers.IO) {
                withTimeout(LOCATION_REQUEST_TIMEOUT_MS) {
                    val response: IpApiResponse = client.get(IP_API_URL).body()
                    val latitude: Double = response.latitude ?: return@withTimeout null
                    val longitude: Double = response.longitude ?: return@withTimeout null
                    GeoLocation(latitude = latitude, longitude = longitude)
                }
            }
        } catch (err: Exception) {
            err.printStackTrace()
            null
        }
    }

    @Serializable
    private data class IpApiResponse(
        @SerialName("latitude") val latitude: Double? = null,
        @SerialName("longitude") val longitude: Double? = null
    )

    private companion object {
        private const val IP_API_URL: String = "https://ipapi.co/json/"
        private const val LOCATION_REQUEST_TIMEOUT_MS: Long = 5_000L
    }
}


