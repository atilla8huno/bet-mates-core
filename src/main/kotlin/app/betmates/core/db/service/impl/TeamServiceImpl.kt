package app.betmates.core.db.service.impl

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.entity.TeamEntity
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.TeamService
import app.betmates.core.domain.FootballTeam
import app.betmates.core.domain.SnookerTeam
import app.betmates.core.domain.Status
import app.betmates.core.domain.Team
import app.betmates.core.domain.TeamType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class TeamServiceImpl(
    private val playerService: PlayerService,
    private val database: Database = DatabaseConnection.database
) : TeamService {

    override suspend fun save(domain: Team): Team = newSuspendedTransaction(db = database) {
        val teamId = TeamEntity.new {
            name = domain.name
            type = domain.type.name
            status = domain.status.name
        }.id.value

        domain.apply { id = teamId }
    }

    override suspend fun update(domain: Team): Team = newSuspendedTransaction(db = database) {
        TeamEntity.findById(domain.id!!)!!
            .apply {
                name = domain.name
                type = domain.type.name
                status = domain.status.name
            }.let {
                mapToDomain(it)
            }
    }

    override suspend fun findById(id: Long): Team? = newSuspendedTransaction(db = database) {
        TeamEntity.findById(id)?.let {
            mapToDomain(it)
        }
    }

    override suspend fun findAll(): Flow<Team> = newSuspendedTransaction(db = database) {
        TeamEntity.all().asFlow().map { mapToDomain(it) }
    }

    override suspend fun delete(domain: Team): Unit = newSuspendedTransaction(db = database) {
        TeamEntity.findById(domain.id!!)?.delete()
    }

    override fun mapToDomain(entity: TeamEntity): Team {
        return when (entity.type) {
            TeamType.FOOTBALL.name -> FootballTeam(entity.name)
            TeamType.SNOOKER.name -> SnookerTeam(entity.name)
            else -> throw IllegalStateException("TeamType not mapped at ${this::class.simpleName}#mapToDomain")
        }.also { team ->
            team.id = entity.id.value
            if (entity.status == Status.INACTIVE.name) team.deactivate()
        }
    }
}
