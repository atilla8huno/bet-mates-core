package app.betmates.core.api.dto

@kotlinx.serialization.Serializable
data class SignUpResponse(
    val id: Long,
    val username: String
)
