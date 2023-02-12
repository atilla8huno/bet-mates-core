package app.betmates.core

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.entity.PlayerTable
import app.betmates.core.db.entity.PlayerTeamTable
import app.betmates.core.db.entity.TeamTable
import app.betmates.core.db.entity.UserTable
import app.betmates.core.ktor.plugins.configureAuthentication
import app.betmates.core.ktor.plugins.configureErrorHandling
import app.betmates.core.ktor.plugins.configureRouting
import app.betmates.core.ktor.plugins.configureSerialization
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.setMain
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
internal abstract class ITest {

    val exceptions = mutableListOf<Throwable>()

    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        exceptions.add(exception)
    }

    @BeforeTest
    fun setUp() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ktor.environment" to "test"
            )
        }
        application {
            configureAuthentication()
            configureRouting()
            configureSerialization()
            configureErrorHandling()
        }
    }

    @BeforeTest
    fun connect() = transaction {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))
        addLogger(StdOutSqlLogger)

        DatabaseConnection.database
        SchemaUtils.create(UserTable, PlayerTable, TeamTable, PlayerTeamTable)
    }

    @AfterTest
    fun cleanUp() = transaction {
        SchemaUtils.drop(UserTable, PlayerTable, TeamTable, PlayerTeamTable)
        exceptions.clear()
    }
}
