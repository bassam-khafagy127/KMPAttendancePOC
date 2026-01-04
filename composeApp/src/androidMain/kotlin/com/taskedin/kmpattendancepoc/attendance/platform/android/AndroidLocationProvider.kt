package com.taskedin.kmpattendancepoc.attendance.platform.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.taskedin.kmpattendancepoc.attendance.model.GeoLocation
import com.taskedin.kmpattendancepoc.attendance.platform.LocationProvider
import kotlinx.coroutines.tasks.await
import androidx.core.app.ActivityCompat

class AndroidLocationProvider(
    private val context: Context
) : LocationProvider {
    override suspend fun getCurrentLocation(): GeoLocation? {
        val applicationContext: Context = context.applicationContext
        if (!hasLocationPermission(applicationContext)) return null
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
            val cancellationTokenSource = CancellationTokenSource()
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()
            if (location == null) return null
            GeoLocation(latitude = location.latitude, longitude = location.longitude)
        } catch (err: Exception) {
            err.printStackTrace()
            null
        }
    }

    private fun hasLocationPermission(context: Context): Boolean {
        val fineStatus: Int = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseStatus: Int = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineStatus == PackageManager.PERMISSION_GRANTED ||
            coarseStatus == PackageManager.PERMISSION_GRANTED
    }
}


