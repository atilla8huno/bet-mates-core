package app.betmates

import app.betmates.plugins.configureHTTP
import app.betmates.plugins.configureMonitoring
import app.betmates.plugins.configureRouting
import app.betmates.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
