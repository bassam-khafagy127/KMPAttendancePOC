package com.taskedin.kmpattendancepoc.attendance.di

import com.taskedin.kmpattendancepoc.attendance.data.AttendanceRepositoryImpl
import com.taskedin.kmpattendancepoc.attendance.domain.AttendanceRepository
import com.taskedin.kmpattendancepoc.attendance.domain.usecase.GetLocationUseCase
import com.taskedin.kmpattendancepoc.attendance.domain.usecase.SubmitAttendanceUseCase
import com.taskedin.kmpattendancepoc.attendance.settings.AttendanceSettingsRepository
import com.taskedin.kmpattendancepoc.attendance.settings.DefaultAttendanceSettingsRepository
import com.taskedin.kmpattendancepoc.attendance.ui.AttendanceViewModel
import org.koin.dsl.module

val attendanceModule = module {
    single<AttendanceSettingsRepository> { DefaultAttendanceSettingsRepository(settingsStore = get()) }
    single<AttendanceRepository> {
        AttendanceRepositoryImpl(
            client = get(),
            safeApiCall = get(),
            settingsRepository = get(),
            locationProvider = get()
        )
    }
    factory { GetLocationUseCase(repository = get()) }
    factory { SubmitAttendanceUseCase(repository = get()) }
    factory {
        AttendanceViewModel(
            getLocationUseCase = get(),
            submitAttendanceUseCase = get(),
            settingsRepository = get(),
            deviceIdentifierProvider = get()
        )
    }
}


