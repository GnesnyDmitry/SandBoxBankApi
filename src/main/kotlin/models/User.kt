package org.example.models

data class User(
    val email: String,
    val password: String,
    var accessToken: String,
    val refreshToken: String,
    val userId: Long,
)
