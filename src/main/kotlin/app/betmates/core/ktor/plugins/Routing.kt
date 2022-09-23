package app.betmates.core.ktor.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        // public routes
        get("/") {
            call.respondText("Hello World!")
        }

        // routes that require authentication
        authenticate(AUTH_JWT) {
        }
    }
}
