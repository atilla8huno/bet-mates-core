package app.betmates.core.api.dto

import app.betmates.core.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val username: String,
    val password: String
) : RequestDTO<User> {
    override fun mapToDomain() = User(
        name = name,
        email = email,
        username = username
    ).apply {
        acceptPassword(password)
    }
}
