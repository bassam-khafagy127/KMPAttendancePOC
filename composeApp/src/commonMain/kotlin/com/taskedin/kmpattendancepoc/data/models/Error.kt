package com.taskedin.kmpattendancepoc.data.models

sealed interface Error {

    enum class RemoteError : Error {
        UnKnown,
        ClientError,
        ServerError,
        UnexpectedError,
        NoInternetError,
        RedirectionError,
        SerializationError,
        UnresolvedAddressException
    }

    enum class LocalError : Error {
        UnKnown,
        NotFound,
        Serialization,
        InvalidRequest
    }

    data class RemoteErrorWithCode(val error: String?, val code: Int = 0) : Error

    sealed class HttpError : Error {
        data class BadRequest(val message: String?) : HttpError()          // 400
        data class Unauthorized(val message: String?) : HttpError()        // 401
        data class Forbidden(val message: String?) : HttpError()           // 403
        data class NotFound(val message: String?) : HttpError()            // 404
        data class Conflict(val message: String?) : HttpError()            // 409
        data class TooManyRequests(val message: String?) : HttpError()     // 429
        data class ServerError(val code: Int, val message: String?) : HttpError() // 5xx
        data class Unknown(val code: Int, val message: String?) : HttpError()
    }
}
