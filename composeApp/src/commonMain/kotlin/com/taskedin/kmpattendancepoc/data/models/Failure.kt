package com.taskedin.kmpattendancepoc.data.models

sealed interface Failure {

    enum class RemoteFailure : Failure {
        UnKnown,
        ClientError,
        ServerError,
        UnexpectedError,
        NoInternetError,
        RedirectionError,
        SerializationError,
        UnresolvedAddressException
    }

    enum class LocalFailure : Failure {
        UnKnown,
        NotFound,
        Serialization,
        InvalidRequest
    }

    data class RemoteFailureWithCode(val error: String?, val code: Int = 0) : Failure

    sealed class HttpFailure : Failure {
        data class BadRequest(val message: String?) : HttpFailure()          // 400
        data class Unauthorized(val message: String?) : HttpFailure()        // 401
        data class Forbidden(val message: String?) : HttpFailure()           // 403
        data class NotFound(val message: String?) : HttpFailure()            // 404
        data class Conflict(val message: String?) : HttpFailure()            // 409
        data class TooManyRequests(val message: String?) : HttpFailure()     // 429
        data class ServerFailure(val code: Int, val message: String?) : HttpFailure() // 5xx
        data class Unknown(val code: Int, val message: String?) : HttpFailure()
    }
}
