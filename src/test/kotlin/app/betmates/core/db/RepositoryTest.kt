package app.betmates.core.db

import app.betmates.core.db.entity.PlayerEntity
import app.betmates.core.db.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.setMain
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
open class RepositoryTest {

    lateinit var db: Database

    @BeforeTest
    fun connect() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

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
