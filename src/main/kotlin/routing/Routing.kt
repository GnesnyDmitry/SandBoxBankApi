package org.example.routing

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import org.example.service.UserService

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    val userService = UserService()
    routing {
        authRoutes(userService)

        refresh(userService)

        debitCardRoutes(userService)

        creditCardRoutes(userService)

        allCardsRoute(userService)

        depositRoutes(userService)

        creditProductRoutes(userService)

        productsRoutes(userService)
    }
}