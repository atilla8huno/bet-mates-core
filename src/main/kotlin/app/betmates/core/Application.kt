package app.betmates.core

import app.betmates.core.ktor.plugins.configureAuthentication
import app.betmates.core.ktor.plugins.configureHTTP
import app.betmates.core.ktor.plugins.configureMonitoring
import app.betmates.core.ktor.plugins.configureRouting
import app.betmates.core.ktor.plugins.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureMonitoring()
    configureHTTP()
    configureRouting()
    configureSerialization()
    configureAuthentication()
}
