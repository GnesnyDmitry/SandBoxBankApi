package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.example.models.CreateCreditCardRequest
import org.example.models.CreateCreditCardResponse
import org.example.service.CreditCardResult
import org.example.service.UserService

fun Routing.creditCardRoutes(userService: UserService) {
    post("/card/credit/create") {
        val authHeader = call.request.headers["Authorization"]
        val token = authHeader?.removePrefix("Bearer ")?.trim()

        if (token == null) {
            call.respond(HttpStatusCode.Forbidden, "Отсутствует или некорректный заголовок Authorization")
            return@post
        }

        val request = call.receive<CreateCreditCardRequest>()

        when (val result = userService.createCreditCard(
            userId = request.userId,
            accessToken = token,
            currentCardNumber = request.currentCardNumber,
            requestNumber = request.requestNumber
        )) {
            is CreditCardResult.InCorrectAccessToken -> call.respond(HttpStatusCode.Forbidden, "Не существует такого accessToken")
            is CreditCardResult.InCorrectCardNumber -> call.respond(HttpStatusCode.BadRequest, "Максимум 3 кредитные карты")
            is CreditCardResult.IsCardExist -> call.respond(HttpStatusCode.Conflict, "Карта с таким порядковым номером уже существует")
            is CreditCardResult.Success -> call.respond(
                HttpStatusCode.Created,
                CreateCreditCardResponse(
                    card = result.card,
                    requestNumber = result.requestNumber,
                    currentCardNumber = result.currentCardNumber
                )
            )
        }
    }
}
