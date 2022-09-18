package app.betmates.core.db.service.impl

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.entity.PlayerRepository
import app.betmates.core.db.entity.UserRepository
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.UserService
import app.betmates.core.domain.Player
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class PlayerServiceImpl(
    private val userService: UserService,
    private val database: Database = DatabaseConnection.database
) : PlayerService {

    override suspend fun save(domain: Player): Player = coroutineScope {
        newSuspendedTransaction(db = database) {
            val playerId = PlayerRepository.new {
                nickName = domain.nickName
                user = UserRepository.findById(domain.user.id!!)!!
            }.id.value

            domain.apply { id = playerId }
        }
    }

    override suspend fun findById(id: Long): Player? = coroutineScope {
        newSuspendedTransaction(db = database) {
            PlayerRepository.findById(id)?.let {
                mapToDomain(it)
            }
        }
    }

    override fun mapToDomain(entity: PlayerRepository): Player {
        return Player(
            nickName = entity.nickName,
            user = userService.mapToDomain(entity.user)
        ).also { player ->
            player.id = entity.id.value
        }
    }
}
