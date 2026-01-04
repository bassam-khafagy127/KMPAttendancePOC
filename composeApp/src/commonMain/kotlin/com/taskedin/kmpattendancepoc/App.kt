package com.taskedin.kmpattendancepoc

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import com.taskedin.kmpattendancepoc.attendance.ui.AttendanceScreen
import com.taskedin.kmpattendancepoc.attendance.ui.AttendanceViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel: AttendanceViewModel = koinInject()
        AttendanceScreen(viewModel = viewModel)
    }
}