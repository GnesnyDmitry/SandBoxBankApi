package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.example.models.AllCardsResponse
import org.example.service.GetAllCardsResult
import org.example.service.UserService

fun Routing.allCardsRoute(userService: UserService) {
    get("/cards") {
        val authHeader = call.request.headers["Authorization"]
        val token = authHeader?.removePrefix("Bearer ")?.trim()

        if (token == null) {
            call.respond(HttpStatusCode.Forbidden, "Отсутствует или некорректный заголовок Authorization")
            return@get
        }

        val userId = call.request.queryParameters["user_id"]?.toLongOrNull()
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Нету такого userId")
            return@get
        }

        val result = userService.getAllCards(userId, token)
        when (result) {
            is GetAllCardsResult.InCorrectAccessToken -> call.respond(HttpStatusCode.Forbidden, "Не существует такого accessToken")
            is GetAllCardsResult.Success -> call.respond(HttpStatusCode.OK, AllCardsResponse(cards = result.cards))
        }
    }
}
