package com.taskedin.kmpattendancepoc.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.taskedin.kmpattendancepoc.data.models.Error
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
    ): Either<Error, R> {
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
            Error.LocalError.Serialization.left()
        } catch (e: UnresolvedAddressException) {
            e.printStackTrace()
            Error.RemoteError.UnresolvedAddressException.left()
        } catch (e: IOException) {
            e.printStackTrace()
            Error.RemoteError.NoInternetError.left()
        } catch (e: ClientRequestException) {
            e.printStackTrace()
            Error.RemoteErrorWithCode(
                error = e.message,
                code = e.response.status.value
            ).left()
        } catch (e: ServerResponseException) {
            e.printStackTrace()
            Error.RemoteError.ServerError.left()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Error.LocalError.InvalidRequest.left()
        } catch (e: Exception) {
            e.printStackTrace()
            Error.RemoteError.UnexpectedError.left()
        }
    }
}

fun mapHttpError(
    statusCode: Int,
    body: String?
): Error {
    val httpError = when (statusCode) {
        400 -> Error.HttpError.BadRequest(body)
        401 -> Error.HttpError.Unauthorized(body)
        403 -> Error.HttpError.Forbidden(body)
        404 -> Error.HttpError.NotFound(body)
        409 -> Error.HttpError.Conflict(body)
        429 -> Error.HttpError.TooManyRequests(body)
        in 500..599 -> Error.HttpError.ServerError(statusCode, body)
        else -> Error.HttpError.Unknown(statusCode, body)
    }
    return httpError
}

