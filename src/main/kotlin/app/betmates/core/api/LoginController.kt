package app.betmates.core.api

import app.betmates.core.api.command.Command
import app.betmates.core.api.command.SignUpCommand
import app.betmates.core.api.dto.SignUpRequest
import app.betmates.core.api.dto.SignUpResponse
import app.betmates.core.db.service.UserService
import app.betmates.core.db.service.impl.UserServiceImpl
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.signUp() {
    val userService: UserService = UserServiceImpl()
    val signUpCommand: Command<SignUpRequest, SignUpResponse> = SignUpCommand(userService)

    route("/api/sign-up") {
        post {
            val request = call.receive<SignUpRequest>()

            val response = signUpCommand.execute(request)

            call.respond(response)
        }
    }
}