package app.betmates.core.db.entity

import app.betmates.core.domain.Status
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

class TeamEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TeamEntity>(TeamTable)

    val name by TeamTable.name
    val type by TeamTable.type
    val status by TeamTable.status

    var players by PlayerEntity via PlayerTeamTable
}

object TeamTable : LongIdTable(name = "team") {
    val name = varchar("name", 255)
    val type = varchar("type", 255)
    val status = varchar("status", 255).default(Status.ACTIVE.name)
}
