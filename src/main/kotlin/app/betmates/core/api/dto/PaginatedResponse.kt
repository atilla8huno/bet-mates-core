package app.betmates.core.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<E>(
    val limit: Int,
    val offset: Int,
    val total: Long,
    val data: List<E>
)
