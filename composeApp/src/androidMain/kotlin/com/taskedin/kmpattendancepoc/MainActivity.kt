package com.taskedin.kmpattendancepoc

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import android.widget.Toast

class MainActivity : ComponentActivity() {
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _: Map<String, Boolean> ->
        // Location will be loaded on demand from the UI (Refresh Location button),
        // or automatically on next app launch.
    }

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(
                this,
                "Notification permission denied; the app may not show notifications.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        requestLocationPermissions()
        requestNotificationPermissionIfNeeded()

        setContent {
            App()
        }
    }

    private fun requestLocationPermissions() {
        val fineLocationPermissionStatus: Int =
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermissionStatus: Int =
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        val hasAnyLocationPermission: Boolean =
            fineLocationPermissionStatus == PackageManager.PERMISSION_GRANTED ||
                coarseLocationPermissionStatus == PackageManager.PERMISSION_GRANTED
        if (hasAnyLocationPermission) return
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val notificationPermissionStatus: Int =
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
        if (notificationPermissionStatus == PackageManager.PERMISSION_GRANTED) return
        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}