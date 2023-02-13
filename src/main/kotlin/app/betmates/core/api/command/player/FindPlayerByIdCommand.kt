package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.PlayerResponse
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.impl.PlayerServiceImpl
import app.betmates.core.exception.NotFoundException
import kotlinx.coroutines.coroutineScope

class FindPlayerByIdCommand(
    private val playerService: PlayerService = PlayerServiceImpl()
) : Command<Long, PlayerResponse> {

    override suspend fun execute(request: Long): PlayerResponse = coroutineScope {
        playerService.findById(request)
            .await()
            ?.let {
                PlayerResponse(
                    id = it.id!!,
                    nickName = it.nickName
                )
            } ?: throw NotFoundException("Entry not found for id $request")
    }
}
