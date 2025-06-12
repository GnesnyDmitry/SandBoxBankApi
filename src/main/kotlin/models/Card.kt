package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    override val id: Long,
    val cvv: Int,
    val endDate: String,
    val owner: String,
    override val type: String,
    val percent: Double,
    override var balance: Long
) : BaseProduct
