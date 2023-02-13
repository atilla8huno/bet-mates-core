package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.impl.PlayerServiceImpl
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DeletePlayerCommand(
    private val playerService: PlayerService = PlayerServiceImpl()
) : Command<Long, Unit> {

    override suspend fun execute(request: Long): Unit = newSuspendedTransaction(db = DatabaseConnection.database) {
        playerService.deleteById(request)
    }
}
