package com.taskedin.kmpattendancepoc.attendance.ui.mvi

import com.taskedin.kmpattendancepoc.attendance.model.CheckState
import com.taskedin.kmpattendancepoc.attendance.model.GeoLocation

data class AttendanceState(
    val currentLocation: GeoLocation? = null,
    val isLoadingLocation: Boolean = false,
    val locationError: String? = null,
    val isSubmitting: Boolean = false,
    val apiError: String? = null,
    val apiSuccess: String? = null,
    val deviceIdentifier: String = "",
    val selectedCheckState: CheckState = CheckState.CHECK_IN,
    val baseUrl: String = "",
    val spaceId: String = "",
    val authToken: String = ""
)

sealed class AttendanceEvent {
    data object LoadLocation : AttendanceEvent()
    data object ClearMessages : AttendanceEvent()
    data object SubmitAttendance : AttendanceEvent()
    data class SelectCheckState(val checkState: CheckState) : AttendanceEvent()
    data class SaveSettings(val baseUrl: String, val spaceId: String, val authToken: String) :
        AttendanceEvent()
}


