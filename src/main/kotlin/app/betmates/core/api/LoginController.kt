package app.betmates.core.api

import app.betmates.core.api.command.UserCommand
import app.betmates.core.api.command.impl.UserCommandImpl
import app.betmates.core.api.dto.UserRequest
import app.betmates.core.db.service.impl.UserServiceImpl
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.signUp() {
    val userCommand: UserCommand = UserCommandImpl(UserServiceImpl())

    route("/api/sign-up") {
        post {
            val request = call.receive<UserRequest>()

            val response = userCommand.signUp(request)

            call.respond(response)
        }
    }
}
