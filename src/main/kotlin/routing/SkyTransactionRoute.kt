package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.example.models.SkyTopUpRequest
import org.example.models.TransactionResponse
import org.example.service.SkyTopUpResult
import org.example.service.UserService

fun Routing.skyTopUpRoutes(userService: UserService) {
    post("/sky") {
        val authHeader = call.request.headers["Authorization"]
        val token = authHeader?.removePrefix("Bearer ")?.trim()

        if (token == null) {
            call.respond(HttpStatusCode.Forbidden, "Отсутствует или некорректный заголовок Authorization")
            return@post
        }

        val request = try {
            call.receive<SkyTopUpRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Некорректное тело запроса")
            return@post
        }

        when(val result = userService.skyTopUp(
            toId = request.toId,
            toType = request.toType,
            value = request.value,
            transactionNumber = request.transactionNumber,
            accessToken = token
        )) {
            is SkyTopUpResult.InvalidAccessToken ->
                call.respond(HttpStatusCode.Forbidden, "Неверный или просроченный access token")

            is SkyTopUpResult.TransactionNumberTooLow ->
                call.respond(HttpStatusCode.BadRequest, "Некорректный порядковый номер транзакции")

            is SkyTopUpResult.TransactionAlreadyExists ->
                call.respond(HttpStatusCode.Conflict, "Транзакция с таким номером уже выполнена")

            is SkyTopUpResult.Success ->
                call.respond(HttpStatusCode.OK, TransactionResponse(transactionNumber = result.transactionNumber))
        }
    }
}
