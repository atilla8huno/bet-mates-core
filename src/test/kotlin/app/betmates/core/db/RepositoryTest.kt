package app.betmates.core.db

import app.betmates.core.db.entity.PlayerTable
import app.betmates.core.db.entity.PlayerTeamTable
import app.betmates.core.db.entity.TeamTable
import app.betmates.core.db.entity.UserTable
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

@ExperimentalCoroutinesApi
internal abstract class RepositoryTest {

    val db by lazy {
        Database.connect(
            url = "jdbc:h2:mem:test",
            driver = "org.h2.Driver",
            user = "root"
        )
    }

    abstract fun `should save the domain in the database`()
    abstract fun `should update the domain in the database`()
    abstract fun `should delete the domain in the database`()
    abstract fun `should find a record in the database by id`()
    abstract fun `should find all records in the database`()
    abstract fun `should map entity to domain`()

    @BeforeTest
    fun connect() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))
    }

    fun Transaction.setUp() {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(UserTable, PlayerTable, TeamTable, PlayerTeamTable)
    }

    fun cleanUp() {
        SchemaUtils.drop(UserTable, PlayerTable, TeamTable, PlayerTeamTable)
    }
}
