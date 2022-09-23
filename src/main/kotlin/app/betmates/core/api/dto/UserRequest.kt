package app.betmates.core.api.dto

import app.betmates.core.domain.User

@kotlinx.serialization.Serializable
data class UserRequest(
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val id: Long? = null
) {

    fun mapToDomain() = User(
        name = name,
        email = email,
        username = username
    ).apply {
        id = this@UserRequest.id
        acceptPassword(password)
    }
}
