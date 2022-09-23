package app.betmates.core.db.service

import app.betmates.core.db.entity.TeamEntity
import app.betmates.core.domain.Team
import kotlinx.coroutines.flow.Flow

interface TeamService : RepositoryService<Team, TeamEntity> {
    suspend fun findByName(name: String): Flow<Team>
}
