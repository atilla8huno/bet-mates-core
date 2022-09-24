package app.betmates.core.api.dto

import app.betmates.core.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
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
        id = this@SignUpRequest.id
        acceptPassword(password)
    }
}
