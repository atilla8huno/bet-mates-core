package app.betmates.core.db.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

class PlayerRepository(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PlayerRepository>(PlayerEntity)

    var nickName by PlayerEntity.nickName
    var user by UserRepository referencedOn PlayerEntity.user
}

object PlayerEntity : LongIdTable(name = "player") {
    val nickName = varchar("nick_name", 255)
    val user = reference("user_id", UserEntity)
}
