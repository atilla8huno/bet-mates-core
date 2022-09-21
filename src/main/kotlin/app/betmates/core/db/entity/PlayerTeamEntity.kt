package app.betmates.core.db.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

class PlayerTeamEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PlayerTeamEntity>(PlayerTeamTable)

    val player by PlayerEntity referencedOn PlayerTeamTable.player
    val team by TeamEntity referencedOn PlayerTeamTable.team
}

object PlayerTeamTable : LongIdTable(name = "player_team") {
    val player = reference("player_id", PlayerTable)
    val team = reference("team_id", TeamTable)

    init {
        uniqueIndex(player, team)
    }
}
