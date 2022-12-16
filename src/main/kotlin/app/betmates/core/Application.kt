package app.betmates.core

import app.betmates.core.db.initDatabase
import app.betmates.core.ktor.plugins.configureAuthentication
import app.betmates.core.ktor.plugins.configureErrorHandling
import app.betmates.core.ktor.plugins.configureHTTP
import app.betmates.core.ktor.plugins.configureMonitoring
import app.betmates.core.ktor.plugins.configureRouting
import app.betmates.core.ktor.plugins.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    initDatabase()
    io.ktor.server.cio.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    configureMonitoring()
    configureHTTP()
    configureAuthentication()
    configureRouting()
    configureSerialization()
    configureErrorHandling()
}
