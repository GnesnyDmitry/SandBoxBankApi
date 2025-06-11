package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthUserResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long
)

@Serializable
data class AuthRequest(
    val email: String,
    val password: String
)