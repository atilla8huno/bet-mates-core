package com.example

import io.ktor.server.application.*
import com.example.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
