package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.example.models.AuthRequest
import org.example.models.AuthUserResponse
import org.example.service.RegisterResult
import org.example.service.UserService

fun Routing.authRoutes(userService: UserService) {
    route("/registration") {
        post {
            val request = call.receive<AuthRequest>()
            when (val result = userService.registerUser(request.email, request.password)) {
                RegisterResult.AlreadyExists -> call.respond(HttpStatusCode.Conflict, "Пользователь уже существует")
                RegisterResult.InvalidEmail -> call.respond(HttpStatusCode.BadRequest, "Некорректный email")
                RegisterResult.PasswordTooShort -> call.respond(HttpStatusCode.BadRequest, "Пароль менее 7 символов")
                is RegisterResult.Success -> {
                    call.respond(
                        HttpStatusCode.Created,
                        AuthUserResponse(result.accessToken, result.refreshToken, result.userId)
                    )
                }
            }

        }
    }
    route("/auth") {
        post {
            val request = call.receive<AuthRequest>()
            when (val result = userService.authenticateUser(request.email, request.password)) {
                is RegisterResult.Success -> {
                    call.respond(HttpStatusCode.OK, AuthUserResponse(result.accessToken, result.refreshToken, result.userId))
                }
                RegisterResult.InvalidEmail -> call.respond(HttpStatusCode.BadRequest, "Некорректный email")
                RegisterResult.PasswordTooShort -> call.respond(HttpStatusCode.BadRequest, "Пароль менее 7 символов")
                else -> call.respond(HttpStatusCode.BadRequest, "Ошибка авторизации")
            }
        }
    }
}