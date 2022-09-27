package app.betmates.core.db.service

import app.betmates.core.db.entity.UserEntity
import app.betmates.core.domain.User

interface UserService : RepositoryService<User, UserEntity> {
    suspend fun findByEmail(email: String): User?
    suspend fun findByEmailAndPassword(
        email: String,
        password: String
    ): User?
    suspend fun updatePassword(
        user: User,
        newPassword: String
    ): User?
}
