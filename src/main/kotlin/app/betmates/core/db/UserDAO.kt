package app.betmates.core.db

import app.betmates.core.db.UserEntity.nullable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserEntity : LongIdTable(name = "user") {
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val username = varchar("username", 255).nullable()
    val password = varchar("password", 255).nullable()
    val status = varchar("status", 255).nullable()
}

class UserDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserDAO>(UserEntity)

    var name by UserEntity.name
    var email by UserEntity.email
    var username by UserEntity.username
    var password by UserEntity.password
    var status by UserEntity.status
}
