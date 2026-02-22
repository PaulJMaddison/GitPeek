package com.repovista.core.network.auth

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    val token: Flow<String?>

    suspend fun setToken(token: String?)
}
