package app.betmates.core.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedRequest(
    val limit: Int,
    val offset: Int
)
