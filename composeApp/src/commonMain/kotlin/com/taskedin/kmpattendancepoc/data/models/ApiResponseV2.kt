package com.taskedin.kmpattendancepoc.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseV2<T>(
    val code: Int?,
    val success: Boolean?,
    val message: String?,
    val data: T?,
    val baseUrl: String?
)