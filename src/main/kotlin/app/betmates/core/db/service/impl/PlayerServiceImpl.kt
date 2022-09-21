package app.betmates.core.db.service.impl

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.entity.PlayerEntity
import app.betmates.core.db.entity.UserEntity
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.UserService
import app.betmates.core.domain.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class PlayerServiceImpl(
    private val userService: UserService,
    private val database: Database = DatabaseConnection.database
) : PlayerService {

    override suspend fun save(domain: Player): Player = newSuspendedTransaction(db = database) {
        val playerId = PlayerEntity.new {
            nickName = domain.nickName
            user = UserEntity.findById(domain.user.id!!)!!
        }.id.value

        domain.apply { id = playerId }
    }

    override suspend fun update(domain: Player): Player = newSuspendedTransaction(db = database) {
        PlayerEntity.findById(domain.id!!)!!
            .apply {
                nickName = domain.nickName
                user = UserEntity.findById(domain.user.id!!)!!
            }.let {
                mapToDomain(it)
            }
    }

    override suspend fun findById(id: Long): Player? = newSuspendedTransaction(db = database) {
        PlayerEntity.findById(id)?.let {
            mapToDomain(it)
        }
    }

    override suspend fun findAll(): Flow<Player> = newSuspendedTransaction(db = database) {
        PlayerEntity.all().asFlow().map { mapToDomain(it) }
    }

    override suspend fun delete(domain: Player): Unit = newSuspendedTransaction(db = database) {
        PlayerEntity.findById(domain.id!!)?.delete()
    }

    override fun mapToDomain(entity: PlayerEntity): Player {
        return Player(
            nickName = entity.nickName,
            user = userService.mapToDomain(entity.user)
        ).also { player ->
            player.id = entity.id.value
        }
    }
}