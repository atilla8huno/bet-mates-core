package app.betmates.core.db.service.impl

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.entity.PlayerEntity
import app.betmates.core.db.entity.PlayerTable
import app.betmates.core.db.entity.PlayerTable.nickName
import app.betmates.core.db.entity.PlayerTable.user
import app.betmates.core.db.entity.UserEntity
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.UserService
import app.betmates.core.domain.Player
import app.betmates.core.exception.NotFoundException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class PlayerServiceImpl(
    private val userService: UserService = UserServiceImpl(),
    private val database: Database = DatabaseConnection.database
) : PlayerService {

    override suspend fun save(domain: Player): Player = newSuspendedTransaction(db = database) {
        val userEntity = domain.user.id?.let {
            UserEntity[it]
        } ?: userService.save(domain.user).let {
            UserEntity[it.id!!]
        }

        val playerId = PlayerEntity.new {
            nickName = domain.nickName
            user = userEntity
        }.id.value

        domain.apply { id = playerId }
    }

    override suspend fun update(domain: Player): Player = newSuspendedTransaction(db = database) {
        PlayerEntity.findById(domain.id!!)
            ?.apply {
                nickName = domain.nickName
                user = UserEntity.findById(domain.user.id!!)!!
            }?.let {
                mapToDomain(it)
            } ?: throw NotFoundException("Entry not found for ID ${domain.id}")
    }

    override suspend fun findById(id: Long): Deferred<Player?> = suspendedTransactionAsync(db = database) {
        PlayerEntity.findById(id)?.let {
            mapToDomain(it)
        }
    }

    override suspend fun findAll(): Deferred<Flow<Player>> = suspendedTransactionAsync(db = database) {
        PlayerEntity.all().asFlow().map { mapToDomain(it) }
    }

    override suspend fun count(): Deferred<Long> = suspendedTransactionAsync(db = database) {
        PlayerEntity.all().count()
    }

    override suspend fun findAllPaginated(limit: Int, offset: Int): Deferred<Flow<Player>> = suspendedTransactionAsync(db = database) {
        PlayerEntity.all()
            .limit(limit, offset.toLong())
            .asFlow()
            .map { mapToDomain(it) }
    }

    override suspend fun delete(domain: Player): Unit = newSuspendedTransaction(db = database) {
        deleteById(domain.id!!)
    }

    override suspend fun deleteById(id: Long): Unit = newSuspendedTransaction(db = database) {
        PlayerEntity.findById(id)?.delete()
            ?: throw NotFoundException("Entry not found for ID $id")
    }

    override fun mapToDomain(entity: PlayerEntity): Player {
        return Player(
            nickName = entity.nickName,
            user = userService.mapToDomain(entity.user)
        ).also { player ->
            player.id = entity.id.value
        }
    }

    override suspend fun existsByNickName(
        nickName: String
    ): Boolean = newSuspendedTransaction(db = database) {
        PlayerEntity.find {
            (PlayerTable.nickName.lowerCase() eq nickName.lowercase())
        }.count() > 0
    }
}
