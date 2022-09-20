package app.betmates.core.db.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UserTable)

    var name by UserTable.name
    var email by UserTable.email
    var username by UserTable.username
    var password by UserTable.password
    var status by UserTable.status
}

object UserTable : LongIdTable(name = "user") {
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val username = varchar("username", 255).nullable()
    val password = varchar("password", 255).nullable()
    val status = varchar("status", 255).nullable()
}
