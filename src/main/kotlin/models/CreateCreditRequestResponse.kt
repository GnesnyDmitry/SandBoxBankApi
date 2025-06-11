package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateCreditRequest(
    val userId: Long,
    val currentCreditNumber: Long,
    val requestNumber: Long,
    val balance: Long,
    val period: Long
)

@Serializable
data class CreateCreditResponse(
    val product: Product,
    val requestNumber: Long,
    val currentCreditNumber: Long
)