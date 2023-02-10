package app.betmates.core

import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.createPlayer(
//    createPlayerCommand: Command<PlayerRequest, PlayerResponse> = CreatePlayerCommand()
) {
    route("/api/player") {
        post {
//            val request = call.receive<PlayerRequest>()
//
//            val response = createPlayerCommand.execute(request)
//            call.respond(HttpStatusCode.Created, response)
        }
    }
}
