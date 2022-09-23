package app.betmates.core.api.dto

import app.betmates.core.domain.User

data class UserRequest(
    override var id: Long? = null,
    val name: String,
    val email: String,
    val username: String,
    val password: String
) : BaseDTO<User>(id) {

    override fun mapToDomain() = User(
        name = name,
        email = email,
        username = username
    ).apply {
        acceptPassword(password)
    }
}
