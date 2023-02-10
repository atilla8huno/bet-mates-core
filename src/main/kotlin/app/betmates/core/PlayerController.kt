package app.betmates.core

import app.betmates.core.api.command.Command
import app.betmates.core.api.command.player.CreatePlayerCommand
import app.betmates.core.api.dto.PlayerRequest
import app.betmates.core.api.dto.PlayerResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.createPlayer(
    createPlayerCommand: Command<PlayerRequest, PlayerResponse> = CreatePlayerCommand()
) {
    route("/api/player") {
        post {
            val request = call.receive<PlayerRequest>()

            val response = createPlayerCommand.execute(request)
            call.respond(HttpStatusCode.Created, response)
        }
    }
}
