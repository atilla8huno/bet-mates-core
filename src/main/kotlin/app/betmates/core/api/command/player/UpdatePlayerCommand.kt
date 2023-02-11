package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.PlayerRequest
import app.betmates.core.api.dto.PlayerResponse
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.impl.PlayerServiceImpl
import kotlinx.coroutines.coroutineScope
import java.lang.IllegalStateException

class UpdatePlayerCommand(
    private val playerService: PlayerService = PlayerServiceImpl()
) : Command<PlayerRequest, PlayerResponse> {

    override suspend fun execute(request: PlayerRequest): PlayerResponse = coroutineScope {
        val player = playerService.findById(request.id)
            ?: throw IllegalStateException("Player not found for ID ${request.id}")

        player.apply {
            nickName = request.nickName
        }.also {
            playerService.update(it)
        }.let {
            PlayerResponse(it.id!!, it.nickName)
        }
    }
}
