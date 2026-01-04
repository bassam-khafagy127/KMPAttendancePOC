package com.taskedin.kmpattendancepoc.data

import arrow.core.Either
import com.taskedin.kmpattendancepoc.data.models.Failure
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class AttendanceRepositoryImpl(
    val client: HttpClient,
    val safeApiCall: SafeApiCall
) {
    suspend inline fun <reified R : Any> getFunction(
        route: String,
        parameters: Map<String, Any> = emptyMap(),
        headers: Map<String, Any> = emptyMap()
    ): Either<Failure, R> {
        return safeApiCall.execute {
            client.get {
                url("$baseUrl/$route")
                parameters.forEach {
                    parameter(it.key, it.value)
                }
                headers.forEach {
                    header(it.key, it.value)
                }
            }
        }
    }
    suspend inline fun <reified R : Any> postFunction(
        route: String,
        parameters: Map<String, Any> = emptyMap(),
        headers: Map<String, Any> = emptyMap(),
        body: Map<String, Any> = emptyMap(),
    ): Either<Failure, R> {
        return safeApiCall.execute {
            client.post {
                url("$baseUrl/$route")
                parameters.forEach {
                    parameter(it.key, it.value)
                }
                headers.forEach {
                    header(it.key, it.value)
                }
                setBody(body)
            }
        }
    }

    val baseUrl: String = ""
}