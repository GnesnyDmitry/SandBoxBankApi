package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.example.models.CreateCreditRequest
import org.example.models.CreateCreditResponse
import org.example.service.CreateCreditResult
import org.example.service.UserService

fun Routing.creditProductRoutes(userService: UserService) {
    post("/credit/create") {
        val authHeader = call.request.headers["Authorization"]
        val token = authHeader?.removePrefix("Bearer ")?.trim()
        if (token == null) {
            call.respond(HttpStatusCode.Forbidden, "Отсутствует или некорректный заголовок Authorization")
            return@post
        }

        val request = call.receive<CreateCreditRequest>()
        when (val result = userService.createCredit(
            userId = request.userId,
            accessToken = token,
            currentCreditNumber = request.currentCreditNumber,
            requestNumber = request.requestNumber,
            balance = request.balance,
            period = request.period
        )) {
            is CreateCreditResult.InCorrectAccessToken ->
                call.respond(HttpStatusCode.Forbidden, "Invalid or expired access token")
            is CreateCreditResult.InCorrectCreditNumber ->
                call.respond(HttpStatusCode.BadRequest, "Максимум 3 кредита")
            is CreateCreditResult.InCorrectBalance ->
                call.respond(HttpStatusCode.BadRequest, "Некорректная сумма кредита")
            is CreateCreditResult.IsCreditExist ->
                call.respond(HttpStatusCode.Conflict, "Кредит с таким номером уже существует")
            is CreateCreditResult.Success ->
                call.respond(HttpStatusCode.Created, CreateCreditResponse(
                    product = result.product,
                    requestNumber = result.requestNumber,
                    currentCreditNumber = result.currentCreditNumber
                )
                )
        }
    }
}
