package app.betmates.core.db.service

import app.betmates.core.domain.Base
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.dao.LongEntity

sealed interface RepositoryService<D : Base, E : LongEntity> {

    suspend fun save(domain: D): D = TODO("Not yet implemented")
    suspend fun update(domain: D): D = TODO("Not yet implemented")
    suspend fun saveOrUpdate(domain: D): D = if (domain.id === null) save(domain) else update(domain)
    suspend fun findById(id: Long): D? = TODO("Not yet implemented")
    suspend fun delete(domain: D): Unit = TODO("Not yet implemented")
    suspend fun findAll(): Flow<D> = TODO("Not yet implemented")
    fun mapToDomain(entity: E): D = TODO("Not yet implemented")
}
