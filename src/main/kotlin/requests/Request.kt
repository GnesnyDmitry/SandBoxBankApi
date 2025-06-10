package org.example.Requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.example.models.AuthUserData


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