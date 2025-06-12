package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.example.models.TransactionRequest
import org.example.models.TransactionResponse
import org.example.service.TransactionResult
import org.example.service.UserService

fun Routing.transactionRoute(userService: UserService) {
    post("/transaction") {
        val authHeader = call.request.headers["Authorization"]
        val token = authHeader?.removePrefix("Bearer ")?.trim()

        if (token == null) {
            call.respond(HttpStatusCode.Forbidden, "Отсутствует или некорректный заголовок Authorization")
            return@post
        }

        val request = call.receive<TransactionRequest>()

        val result = userService.makeTransaction(
            userId = request.userId,
            accessToken = token,
            request = request
        )

        when (result) {
            is TransactionResult.Success -> call.respond(HttpStatusCode.OK, TransactionResponse(transactionNumber = result.transactionNumber))
            is TransactionResult.InvalidToken -> call.respond(HttpStatusCode.Forbidden, "Неверный access token")
            is TransactionResult.InsufficientFunds -> call.respond(HttpStatusCode.BadRequest, "Недостаточно средств на счёте")
            is TransactionResult.InvalidTransactionNumber -> call.respond(HttpStatusCode.BadRequest, "Некорректный порядковый номер транзакции")
            is TransactionResult.AlreadyProcessed -> call.respond(HttpStatusCode.Conflict, "Транзакция с таким номером уже выполнена")
            is TransactionResult.ProductNotFound -> call.respond(HttpStatusCode.BadRequest, "Продукт не найден: проверьте ID и тип")
        }
    }
}
