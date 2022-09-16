package app.betmates.core.db.service.impl

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.entity.UserRepository
import app.betmates.core.db.service.UserService
import app.betmates.core.domain.Status
import app.betmates.core.domain.User
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserServiceImpl(
    private val database: Database = DatabaseConnection.database
) : UserService {

    override suspend fun save(domain: User): User = coroutineScope {
        newSuspendedTransaction(db = database) {
            val userId = UserRepository.new {
                name = domain.name
                email = domain.email
                username = domain.username
                status = if (domain.isActive()) Status.ACTIVE.name else Status.INACTIVE.name
                password = domain.encryptedPassword
            }.id.value

            domain.apply { id = userId }
        }
    }

    override suspend fun findById(id: Long): User? = coroutineScope {
        newSuspendedTransaction(db = database) {
            UserRepository.findById(id)?.let {
                User(
                    name = it.name,
                    email = it.email,
                    username = it.username!!
                ).also { user ->
                    user.id = id
                    if (it.status == Status.INACTIVE.name) user.deactivate()
                }
            }
        }
    }
}
