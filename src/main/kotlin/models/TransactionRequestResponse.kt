package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    val fromId: Long,
    val fromType: String,
    val toId: Long,
    val toType: String,
    val value: Long,
    val transactionNumber: Long,
    val userId: Long
)

@Serializable
data class TransactionResponse(
    val transactionNumber: Long
)
