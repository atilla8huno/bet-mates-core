package app.betmates.core.api

import app.betmates.core.api.command.Command
import app.betmates.core.api.command.player.CreatePlayerCommand
import app.betmates.core.api.command.player.UpdatePlayerCommand
import app.betmates.core.api.dto.PlayerRequest
import app.betmates.core.api.dto.PlayerResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.playerAPI(
    createPlayerCommand: Command<PlayerRequest, PlayerResponse> = CreatePlayerCommand(),
    updatePlayerCommand: Command<PlayerRequest, PlayerResponse> = UpdatePlayerCommand()
) {
    route("/api/player") {
        post {
            val request = call.receive<PlayerRequest>()

            val response = createPlayerCommand.execute(request)
            call.respond(HttpStatusCode.Created, response)
        }

        put("{id}") {
            val idParam = call.parameters["id"]

            val request = call.receive<PlayerRequest>().apply {
                id = idParam?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }

            val response = updatePlayerCommand.execute(request)
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
