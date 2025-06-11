package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateDepositRequest(
    val userId: Long,
    val currentDepositNumber: Long,
    val requestNumber: Long,
    val percentType: Long,
    val period: Long
)

@Serializable
data class CreateDepositResponse(
    val product: Product,
    val requestNumber: Long,
    val currentDepositNumber: Long
)