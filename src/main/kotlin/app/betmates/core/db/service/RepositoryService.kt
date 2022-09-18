package app.betmates.core.db.service

import app.betmates.core.domain.Base
import org.jetbrains.exposed.dao.LongEntity

sealed interface RepositoryService<D : Base, E : LongEntity> {

    suspend fun save(domain: D): D
    suspend fun findById(id: Long): D?
    fun mapToDomain(entity: E): D
}
