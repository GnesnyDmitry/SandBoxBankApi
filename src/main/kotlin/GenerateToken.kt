package org.example

import java.security.SecureRandom

private val secureRandom = SecureRandom()
private const val TOKEN_LENGTH = 32

fun generateToken(): String {
    val bytes = ByteArray(TOKEN_LENGTH)
    secureRandom.nextBytes(bytes)
    return bytes.joinToString("") { "%02x".format(it) }
}