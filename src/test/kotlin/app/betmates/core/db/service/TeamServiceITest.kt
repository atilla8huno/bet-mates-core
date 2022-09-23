package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
import app.betmates.core.db.entity.TeamEntity
import app.betmates.core.db.service.impl.PlayerServiceImpl
import app.betmates.core.db.service.impl.TeamServiceImpl
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.domain.FootballTeam
import app.betmates.core.domain.Player
import app.betmates.core.domain.SnookerTeam
import app.betmates.core.domain.Team
import app.betmates.core.domain.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
            val team = teamService.saveOrUpdate(SnookerTeam("Jimmy White"))
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

    @Test
    fun `should save the team and its players in the database =`() = transaction {
        setUp()

        runTest {
            // given
            val user1 = User("Cristiano Ronaldo", "cris@cr7.com")
            val user2 = User("Ronaldo Fenômeno", "ronaldo@r9.com")
            val player1 = Player("CR7", user = user1)
            val player2 = Player("R9", user = user2)
            val team = FootballTeam("Real Madrid").apply {
                addPlayer(player1)
                addPlayer(player2)
            }

            // when
            val savedTeam = teamService.save(team)

            // then
            val players = teamService.findById(savedTeam.id!!)!!.players()

            assertEquals(
                2,
                players.asSequence().filter {
                    it.nickName == player1.nickName || it.nickName == player2.nickName
                }.count()
            )
        }

        cleanUp()
    }

    @Test
    fun `should add players to an existing team in the database`() = transaction {
        setUp()

        runTest {
            // given
            val user1 = User("Cristiano Ronaldo", "cris@cr7.com")
            val player1 = Player("CR7", user = user1)
            val team = FootballTeam("Real Madrid").apply {
                addPlayer(player1)
            }
            var savedTeam = teamService.save(team)

            // when
            val user2 = User("Ronaldo Fenômeno", "ronaldo@r9.com")
            val player2 = Player("R9", user = user2)

            savedTeam.addPlayer(player2)

            savedTeam = teamService.update(team)

            // then
            val players = teamService.findById(savedTeam.id!!)!!.players()

            assertEquals(
                2,
                players.count {
                    it.nickName == player1.nickName || it.nickName == player2.nickName
                }
            )
        }

        cleanUp()
    }

    @Test
    fun `should remove players from the team in the database`() = transaction {
        setUp()

        runTest {
            // given
            val user1 = User("Cristiano Ronaldo", "cris@cr7.com")
            val user2 = User("Ronaldo Fenômeno", "ronaldo@r9.com")
            val player1 = playerService.save(Player("CR7", user = user1))
            val player2 = playerService.save(Player("R9", user = user2))
            val team = SnookerTeam("Real Snooker").apply {
                addPlayer(player1)
                addPlayer(player2)
            }
            var savedTeam = teamService.save(team)

            // when
            savedTeam.removePlayer(player2)

            savedTeam = teamService.update(savedTeam)

            // then
            val players = teamService.findById(savedTeam.id!!)!!.players()

            assertEquals(1, players.size)
            assertEquals(
                1,
                players.count {
                    it.nickName == player1.nickName
                }
            )
        }

        cleanUp()
    }

    @Test
    fun `should find teams by name in the database`() = transaction {
        setUp()

        runTest {
            // given
            val team1 = teamService.save(FootballTeam("Real Madrid"))
            val team2 = teamService.save(FootballTeam("Real Betis"))
            val team3 = teamService.save(FootballTeam("Real Sociedad"))
            val team4 = teamService.save(FootballTeam("Liverpool"))

            // when
            val teams = teamService.findByName("real")

            // then
            val list = mutableSetOf<Team>()
            teams.collect {
                list.add(it)
            }

            assertEquals(3, list.size)
            assertTrue { list.contains(team1) }
            assertTrue { list.contains(team2) }
            assertTrue { list.contains(team3) }
            assertFalse { list.contains(team4) }
        }

        cleanUp()
    }
}
