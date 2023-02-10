package app.betmates.core.db.service

import app.betmates.core.db.entity.PlayerEntity
import app.betmates.core.domain.Player

interface PlayerService : RepositoryService<Player, PlayerEntity> {
    suspend fun existsByNickName(
        nickName: String
    ): Boolean
}
