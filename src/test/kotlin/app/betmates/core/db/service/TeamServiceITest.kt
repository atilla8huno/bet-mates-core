package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
import app.betmates.core.db.entity.TeamEntity
import app.betmates.core.db.service.impl.PlayerServiceImpl
import app.betmates.core.db.service.impl.TeamServiceImpl
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.domain.FootballTeam
import app.betmates.core.domain.SnookerTeam
import app.betmates.core.domain.Team
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    @Test
    override fun `should save the domain in the database`() = transaction {
        setUp()

        runTest {
            // given
            val team1 = FootballTeam("Real Madrid")
            val team2 = SnookerTeam("Ronnie O'Sullivan")

            // when
            val savedFootballTeam = teamService.save(team1)
            val savedSnookerTeam = teamService.save(team2)

            val foundTeam1 = teamService.findById(savedFootballTeam.id!!)
            val foundTeam2 = teamService.findById(savedSnookerTeam.id!!)

            // then
            assertNotNull(foundTeam1)
            assertNotNull(foundTeam2)
            assertEquals(savedFootballTeam, foundTeam1)
            assertEquals(savedSnookerTeam, foundTeam2)

            assertEquals(foundTeam1.name, savedFootballTeam.name)
            assertEquals(foundTeam1.type, savedFootballTeam.type)
            assertEquals(foundTeam1.status, savedFootballTeam.status)

            assertEquals(foundTeam2.name, savedSnookerTeam.name)
            assertEquals(foundTeam2.type, savedSnookerTeam.type)
            assertEquals(foundTeam2.status, savedSnookerTeam.status)
        }

        cleanUp()
    }

    @Test
    override fun `should update the domain in the database`() = transaction {
        setUp()

        runTest {
            // given
            val team = teamService.save(SnookerTeam("Jimmy White"))
            assertTrue { team.isActive() }

            var updatedTeam: Team = FootballTeam("Real Madrid")
                .apply {
                    id = team.id
                }.also {
                    it.deactivate()
                }

            // when
            updatedTeam = teamService.update(updatedTeam)

            // then
            val foundTeam = teamService.findById(team.id!!)!!

            assertEquals(updatedTeam, foundTeam)
            assertNotEquals(team.name, foundTeam.name)
            assertNotEquals(team.type, foundTeam.type)
            assertNotEquals(team.status, foundTeam.status)

            assertFalse { foundTeam.isActive() }
        }

        cleanUp()
    }

    @Test
    override fun `should delete the domain in the database`() = transaction {
        setUp()

        runTest {
            // given
            val team = teamService.save(FootballTeam("Real Madrid"))
            assertNotNull(teamService.findById(team.id!!))

            // when
            teamService.delete(team)

            // then
            val foundTeam = teamService.findById(team.id!!)
            assertNull(foundTeam)
        }

        cleanUp()
    }

    @Test
    override fun `should find a record in the database by id`() = transaction {
        setUp()

        runTest {
            // given
            val savedFootballTeam = teamService.save(FootballTeam("Real Madrid"))

            // when
            val foundTeam = teamService.findById(savedFootballTeam.id!!)

            // then
            assertNotNull(foundTeam)
        }

        cleanUp()
    }

    @Test
    override fun `should find all records in the database`() = transaction {
        setUp()

        runTest {
            // given
            val team1 = teamService.save(FootballTeam("Real Madrid"))
            val team2 = teamService.save(FootballTeam("Vasco da Gama"))
            val team3 = teamService.save(FootballTeam("Arsenal"))
            val team4 = teamService.save(FootballTeam("Liverpool"))

            // when
            val allTeams = teamService.findAll()

            // then
            val list = mutableSetOf<Team>()
            allTeams.collect {
                list.add(it)
            }

            assertTrue { list.contains(team1) }
            assertTrue { list.contains(team2) }
            assertTrue { list.contains(team3) }
            assertTrue { list.contains(team4) }
        }

        cleanUp()
    }

    @Test
    override fun `should map entity to domain`() = transaction {
        setUp()

        runTest {
            // given
            val team = teamService.save(SnookerTeam("Judd Trump"))
            val entity = TeamEntity.findById(team.id!!)

            // when
            val domain = teamService.mapToDomain(entity!!)

            // then
            assertNotNull(domain)
            assertEquals(team.name, domain.name)
            assertEquals(team.type, domain.type)
            assertEquals(team.status, domain.status)
        }

        cleanUp()
    }
}
