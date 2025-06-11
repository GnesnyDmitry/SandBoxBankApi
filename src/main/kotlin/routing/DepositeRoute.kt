package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.example.models.CreateDepositRequest
import org.example.models.CreateDepositResponse
import org.example.service.DepositResult
import org.example.service.UserService

fun Routing.depositRoutes(userService: UserService) {
    post("/deposit/create") {
        val authHeader = call.request.headers["Authorization"]
        val token = authHeader?.removePrefix("Bearer ")?.trim()

        if (token == null) {
            call.respond(HttpStatusCode.Forbidden, "Missing or invalid Authorization header")
            return@post
        }

        val request = call.receive<CreateDepositRequest>()

        when (val result = userService.createDeposit(
            userId = request.userId,
            accessToken = token,
            currentDepositNumber = request.currentDepositNumber,
            requestNumber = request.requestNumber,
            percentType = request.percentType,
            period = request.period
        )) {
            is DepositResult.InCorrectAccessToken ->
                call.respond(HttpStatusCode.Forbidden, "Не существует такого accessToken")

            is DepositResult.InCorrectDepositNumber ->
                call.respond(HttpStatusCode.BadRequest, "Максимум 5 вкладов")

            is DepositResult.IsDepositExist ->
                call.respond(HttpStatusCode.Conflict, "Вклад с таким порядковым номером уже существует")

            is DepositResult.Success ->
                call.respond(HttpStatusCode.Created, CreateDepositResponse(
                    product = result.product,
                    requestNumber = result.requestNumber,
                    currentDepositNumber = result.currentDepositNumber
                )
                )
        }
    }
}
