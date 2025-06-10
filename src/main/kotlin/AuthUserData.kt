package org.example

data class AuthUserData(
    val accessToken: String?,
    val refreshToken: String?,
    val userId: Long?
)
