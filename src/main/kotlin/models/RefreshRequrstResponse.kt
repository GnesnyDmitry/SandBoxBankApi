package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val email: String,
    val refreshToken: String
)

@Serializable
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
)