package org.bosf.pooch.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.bosf.pooch.data.api.ApiService
import org.bosf.pooch.data.api.models.auth.LoginRequest
import org.bosf.pooch.data.api.models.auth.RegisterRequest
import org.bosf.pooch.data.api.models.auth.UserResponse
import org.bosf.pooch.data.local.TokenStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenStore: TokenStore
) {
    val token: Flow<String?> = tokenStore.token

    val isLoggedIn: Flow<Boolean> = tokenStore.token.map { it != null }

    suspend fun login(email: String, password: String): NetworkResult<UserResponse> {
        val result = safeApiCall { api.login(LoginRequest(email, password)) }

        if (result is NetworkResult.Success) {
            tokenStore.saveToken(result.data.accessToken)

            // Fetch full user info
            return safeApiCall { api.getCurrentUser() }
        }

        @Suppress("UNCHECKED_CAST")
        return result as NetworkResult<UserResponse>
    }

    suspend fun register(name: String, email: String, password: String): NetworkResult<UserResponse> {
        val result = safeApiCall { api.register(RegisterRequest(email, password, name)) }

        if (result is NetworkResult.Success) {
            // Auto-login after register
            return login(email, password)
        }

        return result
    }

    suspend fun getCurrentUser(): NetworkResult<UserResponse> {
        return safeApiCall { api.getCurrentUser() }
    }

    suspend fun logout() {
        tokenStore.clearToken()
        tokenStore.clearSelectedDog()
    }

    suspend fun hasValidToken(): Boolean {
        val token = tokenStore.token.first() ?: return false
        val result = safeApiCall { api.getCurrentUser() }

        return result is NetworkResult.Success
    }
}
