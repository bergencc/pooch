package org.bosf.pooch.data.api.models.auth

data class LoginRequest(
    val email: String,
    val password: String
)