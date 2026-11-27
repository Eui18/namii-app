package com.namii.app.features.auth.data.datasources.local

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.namii.app.core.network.TokenProvider
import com.namii.app.features.auth.domain.entities.AuthSession
import com.namii.app.features.auth.domain.entities.AuthUser
import com.namii.app.features.auth.domain.entities.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenPreferencesDataSource @Inject constructor(
    @ApplicationContext context: Context,
    private val gson: Gson
) : TokenProvider {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _sessionFlow = MutableStateFlow(readStoredSession())
    val sessionFlow: StateFlow<AuthSession?> = _sessionFlow.asStateFlow()

    override fun getToken(): String? = _sessionFlow.value?.token ?: prefs.getString(KEY_TOKEN, null)

    fun saveSession(session: AuthSession) {
        prefs.edit {
            putString(KEY_TOKEN, session.token)
            putInt(KEY_USER_ID, session.user.id)
            putString(KEY_USER_NAME, session.user.name)
            putString(KEY_USER_EMAIL, session.user.email)
            putString(KEY_USER_ROLE, session.user.role.name)
        }
        _sessionFlow.value = session
    }

    fun clearSession() {
        prefs.edit {
            remove(KEY_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_ROLE)
        }
        _sessionFlow.value = null
    }

    private fun readStoredSession(): AuthSession? {
        val token = prefs.getString(KEY_TOKEN, null) ?: return null
        val id = prefs.getInt(KEY_USER_ID, -1)
        val name = prefs.getString(KEY_USER_NAME, null) ?: return null
        val email = prefs.getString(KEY_USER_EMAIL, null) ?: return null
        val role = UserRole.fromRaw(prefs.getString(KEY_USER_ROLE, null))
        if (id <= 0 || token.isBlank()) return null
        return AuthSession(
            token = token,
            user = AuthUser(id = id, name = name, email = email, role = role)
        )
    }

    private companion object {
        const val PREFS_NAME = "namii_auth_prefs"
        const val KEY_TOKEN = "auth_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_ROLE = "user_role"
    }
}
