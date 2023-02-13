package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.PaginatedRequest
import app.betmates.core.api.dto.PaginatedResponse
import app.betmates.core.api.dto.PlayerResponse
import app.betmates.core.db.DatabaseConnection.database
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.impl.PlayerServiceImpl
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class FindAllPlayersCommand(
    private val playerService: PlayerService = PlayerServiceImpl()
) : Command<PaginatedRequest, PaginatedResponse<PlayerResponse>> {

    override suspend fun execute(request: PaginatedRequest): PaginatedResponse<PlayerResponse> = newSuspendedTransaction(db = database) {
        val data = playerService
            .findAllPaginated(request.limit, request.offset)
            .await()
            .map { PlayerResponse(it.id!!, it.nickName) }
            .toList(mutableListOf())

        PaginatedResponse(
            limit = request.limit,
            offset = request.offset,
            total = playerService.count().await(),
            data = data
        )
    }
}
