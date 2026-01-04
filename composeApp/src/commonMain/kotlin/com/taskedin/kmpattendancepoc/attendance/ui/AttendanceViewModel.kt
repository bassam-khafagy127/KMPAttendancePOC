package com.taskedin.kmpattendancepoc.attendance.ui

import arrow.core.Either
import com.taskedin.kmpattendancepoc.attendance.domain.usecase.GetLocationUseCase
import com.taskedin.kmpattendancepoc.attendance.domain.usecase.SubmitAttendanceUseCase
import com.taskedin.kmpattendancepoc.attendance.platform.DeviceIdentifierProvider
import com.taskedin.kmpattendancepoc.attendance.settings.AttendanceSettings
import com.taskedin.kmpattendancepoc.attendance.settings.AttendanceSettingsRepository
import com.taskedin.kmpattendancepoc.attendance.ui.mvi.AttendanceEvent
import com.taskedin.kmpattendancepoc.attendance.ui.mvi.AttendanceState
import com.taskedin.kmpattendancepoc.data.models.ApiResponseV2
import com.taskedin.kmpattendancepoc.data.models.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement

class AttendanceViewModel(
    private val getLocationUseCase: GetLocationUseCase,
    private val submitAttendanceUseCase: SubmitAttendanceUseCase,
    private val settingsRepository: AttendanceSettingsRepository,
    private val deviceIdentifierProvider: DeviceIdentifierProvider
) {
    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val internalState: MutableStateFlow<AttendanceState> =
        MutableStateFlow(createInitialState())
    val uiState: StateFlow<AttendanceState> = internalState.asStateFlow()

    init {
        internalState.update { it.copy(deviceIdentifier = deviceIdentifierProvider.getDeviceIdentifier()) }
        onEvent(AttendanceEvent.LoadLocation)
    }

    fun onEvent(event: AttendanceEvent) {
        when (event) {
            is AttendanceEvent.LoadLocation -> executeLoadLocation()
            is AttendanceEvent.SubmitAttendance -> executeSubmitAttendance()
            is AttendanceEvent.SelectCheckState -> internalState.update {
                it.copy(selectedCheckState = event.checkState)
            }
            is AttendanceEvent.SaveSettings -> executeSaveSettings(event)
            is AttendanceEvent.ClearMessages -> internalState.update {
                it.copy(apiError = null, apiSuccess = null)
            }
        }
    }

    fun clear() {
        coroutineScope.cancel()
    }

    private fun executeLoadLocation() {
        coroutineScope.launch {
            internalState.update { it.copy(isLoadingLocation = true, locationError = null) }
            val location = getLocationUseCase()
            internalState.update {
                if (location == null) {
                    it.copy(
                        currentLocation = null,
                        isLoadingLocation = false,
                        locationError = "Failed to get location. Please check permissions."
                    )
                } else {
                    it.copy(
                        currentLocation = location,
                        isLoadingLocation = false,
                        locationError = null
                    )
                }
            }
        }
    }

    private fun executeSubmitAttendance() {
        val location = internalState.value.currentLocation
        if (location == null) {
            internalState.update { it.copy(apiError = "Location not available. Please try again.") }
            return
        }
        val state = internalState.value
        coroutineScope.launch {
            internalState.update { it.copy(isSubmitting = true, apiError = null, apiSuccess = null) }
            val response: Either<Failure, ApiResponseV2<JsonElement?>> = submitAttendanceUseCase(
                status = state.selectedCheckState.value,
                macAddress = state.deviceIdentifier,
                latitude = location.latitude,
                longitude = location.longitude
            )
            when (response) {
                is Either.Left -> internalState.update {
                    it.copy(isSubmitting = false, apiError = mapFailureToMessage(response.value))
                }
                is Either.Right -> {
                    val apiResponse = response.value
                    val isSuccess: Boolean = apiResponse.success == true
                    val message: String? = apiResponse.message
                    if (isSuccess) {
                        internalState.update {
                            it.copy(isSubmitting = false, apiSuccess = "Attendance submitted successfully!")
                        }
                    } else {
                        internalState.update {
                            it.copy(
                                isSubmitting = false,
                                apiError = "Failed to submit attendance: ${message ?: "Unknown error"}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun executeSaveSettings(event: AttendanceEvent.SaveSettings) {
        val settings = AttendanceSettings(
            baseUrl = event.baseUrl.trim(),
            spaceId = event.spaceId.trim(),
            authToken = event.authToken.trim()
        )
        settingsRepository.saveSettings(settings)
        internalState.update {
            it.copy(
                baseUrl = settings.baseUrl,
                spaceId = settings.spaceId,
                authToken = settings.authToken,
                apiSuccess = "Settings saved."
            )
        }
    }

    private fun createInitialState(): AttendanceState {
        val settings: AttendanceSettings = settingsRepository.getSettings()
        return AttendanceState(
            baseUrl = settings.baseUrl,
            spaceId = settings.spaceId,
            authToken = settings.authToken
        )
    }

    private fun mapFailureToMessage(failure: Failure): String {
        return when (failure) {
            is Failure.HttpFailure.BadRequest -> failure.message ?: "Bad request"
            is Failure.HttpFailure.Unauthorized -> failure.message ?: "Unauthorized"
            is Failure.HttpFailure.Forbidden -> failure.message ?: "Forbidden"
            is Failure.HttpFailure.NotFound -> failure.message ?: "Not found"
            is Failure.HttpFailure.Conflict -> failure.message ?: "Conflict"
            is Failure.HttpFailure.TooManyRequests -> failure.message ?: "Too many requests"
            is Failure.HttpFailure.ServerFailure -> failure.message ?: "Server error (${failure.code})"
            is Failure.HttpFailure.Unknown -> failure.message ?: "Unexpected error (${failure.code})"
            is Failure.LocalFailure -> failure.name
            is Failure.RemoteFailure -> failure.name
            is Failure.RemoteFailureWithCode -> failure.error ?: "Remote error (${failure.code})"
        }
    }
}


