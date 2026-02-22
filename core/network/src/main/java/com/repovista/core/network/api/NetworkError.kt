package com.repovista.core.network.api

import java.io.IOException
import retrofit2.HttpException

class NetworkDataException(
    override val message: String,
    val causeType: Throwable? = null
) : IOException(message, causeType)

fun Throwable.asNetworkDataException(context: String): NetworkDataException = when (this) {
    is NetworkDataException -> this
    is HttpException -> {
        val reason = when (code()) {
            401 -> "Authentication failed. Check your GitHub token."
            403 -> "Request forbidden or rate limit exceeded."
            404 -> "Requested resource was not found."
            in 500..599 -> "GitHub server error (${code()})."
            else -> "GitHub API error (${code()})."
        }
        NetworkDataException("$context failed: $reason", this)
    }

    is IOException -> NetworkDataException(
        "$context failed: Network connection error. Check your internet connection.",
        this
    )

    else -> NetworkDataException("$context failed: ${message ?: "Unknown error"}", this)
}
