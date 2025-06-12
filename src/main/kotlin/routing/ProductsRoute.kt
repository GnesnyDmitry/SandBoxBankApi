package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.example.models.ProductsResponse
import org.example.service.GetProductsResult
import org.example.service.UserService

fun Routing.productsRoutes(userService: UserService) {
    get("/products") {
        val authHeader = call.request.headers["Authorization"]
        val token = authHeader?.removePrefix("Bearer ")?.trim()

        if (token == null) {
            call.respond(HttpStatusCode.Forbidden, "Отсутствует или некорректный заголовок Authorization")
            return@get
        }

        val userId = call.request.queryParameters["user_id"]?.toLongOrNull()
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid user_id")
            return@get
        }

        when (val result = userService.getAllProducts(userId, token)) {
            is GetProductsResult.InCorrectAccessToken -> call.respond(HttpStatusCode.Forbidden, "Не существует такого accessToken")
            is GetProductsResult.Success -> call.respond(
                HttpStatusCode.OK,
                ProductsResponse(products = result.products)
            )
        }
    }
}
