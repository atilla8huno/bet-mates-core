package app.betmates.core.api

import app.betmates.core.api.command.Command
import app.betmates.core.api.command.player.CreatePlayerCommand
import app.betmates.core.api.command.player.DeletePlayerCommand
import app.betmates.core.api.command.player.FindAllPlayersCommand
import app.betmates.core.api.command.player.FindPlayerByIdCommand
import app.betmates.core.api.command.player.UpdatePlayerCommand
import app.betmates.core.api.dto.PaginatedRequest
import app.betmates.core.api.dto.PaginatedResponse
import app.betmates.core.api.dto.PlayerRequest
import app.betmates.core.api.dto.PlayerResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.playerAPI(
    createPlayerCommand: Command<PlayerRequest, PlayerResponse> = CreatePlayerCommand(),
    updatePlayerCommand: Command<PlayerRequest, PlayerResponse> = UpdatePlayerCommand(),
    deletePlayerCommand: Command<Long, Unit> = DeletePlayerCommand(),
    findPlayerByIdCommand: Command<Long, PlayerResponse> = FindPlayerByIdCommand(),
    findAllPlayersCommand: Command<PaginatedRequest, PaginatedResponse<PlayerResponse>> = FindAllPlayersCommand()
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
                id = idParam
                    ?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }

            val response = updatePlayerCommand.execute(request)
            call.respond(HttpStatusCode.OK, response)
        }

        delete("{id}") {
            val request = call.parameters["id"]
                ?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val response = deletePlayerCommand.execute(request)
            call.respond(HttpStatusCode.NoContent, response)
        }

        get {
            val limit = call.request.queryParameters["limit"]?.toInt() ?: 10
            val offset = call.request.queryParameters["offset"]?.toInt() ?: 0

            val request = PaginatedRequest(limit, offset)
            val response = findAllPlayersCommand.execute(request)

            call.respond(HttpStatusCode.OK, response)
        }

        get("{id}") {
            val request = call.parameters["id"]
                ?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val response = findPlayerByIdCommand.execute(request)
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
