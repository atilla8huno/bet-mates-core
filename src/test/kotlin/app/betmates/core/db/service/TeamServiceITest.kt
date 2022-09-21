package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
import app.betmates.core.db.service.impl.PlayerServiceImpl
import app.betmates.core.db.service.impl.TeamServiceImpl
import app.betmates.core.db.service.impl.UserServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest

@ExperimentalCoroutinesApi
internal class TeamServiceITest : RepositoryTest() {

    private lateinit var teamService: TeamService

    private lateinit var playerService: PlayerService
    private lateinit var userService: UserService

    @BeforeTest
    fun init() {
        userService = UserServiceImpl(database = db)
        playerService = PlayerServiceImpl(userService, database = db)

        teamService = TeamServiceImpl(playerService, database = db)
    }

    override fun `should save the domain in the database`() = transaction {
        setUp()

        runTest {
        }

        cleanUp()
        TODO("Not yet implemented")
    }

    override fun `should update the domain in the database`() = transaction {
        setUp()

        runTest {
        }

        cleanUp()
        TODO("Not yet implemented")
    }

    override fun `should delete the domain in the database`() = transaction {
        setUp()

        runTest {
        }

        cleanUp()
        TODO("Not yet implemented")
    }

    override fun `should find a record in the database by id`() = transaction {
        setUp()

        runTest {
        }

        cleanUp()
        TODO("Not yet implemented")
    }

    override fun `should find all records in the database`() = transaction {
        setUp()

        runTest {
        }

        cleanUp()
        TODO("Not yet implemented")
    }

    override fun `should map entity to domain`() = transaction {
        setUp()

        runTest {
        }

        cleanUp()
        TODO("Not yet implemented")
    }
}
