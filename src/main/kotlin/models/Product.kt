package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    override val id: Long,
    override val type: String,
    val percentType: Long,
    val period: Long,
    val percent: Int,
    override var balance: Long
) : BaseProduct