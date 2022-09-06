package app.betmates.core.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object PlayerEntity : LongIdTable(name = "player") {
    val nickName = varchar("nick_name", 255)
    val user = reference("user_id", UserEntity)
}

class PlayerDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PlayerDAO>(PlayerEntity)

    var nickName by PlayerEntity.nickName
    var user by UserDAO referencedOn PlayerEntity.user
}
