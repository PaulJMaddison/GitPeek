package com.repovista.core.network.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_settings")

@Singleton
class DataStoreTokenRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenRepository, TokenProvider {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val latestToken = MutableStateFlow<String?>(null)

    override val token: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_GITHUB_TOKEN]
        }

    init {
        scope.launch {
            token.collect { storedToken ->
                latestToken.value = storedToken
            }
        }
    }

    override suspend fun setToken(token: String?) {
        val normalizedToken = token?.trim().orEmpty().ifBlank { null }
        context.dataStore.edit { preferences ->
            if (normalizedToken == null) {
                preferences.remove(KEY_GITHUB_TOKEN)
            } else {
                preferences[KEY_GITHUB_TOKEN] = normalizedToken
            }
        }
    }

    override fun getToken(): String? = latestToken.value

    private companion object {
        val KEY_GITHUB_TOKEN = stringPreferencesKey("github_token")
    }
}
