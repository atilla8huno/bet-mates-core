package app.betmates.core.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    val username: String,
    val token: String,
    val expiresAt: Long
)
