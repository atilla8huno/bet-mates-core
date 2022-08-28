package com.betmates.core

import com.betmates.core.ktor.plugins.configureHTTP
import com.betmates.core.ktor.plugins.configureMonitoring
import com.betmates.core.ktor.plugins.configureRouting
import com.betmates.core.ktor.plugins.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
