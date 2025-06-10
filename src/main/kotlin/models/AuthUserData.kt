package org.example.models

data class AuthUserData(
    val accessToken: String?,
    val refreshToken: String?,
    val userId: Long?
)