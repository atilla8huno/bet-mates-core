package app.betmates.core.db.service.impl

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.entity.UserEntity
import app.betmates.core.db.entity.UserTable
import app.betmates.core.db.service.UserService
import app.betmates.core.domain.Status
import app.betmates.core.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserServiceImpl(
    private val database: Database = DatabaseConnection.database
) : UserService {

    override suspend fun save(domain: User): User = newSuspendedTransaction(db = database) {
        val userId = UserEntity.new {
            name = domain.name
            email = domain.email
            username = domain.username
            status = if (domain.isActive()) Status.ACTIVE.name else Status.INACTIVE.name
            password = domain.encryptedPassword
        }.id.value

        domain.apply { id = userId }
    }

    override suspend fun update(domain: User): User = newSuspendedTransaction(db = database) {
        UserEntity.findById(domain.id!!)!!
            .apply {
                name = domain.name
                email = domain.email
                username = domain.username
                status = if (domain.isActive()) Status.ACTIVE.name else Status.INACTIVE.name
            }.let {
                mapToDomain(it)
            }
    }

    override suspend fun delete(domain: User): Unit = newSuspendedTransaction(db = database) {
        UserEntity.findById(domain.id!!)?.delete()
    }

    override suspend fun findById(id: Long): User? = newSuspendedTransaction(db = database) {
        UserEntity.findById(id)?.let {
            mapToDomain(it)
        }
    }

    override suspend fun findAll(): Flow<User> = newSuspendedTransaction(db = database) {
        UserEntity.all().asFlow().map { mapToDomain(it) }
    }

    override suspend fun findByEmail(email: String): User? = newSuspendedTransaction(db = database) {
        UserEntity.find {
            UserTable.email eq email
        }.firstOrNull()?.let {
            mapToDomain(it)
        }
    }

    override suspend fun findByUsernameAndPassword(
        username: String,
        password: String
    ): User? = newSuspendedTransaction(db = database) {
        UserEntity.find {
            (UserTable.username eq username) and (UserTable.password eq User.encrypt(password))
        }.firstOrNull()?.let {
            mapToDomain(it)
        }
    }

    override suspend fun updatePassword(
        user: User,
        newPassword: String
    ): User? = newSuspendedTransaction(db = database) {
        UserEntity.find {
            (UserTable.id eq user.id!!) and (UserTable.password eq user.encryptedPassword!!)
        }.firstOrNull()?.let {
            user.acceptPassword(newPassword)
            it.password = user.encryptedPassword

            user
        }
    }

    override fun mapToDomain(entity: UserEntity): User {
        return User(
            name = entity.name,
            email = entity.email,
            username = entity.username
        ).also { user ->
            user.id = entity.id.value
            if (entity.status == Status.INACTIVE.name) user.deactivate()
        }
    }
}