package app.betmates.core.api

import app.betmates.core.api.command.Command
import app.betmates.core.api.command.login.SignInCommand
import app.betmates.core.api.command.login.SignUpCommand
import app.betmates.core.api.dto.SignInRequest
import app.betmates.core.api.dto.SignInResponse
import app.betmates.core.api.dto.SignUpRequest
import app.betmates.core.api.dto.SignUpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.signUp(
    signUpCommand: Command<SignUpRequest, SignUpResponse> = SignUpCommand()
) {
    route("/api/sign-up") {
        post {
            val request = call.receive<SignUpRequest>()

            val response = signUpCommand.execute(request)
            call.respond(HttpStatusCode.Created, response)
        }
    }
}

fun Route.signIn(
    signInCommand: Command<SignInRequest, SignInResponse> = SignInCommand()
) {
    route("/api/sign-in") {
        post {
            val request = call.receive<SignInRequest>()

            val response = signInCommand.execute(request)
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
