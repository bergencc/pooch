package org.bosf.pooch.data.api.models.auth

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)
