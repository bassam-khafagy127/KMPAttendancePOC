package com.taskedin.kmpattendancepoc.attendance.platform.android

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.taskedin.kmpattendancepoc.attendance.platform.DeviceIdentifierProvider
import java.net.NetworkInterface

class AndroidDeviceIdentifierProvider(
    private val context: Context
) : DeviceIdentifierProvider {
    @SuppressLint("HardwareIds")
    override fun getDeviceIdentifier(): String {
        val applicationContext: Context = context.applicationContext
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } else {
            NetworkInterface.getNetworkInterfaces()
                ?.toList()
                ?.firstOrNull { networkInterface -> networkInterface.name.equals("wlan0", ignoreCase = true) }
                ?.hardwareAddress
                ?.joinToString(":") { byteValue -> "%02X".format(byteValue) }
                ?: DEFAULT_MAC_ADDRESS
        }
    }

    private companion object {
        private const val DEFAULT_MAC_ADDRESS: String = "00:00:00:00:00:00"
    }
}


