package com.taskedin.kmpattendancepoc.attendance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taskedin.kmpattendancepoc.attendance.model.CheckState
import com.taskedin.kmpattendancepoc.attendance.ui.mvi.AttendanceEvent
import com.taskedin.kmpattendancepoc.attendance.ui.mvi.AttendanceState
import org.koin.compose.koinInject
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
) {
    val viewModel: AttendanceViewModel = koinInject()

    DisposableEffect(viewModel) {
        onDispose { viewModel.clear() }
    }
    var isSettingsDialogVisible: Boolean by remember { mutableStateOf(false) }
    val uiState: AttendanceState by viewModel.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var isCheckStateExpanded: Boolean by remember { mutableStateOf(false) }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { isSettingsDialogVisible = true },
//                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Settings")
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier.fillMaxSize().systemBarsPadding().padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Attendance POC",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Row {
                                Text(
                                    text = "Current Location",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(
                                    onClick = { viewModel.onEvent(AttendanceEvent.LoadLocation) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "Refresh Location"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            when {
                                uiState.isLoadingLocation -> CircularProgressIndicator(
                                    modifier = Modifier.size(
                                        24.dp
                                    )
                                )

                                uiState.locationError != null -> Text(
                                    text = uiState.locationError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                uiState.currentLocation != null -> {
                                    val location = uiState.currentLocation
                                    Text(
                                        text = "Latitude: ${formatSixDecimals(location?.latitude)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Longitude: ${formatSixDecimals(location?.longitude)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                else -> Text(
                                    text = "Location not available",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ExposedDropdownMenuBox(
                        expanded = isCheckStateExpanded,
                        onExpandedChange = { isCheckStateExpanded = !isCheckStateExpanded }
                    ) {
                        val selectedText: String = when (uiState.selectedCheckState) {
                            CheckState.CHECK_IN -> "Check In"
                            CheckState.CHECK_OUT -> "Check Out"
                        }
                        TextField(
                            value = selectedText,
                            onValueChange = { _: String -> },
                            readOnly = true,
                            label = { Text("Select Check State") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCheckStateExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = isCheckStateExpanded,
                            onDismissRequest = { isCheckStateExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Check In") },
                                onClick = {
                                    viewModel.onEvent(AttendanceEvent.SelectCheckState(CheckState.CHECK_IN))
                                    isCheckStateExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Check Out") },
                                onClick = {
                                    viewModel.onEvent(AttendanceEvent.SelectCheckState(CheckState.CHECK_OUT))
                                    isCheckStateExpanded = false
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.onEvent(AttendanceEvent.SubmitAttendance) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !uiState.isSubmitting && uiState.currentLocation != null
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            val buttonText: String =
                                if (uiState.selectedCheckState == CheckState.CHECK_IN) {
                                    "Check In"
                                } else {
                                    "Check Out"
                                }
                            Text(text = buttonText, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.onEvent(AttendanceEvent.LoadLocation) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoadingLocation
                    ) {
                        Text("Refresh Location")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    uiState.apiSuccess?.let { message ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = message,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    uiState.apiError?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                TextButton(onClick = { viewModel.onEvent(AttendanceEvent.ClearMessages) }) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (isSettingsDialogVisible) {
        SettingsDialog(
            initialBaseUrl = uiState.baseUrl,
            initialSpaceId = uiState.spaceId,
            initialAuthToken = uiState.authToken,
            onDismiss = { isSettingsDialogVisible = false },
            onSaved = { baseUrl: String, spaceId: String, authToken: String ->
                viewModel.onEvent(AttendanceEvent.SaveSettings(baseUrl, spaceId, authToken))
                isSettingsDialogVisible = false
            }
        )
    }
}

private fun formatSixDecimals(value: Double?): String {
    if (value == null) return "N/A"
    val multiplier: Double = 1_000_000.0
    val roundedValue: Double = round(value * multiplier) / multiplier
    return roundedValue.toString()
}


