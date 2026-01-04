package com.taskedin.kmpattendancepoc

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.taskedin.kmpattendancepoc.attendance.ui.AttendanceScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        AttendanceScreen()
    }
}