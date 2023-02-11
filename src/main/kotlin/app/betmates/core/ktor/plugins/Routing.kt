package app.betmates.core.ktor.plugins

import app.betmates.core.api.playerAPI
import app.betmates.core.api.signInAPI
import app.betmates.core.api.signUpAPI
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        // public APIs
        signUpAPI()
        signInAPI()

        // APIs that require authentication
        authenticate(AUTH_JWT) {
            get("/") {
                call.respondText("Hello World!")
            }

            playerAPI()
        }
    }
}
