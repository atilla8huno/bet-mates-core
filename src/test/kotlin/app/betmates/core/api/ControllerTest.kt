package app.betmates.core.api

import app.betmates.core.db.DatabaseConnection
import app.betmates.core.db.entity.PlayerTable
import app.betmates.core.db.entity.PlayerTeamTable
import app.betmates.core.db.entity.TeamTable
import app.betmates.core.db.entity.UserTable
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

@ExperimentalCoroutinesApi
internal abstract class ControllerTest {

    @BeforeTest
    fun connect() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

        DatabaseConnection.database
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(UserTable, PlayerTable, TeamTable, PlayerTeamTable)
        }
    }

    @AfterTest
    fun cleanUp() {
        transaction {
            SchemaUtils.drop(UserTable, PlayerTable, TeamTable, PlayerTeamTable)
        }
    }
}
