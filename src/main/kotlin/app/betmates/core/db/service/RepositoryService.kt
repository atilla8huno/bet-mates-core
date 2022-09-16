package app.betmates.core.db.service

import app.betmates.core.domain.Base

sealed interface RepositoryService<T : Base> {

    suspend fun save(domain: T): T
    suspend fun findById(id: Long): T?
}
