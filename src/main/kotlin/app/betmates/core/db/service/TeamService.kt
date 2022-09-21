package app.betmates.core.db.service

import app.betmates.core.db.entity.TeamEntity
import app.betmates.core.domain.Team

interface TeamService : RepositoryService<Team, TeamEntity>
