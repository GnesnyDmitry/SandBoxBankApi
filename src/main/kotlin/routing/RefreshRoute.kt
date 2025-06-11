package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.example.models.RefreshRequest
import org.example.models.RefreshResponse
import org.example.service.RefreshTokenResult
import org.example.service.UserService

fun Routing.refresh(userService: UserService) {
    route("/refresh") {
        post {
            val request = call.receive<RefreshRequest>()
            when (val result = userService.refreshToken(request.email, request.refreshToken)) {
                RefreshTokenResult.NoExist -> {
                    call.respond(HttpStatusCode.Conflict, "Нет пользователя с таким refreshToken или email")
                }
                is RefreshTokenResult.Success -> {
                    call.respond(
                        HttpStatusCode.OK,
                        RefreshResponse(
                            accessToken = result.accessToken,
                            refreshToken = result.refreshToken
                        )
                    )
                }
            }
        }
    }
}