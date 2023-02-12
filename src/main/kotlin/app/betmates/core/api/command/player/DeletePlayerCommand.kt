package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.impl.PlayerServiceImpl
import kotlinx.coroutines.coroutineScope

class DeletePlayerCommand(
    private val playerService: PlayerService = PlayerServiceImpl()
) : Command<Long, Unit> {

    override suspend fun execute(request: Long): Unit = coroutineScope {
        playerService.deleteById(request)
    }
}
