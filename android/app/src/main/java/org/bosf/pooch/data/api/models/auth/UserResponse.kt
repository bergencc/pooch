package org.bosf.pooch.data.api.models.auth

data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: String
)