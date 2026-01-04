package com.taskedin.kmpattendancepoc.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.taskedin.kmpattendancepoc.data.models.Failure
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

object SafeApiCall {

    suspend inline fun <reified R> execute(
        crossinline apiCall: suspend () -> HttpResponse
    ): Either<Failure, R> {
        return try {
            val response = apiCall()
            val status = response.status.value

            if (status in 200..299) {
                response.body<R>().right()
            } else {
                mapHttpError(
                    statusCode = status,
                    body = response.bodyAsText()
                ).left()
            }
        } catch (e: SerializationException) {
            e.printStackTrace()
            Failure.LocalFailure.Serialization.left()
        } catch (e: UnresolvedAddressException) {
            e.printStackTrace()
            Failure.RemoteFailure.UnresolvedAddressException.left()
        } catch (e: IOException) {
            e.printStackTrace()
            Failure.RemoteFailure.NoInternetError.left()
        } catch (e: ClientRequestException) {
            e.printStackTrace()
            Failure.RemoteFailureWithCode(
                error = e.message,
                code = e.response.status.value
            ).left()
        } catch (e: ServerResponseException) {
            e.printStackTrace()
            Failure.RemoteFailure.ServerError.left()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Failure.LocalFailure.InvalidRequest.left()
        } catch (e: Exception) {
            e.printStackTrace()
            Failure.RemoteFailure.UnexpectedError.left()
        }
    }
}

fun mapHttpError(
    statusCode: Int,
    body: String?
): Failure {
    val httpFailure = when (statusCode) {
        400 -> Failure.HttpFailure.BadRequest(body)
        401 -> Failure.HttpFailure.Unauthorized(body)
        403 -> Failure.HttpFailure.Forbidden(body)
        404 -> Failure.HttpFailure.NotFound(body)
        409 -> Failure.HttpFailure.Conflict(body)
        429 -> Failure.HttpFailure.TooManyRequests(body)
        in 500..599 -> Failure.HttpFailure.ServerFailure(statusCode, body)
        else -> Failure.HttpFailure.Unknown(statusCode, body)
    }
    return httpFailure
}

