package app.betmates.core.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import kotlin.test.BeforeTest

open class DAOTest {

    lateinit var db: Database

    @BeforeTest
    fun connect() {
        db = Database.connect(
            url = "jdbc:h2:mem:test",
            driver = "org.h2.Driver",
            user = "root"
        )
    }

    fun Transaction.setUp() {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(UserEntity, PlayerEntity)
    }

    fun cleanUp() {
        SchemaUtils.drop(UserEntity, PlayerEntity)
    }
}
