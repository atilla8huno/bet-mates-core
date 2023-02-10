package app.betmates.core.api.dto

import app.betmates.core.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email: String,
    val password: String
) : RequestDTO<User> {
    override fun mapToDomain() = User(
        email = email
    ).apply {
        acceptPassword(password)
    }
}
