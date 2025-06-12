package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class SkyTopUpRequest(
    val toId: Long,
    val toType: String,
    val value: Long,
    val transactionNumber: Long
)
