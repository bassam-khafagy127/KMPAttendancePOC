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
                    val response: IpInfoResponse = client.get(IP_INFO_API_URL).body()
                    val location: String = response.loc ?: return@withTimeout null
                    val coordinates: List<String> = location.split(",")
                    if (coordinates.size != 2) return@withTimeout null
                    val latitude: Double = coordinates[0].toDoubleOrNull() ?: return@withTimeout null
                    val longitude: Double = coordinates[1].toDoubleOrNull() ?: return@withTimeout null
                    GeoLocation(latitude = latitude, longitude = longitude)
                }
            }
        } catch (err: Exception) {
            err.printStackTrace()
            null
        }
    }

    @Serializable
    private data class IpInfoResponse(
        @SerialName("ip") val ip: String? = null,
        @SerialName("city") val city: String? = null,
        @SerialName("region") val region: String? = null,
        @SerialName("country") val country: String? = null,
        @SerialName("loc") val loc: String? = null,
        @SerialName("org") val org: String? = null,
        @SerialName("timezone") val timezone: String? = null
    )

    private companion object {
        private const val IP_INFO_API_URL: String = "https://ipinfo.io/json"
        private const val LOCATION_REQUEST_TIMEOUT_MS: Long = 5_000L
    }
}


