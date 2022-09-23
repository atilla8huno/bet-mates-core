package app.betmates.core.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val id: Long,
    val username: String
)
