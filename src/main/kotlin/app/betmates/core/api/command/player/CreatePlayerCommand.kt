package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.PlayerRequest
import app.betmates.core.api.dto.PlayerResponse
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.impl.PlayerServiceImpl
import app.betmates.core.exception.ConflictException
import kotlinx.coroutines.coroutineScope

class CreatePlayerCommand(
    private val playerService: PlayerService = PlayerServiceImpl()
) : Command<PlayerRequest, PlayerResponse> {

    override suspend fun execute(request: PlayerRequest): PlayerResponse = coroutineScope {
        val playerAlreadyExists = playerService.existsByNickName(request.nickName)

        if (playerAlreadyExists) {
            throw ConflictException("NickName already exists")
        }

        val newPLayer = playerService.save(request.mapToDomain())

        PlayerResponse(newPLayer.id!!, newPLayer.nickName)
    }
}
