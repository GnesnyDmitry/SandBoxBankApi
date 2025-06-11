package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateDebitCardRequest(
    val userId: Long,
    val currentCardNumber: Long,
    val requestNumber: Long
)

@Serializable
data class CreateDebitCardResponse(
    val card: Card,
    val requestNumber: Long,
    val currentCardNumber: Long
)