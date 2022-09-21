package app.betmates.core.db.service.impl

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.service.PlayerService
import app.betmates.core.db.service.TeamService
import org.jetbrains.exposed.sql.Database

class TeamServiceImpl(
    private val playerService: PlayerService,
    private val database: Database = DatabaseConnection.database
) : TeamService
