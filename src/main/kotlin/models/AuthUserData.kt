package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthUserData(
    val accessToken: String?,
    val refreshToken: String?,
    val userId: Long?
)