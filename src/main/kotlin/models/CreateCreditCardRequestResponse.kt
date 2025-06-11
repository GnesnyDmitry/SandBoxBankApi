package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateCreditCardRequest(
    val userId: Long,
    val currentCardNumber: Long,
    val requestNumber: Long
)

@Serializable
data class CreateCreditCardResponse(
    val card: Card,
    val requestNumber: Long,
    val currentCardNumber: Long
)
