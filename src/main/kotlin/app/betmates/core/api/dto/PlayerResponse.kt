package app.betmates.core.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlayerResponse(
    val id: Long,
    val nickName: String
)
