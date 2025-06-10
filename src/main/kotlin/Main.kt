package org.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.module() {
    routing {
        get("/hello") {
            call.respondText("Hello, Ktor!")
        }
        createGame()
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

private fun Routing.createGame() {
    get("/info") {
        val response = AuthUserData(
            accessToken = "111",
            refreshToken = "222",
            userId = 1L
        )
        call.respond(HttpStatusCode.OK, response)
    }
}