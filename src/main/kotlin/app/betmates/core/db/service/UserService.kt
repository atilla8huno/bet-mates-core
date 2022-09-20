package app.betmates.core.db.service

import app.betmates.core.db.entity.UserRepository
import app.betmates.core.domain.User

interface UserService : RepositoryService<User, UserRepository> {
    suspend fun findByEmail(email: String): User?
    suspend fun findByUsernameAndPassword(
        username: String,
        password: String
    ): User?
    suspend fun updatePassword(
        user: User,
        newPassword: String
    ): User?
}
