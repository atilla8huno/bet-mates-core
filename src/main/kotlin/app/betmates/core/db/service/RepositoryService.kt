package app.betmates.core.db.service

import app.betmates.core.domain.Base
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.sql.ComparisonOp
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryParameter

sealed interface RepositoryService<D : Base, E : LongEntity> {

    suspend fun save(domain: D): D = TODO("Not yet implemented")
    suspend fun update(domain: D): D = TODO("Not yet implemented")
    suspend fun saveOrUpdate(domain: D): D = if (domain.id === null) save(domain) else update(domain)
    suspend fun findById(id: Long): D? = TODO("Not yet implemented")
    suspend fun delete(domain: D): Unit = TODO("Not yet implemented")
    suspend fun findAll(): Flow<D> = TODO("Not yet implemented")
    fun mapToDomain(entity: E): D = TODO("Not yet implemented")
}

class InsensitiveLikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "ILIKE")

infix fun<T : String?> ExpressionWithColumnType<T>.ilike(pattern: String): Op<Boolean> =
    InsensitiveLikeOp(this, QueryParameter(pattern, columnType))
