package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
    val products: List<Product>
)