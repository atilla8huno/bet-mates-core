package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
import app.betmates.core.db.entity.PlayerTable.user
import app.betmates.core.db.entity.TeamEntity
import app.betmates.core.db.service.impl.PlayerServiceImpl
import app.betmates.core.db.service.impl.TeamServiceImpl
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.domain.FootballTeam
import app.betmates.core.domain.Player
import app.betmates.core.domain.SnookerTeam
import app.betmates.core.domain.Team
import app.betmates.core.domain.User
import app.betmates.core.exception.NotFoundException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
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

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
internal class TeamServiceITest : RepositoryTest() {

    private lateinit var teamService: TeamService

    private lateinit var playerService: PlayerService
    private lateinit var userService: UserService

    @BeforeTest
    fun init() {
        userService = UserServiceImpl()
        playerService = PlayerServiceImpl(userService)

        teamService = TeamServiceImpl(playerService)
    }

    @Test
    override fun `should save the domain in the database`() = transaction {
        runTest {
            // given
            val team1 = FootballTeam(name = "Real Madrid")
            val team2 = SnookerTeam(name = "Ronnie O'Sullivan")

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
    }

    @Test
    override fun `should update the domain in the database`() = transaction {
        runTest {
            // given
            val team = teamService.saveOrUpdate(SnookerTeam(name = "Jimmy White"))
            assertTrue { team.isActive() }

            var updatedTeam: Team = FootballTeam(name = "Real Madrid")
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
    }

    @Test
    override fun `should throw exception if entry is not found by id on update`() = transaction {
        runTest {
            GlobalScope.launch(exceptionHandler) {
                // given
                val team = FootballTeam(id = 1L, name = "Real Madrid")

                // when
                teamService.update(team)
            }.join()

            // then
            assertTrue { exceptions.size == 1 }
            assertTrue { exceptions[0] is NotFoundException }
            assertEquals("Entry not found for ID 1", exceptions[0].message)
        }
    }

    @Test
    override fun `should delete the domain in the database`() = transaction {
        runTest {
            // given
            val team = teamService.save(FootballTeam(name = "Real Madrid"))
            assertNotNull(teamService.findById(team.id!!))

            // when
            teamService.delete(team)

            // then
            val foundTeam = teamService.findById(team.id!!)
            assertNull(foundTeam)
        }
    }

    @Test
    override fun `should delete the domain in the database by id`() = transaction {
        runTest {
            // given
            val team = teamService.save(FootballTeam(name = "Real Madrid"))
            assertNotNull(teamService.findById(team.id!!))

            // when
            teamService.deleteById(team.id!!)

            // then
            val foundTeam = teamService.findById(team.id!!)
            assertNull(foundTeam)
        }
    }

    @Test
    override fun `should throw exception if entry is not found by id on delete`() = transaction {
        runTest {
            GlobalScope.launch(exceptionHandler) {
                // given
                val id = 1L
                assertNull(teamService.findById(id))

                // when
                teamService.deleteById(id)
            }.join()

            // then
            assertTrue { exceptions.size == 1 }
            assertTrue { exceptions[0] is NotFoundException }
            assertEquals("Entry not found for ID 1", exceptions[0].message)
        }
    }

    @Test
    override fun `should find a record in the database by id`() = transaction {
        runTest {
            // given
            val savedFootballTeam = teamService.save(FootballTeam(name = "Real Madrid"))

            // when
            val foundTeam = teamService.findById(savedFootballTeam.id!!)

            // then
            assertNotNull(foundTeam)
        }
    }

    @Test
    override fun `should find all records in the database`() = transaction {
        runTest {
            // given
            val team1 = teamService.save(FootballTeam(name = "Real Madrid"))
            val team2 = teamService.save(FootballTeam(name = "Vasco da Gama"))
            val team3 = teamService.save(FootballTeam(name = "Arsenal"))
            val team4 = teamService.save(FootballTeam(name = "Liverpool"))

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
    }

    @Test
    override fun `should find all records paginated in the database`() = transaction {
        runTest {
            // given
            val limit = 10
            val offset = 5

            for (num in 1..20) {
                teamService.save(FootballTeam(name = "Team $num"))
            }

            // when
            val allTeams = teamService.findAllPaginated(limit, offset)

            // then
            val list = mutableSetOf<Team>()
            allTeams.collect {
                list.add(it)
            }

            assertTrue { list.size == limit }
            for (num in offset + 1..offset + limit) {
                assertTrue { list.map { it.name }.contains("Team $num") }
            }
        }
    }

    @Test
    override fun `should map entity to domain`() = transaction {
        runTest {
            // given
            val team = teamService.save(SnookerTeam(name = "Judd Trump"))
            val entity = TeamEntity.findById(team.id!!)

            // when
            val domain = teamService.mapToDomain(entity!!)

            // then
            assertNotNull(domain)
            assertEquals(team.name, domain.name)
            assertEquals(team.type, domain.type)
            assertEquals(team.status, domain.status)
        }
    }

    @Test
    fun `should save the team and its players in the database =`() = transaction {
        runTest {
            // given
            val user1 = User(name = "Cristiano Ronaldo", email = "cris@cr7.com")
            val user2 = User(name = "Ronaldo Fenômeno", email = "ronaldo@r9.com")
            val player1 = Player(nickName = "CR7", user = user1)
            val player2 = Player(nickName = "R9", user = user2)
            val team = FootballTeam(name = "Real Madrid").apply {
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
    }

    @Test
    fun `should add players to an existing team in the database`() = transaction {
        runTest {
            // given
            val user1 = User(name = "Cristiano Ronaldo", email = "cris@cr7.com")
            val player1 = Player(nickName = "CR7", user = user1)
            val team = FootballTeam(name = "Real Madrid").apply {
                addPlayer(player1)
            }
            var savedTeam = teamService.save(team)

            // when
            val user2 = User(name = "Ronaldo Fenômeno", email = "ronaldo@r9.com")
            val player2 = Player(nickName = "R9", user = user2)

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
    }

    @Test
    fun `should remove players from the team in the database`() = transaction {
        runTest {
            // given
            val user1 = User(name = "Cristiano Ronaldo", email = "cris@cr7.com")
            val user2 = User(name = "Ronaldo Fenômeno", email = "ronaldo@r9.com")
            val player1 = playerService.save(Player(nickName = "CR7", user = user1))
            val player2 = playerService.save(Player(nickName = "R9", user = user2))
            val team = SnookerTeam(name = "Real Snooker").apply {
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
    }

    @Test
    fun `should find teams by name in the database`() = transaction {
        runTest {
            // given
            val team1 = teamService.save(FootballTeam(name = "Real Madrid FC"))
            val team2 = teamService.save(FootballTeam(name = "FC Betis Real"))
            val team3 = teamService.save(FootballTeam(name = "FC Real Sociedad"))
            val team4 = teamService.save(FootballTeam(name = "An Unrealistic Team"))
            val team5 = teamService.save(FootballTeam(name = "Liverpool"))

            // when
            val teams = teamService.findByName("real")

            // then
            val list = mutableSetOf<Team>()
            teams.toCollection(list)

            assertEquals(4, list.size)

            assertTrue { list.contains(team1) }
            assertTrue { list.contains(team2) }
            assertTrue { list.contains(team3) }
            assertTrue { list.contains(team4) }

            assertFalse { list.contains(team5) }
        }
    }
}
