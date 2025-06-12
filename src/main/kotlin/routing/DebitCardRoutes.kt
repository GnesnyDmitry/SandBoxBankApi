package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.example.models.CreateDebitCardRequest
import org.example.models.CreateDebitCardResponse
import org.example.service.DebitCardResult
import org.example.service.UserService

fun Routing.debitCardRoutes(userService: UserService) {
    post("/card/debit/create") {
        val authHeader = call.request.headers["Authorization"]
        val token = authHeader?.removePrefix("Bearer ")?.trim()

        if (token == null) {
            call.respond(HttpStatusCode.Forbidden, "Отсутствует или некорректный заголовок Authorization")
            return@post
        }

        val request = call.receive<CreateDebitCardRequest>()

        when (val result = userService.createDebitCard(
            userId = request.userId,
            accessToken = token,
            currentCardNumber = request.currentCardNumber,
            requestNumber = request.requestNumber
        )) {
            is DebitCardResult.InCorrectAccessToken -> call.respond(HttpStatusCode.Forbidden, "Не существует такого accessToken")
            is DebitCardResult.InCorrectCardNumber -> call.respond(HttpStatusCode.BadRequest, "Максимум 5 карт")
            is DebitCardResult.IsCardExist -> call.respond(HttpStatusCode.Conflict, "Карта с таким порядковым номером уже существует")
            is DebitCardResult.Success -> call.respond(
                HttpStatusCode.Created,
                CreateDebitCardResponse(
                    card = result.card,
                    requestNumber = result.requestNumber,
                    currentCardNumber = result.currentCardNumber
                )
            )
        }
    }
}

