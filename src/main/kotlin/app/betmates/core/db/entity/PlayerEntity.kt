package app.betmates.core.db.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

class PlayerEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PlayerEntity>(PlayerTable)

    var nickName by PlayerTable.nickName
    var user by UserEntity referencedOn PlayerTable.user
}

object PlayerTable : LongIdTable(name = "player") {
    val nickName = varchar("nick_name", 255)
    val user = reference("user_id", UserTable)
}
