package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long,
    val type: String,
    val percentType: Long,
    val period: Long,
    val percent: Int,
    val balance: Long
)