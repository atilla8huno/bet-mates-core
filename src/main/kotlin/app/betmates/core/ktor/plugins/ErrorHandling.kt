package app.betmates.core.ktor.plugins

import app.betmates.core.exception.AuthenticationFailed
import app.betmates.core.exception.ConflictException
import app.betmates.core.exception.NotFoundException
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.respond

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is ConflictException -> call.respond(
                    status = Conflict,
                    message = "${cause.message}"
                )

                is NotFoundException -> call.respond(
                    status = NotFound,
                    message = "${cause.message}"
                )

                is AuthenticationFailed -> call.respond(
                    status = Unauthorized,
                    message = "${cause.message}"
                )

                else -> call.respond(
                    status = InternalServerError,
                    message = "${InternalServerError.value}: ${cause.message ?: cause}"
                )
            }
        }
    }
}
