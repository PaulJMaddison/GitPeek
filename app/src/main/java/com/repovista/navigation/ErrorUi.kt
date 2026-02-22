package com.repovista.navigation

import java.io.IOException
import retrofit2.HttpException

internal fun Throwable.toUserMessage(defaultMessage: String): String {
    if (this is HttpException) {
        return when (code()) {
            403 -> "GitHub rate limit reached. Add a token or try again later."
            in 500..599 -> "GitHub is having issues right now. Please retry in a moment."
            else -> message() ?: defaultMessage
        }
    }

    if (this is IOException) {
        return "Network error. Check your internet connection and try again."
    }

    return message ?: defaultMessage
}
