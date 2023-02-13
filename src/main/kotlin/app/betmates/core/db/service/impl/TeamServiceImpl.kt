package app.betmates.core.db.service.impl

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.entity.PlayerEntity
import app.betmates.core.db.entity.TeamEntity
import app.betmates.core.db.entity.TeamTable
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.TeamService
import app.betmates.core.db.service.ilike
import app.betmates.core.domain.FootballTeam
import app.betmates.core.domain.SnookerTeam
import app.betmates.core.domain.Status
import app.betmates.core.domain.Team
import app.betmates.core.domain.TeamType
import app.betmates.core.exception.NotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class TeamServiceImpl(
    private val playerService: PlayerService,
    private val database: Database = DatabaseConnection.database
) : TeamService {

    override suspend fun save(domain: Team): Team = newSuspendedTransaction(db = database) {
        val playersList = domain.players().map {
            if (it.id == null) playerService.save(it) else it
        }

        val teamId = TeamEntity.new {
            name = domain.name
            type = domain.type.name
            status = domain.status.name

            players = SizedCollection(
                playersList.map {
                    PlayerEntity[it.id!!]
                }
            )
        }.id.value

        domain.apply { id = teamId }
    }

    override suspend fun update(domain: Team): Team = newSuspendedTransaction(db = database) {
        val playersList = domain.players().map {
            if (it.id == null) playerService.save(it) else it
        }

        TeamEntity.findById(domain.id!!)
            ?.apply {
                name = domain.name
                type = domain.type.name
                status = domain.status.name

                players = SizedCollection(
                    playersList.map {
                        PlayerEntity[it.id!!]
                    }
                )
            }?.let {
                mapToDomain(it)
            } ?: throw NotFoundException("Entry not found for ID ${domain.id}")
    }

    override suspend fun findById(id: Long): Team? = newSuspendedTransaction(db = database) {
        TeamEntity.findById(id)?.let {
            mapToDomain(it)
        }
    }

    override suspend fun findAll(): Flow<Team> = newSuspendedTransaction(db = database) {
        TeamEntity.all().asFlow().map { mapToDomain(it) }
    }

    override suspend fun count(): Long = newSuspendedTransaction(db = database) {
        TeamEntity.all().count()
    }

    override suspend fun findAllPaginated(limit: Int, offset: Int): Flow<Team> = newSuspendedTransaction(db = database) {
        TeamEntity.all()
            .limit(limit, offset.toLong())
            .asFlow()
            .map { mapToDomain(it) }
    }

    override suspend fun findByName(name: String): Flow<Team> = newSuspendedTransaction(db = database) {
        TeamEntity.find {
            TeamTable.name ilike "%$name%"
        }.asFlow().map { mapToDomain(it) }
    }

    override suspend fun delete(domain: Team): Unit = newSuspendedTransaction(db = database) {
        deleteById(domain.id!!)
    }

    override suspend fun deleteById(id: Long): Unit = newSuspendedTransaction(db = database) {
        TeamEntity.findById(id)?.delete()
            ?: throw NotFoundException("Entry not found for ID $id")
    }

    override fun mapToDomain(entity: TeamEntity): Team {
        return when (entity.type) {
            TeamType.FOOTBALL.name -> FootballTeam(name = entity.name)
            TeamType.SNOOKER.name -> SnookerTeam(name = entity.name)
            else -> throw IllegalStateException("TeamType not mapped at ${this::class.simpleName}#mapToDomain")
        }.also { team ->
            team.id = entity.id.value
            entity.players.forEach {
                team.addPlayer(playerService.mapToDomain(it))
            }
            if (entity.status == Status.INACTIVE.name) team.deactivate()
        }
    }
}
