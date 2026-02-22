package com.repovista.core.network.auth

interface TokenProvider {
    fun getToken(): String?
}
