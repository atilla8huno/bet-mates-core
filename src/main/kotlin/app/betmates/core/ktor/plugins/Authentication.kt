package app.betmates.core.ktor.plugins

import app.betmates.core.util.JwtProperties
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

const val AUTH_JWT = "auth-jwt"

fun Application.configureAuthentication() {
    val secret = JwtProperties.secret
    val audience = JwtProperties.audience
    val issuer = JwtProperties.issuer
    val jwtRealm = JwtProperties.realm

    install(Authentication) {
        jwt(AUTH_JWT) {
            realm = jwtRealm!!

            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret!!))
                    .withAudience(audience!!)
                    .withIssuer(issuer!!)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            // authorization block - can be used to check when I certain user can access certain resources
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Authentication token is not valid or has expired")
            }
        }
    }
}
