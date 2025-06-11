package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class AllCardsResponse(
    val cards: List<Card>
)
