package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: Long,
    val cvv: Int,
    val endDate: String,
    val owner: String,
    val type: String,
    val percent: Double,
    val balance: Int
)
