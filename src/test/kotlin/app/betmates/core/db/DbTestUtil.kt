package app.betmates.core.db

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger

open class DbTestUtil {

    fun Transaction.setUp() {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(UserEntity, PlayerEntity)
    }

    fun cleanUp() {
        SchemaUtils.drop(UserEntity, PlayerEntity)
    }
}
