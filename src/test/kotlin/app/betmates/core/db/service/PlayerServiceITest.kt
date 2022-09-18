package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
import app.betmates.core.db.entity.PlayerRepository
import app.betmates.core.db.service.impl.PlayerServiceImpl
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.domain.Player
import app.betmates.core.domain.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerServiceITest : RepositoryTest() {

    private lateinit var playerService: PlayerService
    private lateinit var userService: UserService

    @BeforeTest
    fun init() {
        userService = UserServiceImpl(database = db)
        playerService = PlayerServiceImpl(userService, database = db)
    }

    @Test
    override fun `should save the domain in the database`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("Cristiano Ronaldo", "cris@cr7.com"))

                // when
                val player = Player(nickName = "CR7", user = user)
                val savedPlayer = playerService.save(player)

                // then
                assertNotNull(savedPlayer)
                assertNotNull(savedPlayer.id)
                assertEquals(savedPlayer.user, user)

                assertEquals(player.nickName, savedPlayer.nickName)
                assertEquals(player.memberOf(), savedPlayer.memberOf())
            }

            cleanUp()
        }
    }

    @Test
    override fun `should find a record in the database by id`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("Cristiano Ronaldo", "cris@cr7.com"))
                val player = playerService.save(Player(nickName = "CR7", user = user))

                // when
                val foundPlayer = playerService.findById(player.id!!)

                // then
                assertNotNull(foundPlayer)
                assertEquals(player, foundPlayer)
            }

            cleanUp()
        }
    }

    @Test
    override fun `should map entity to domain`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("Zinedine Zidane", "zizou@rm.es"))
                val player = playerService.save(Player(nickName = "Zizou", user = user))

                val entity = PlayerRepository.findById(player.id!!)

                // when
                val domain = playerService.mapToDomain(entity!!)

                // then
                assertNotNull(domain)
                assertEquals(player, domain)
                assertEquals(player.nickName, domain.nickName)
                assertEquals(player.memberOf(), domain.memberOf())
                assertEquals(player.user, domain.user)
            }

            cleanUp()
        }
    }

    @Test
    override fun `should find all records in the database`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("User 1", "user@1.com"))
                val player1 = playerService.save(Player(nickName = "Player 1", user = user))
                val player2 = playerService.save(Player(nickName = "Player 2", user = user))
                val player3 = playerService.save(Player(nickName = "Player 3", user = user))
                val player4 = playerService.save(Player(nickName = "Player 4", user = user))

                // when
                val allPlayers = playerService.findAll()

                // then
                val list = mutableSetOf<Player>()
                allPlayers.collect {
                    list.add(it)
                }

                assertTrue { list.contains(player1) }
                assertTrue { list.contains(player2) }
                assertTrue { list.contains(player3) }
                assertTrue { list.contains(player4) }
            }

            cleanUp()
        }
    }

    @Test
    override fun `should delete the domain in the database`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("User 1", "user@1.com"))
                val player = playerService.save(Player(nickName = "CR7", user = user))

                // when
                playerService.delete(player)

                // then
                val foundPlayer = playerService.findById(player.id!!)

                assertNull(foundPlayer)
            }

            cleanUp()
        }
    }

    @Test
    override fun `should update the domain in the database`() {
        transaction {
            setUp()

            runTest {
                // given
                val user1 = userService.save(User("User 1", "user@1.com"))
                val user2 = userService.save(User("User 2", "user@2.com"))
                val player = playerService.save(Player(nickName = "CR7", user = user1))

                assertEquals(player.user, user1)

                var updatedPlayer = Player(nickName = "Messi", user = user2)
                    .apply {
                        id = player.id
                    }

                // when
                updatedPlayer = playerService.update(updatedPlayer)

                // then
                val foundPlayer = playerService.findById(player.id!!)!!

                assertEquals(player.id!!, updatedPlayer.id!!)
                assertEquals(player.id!!, foundPlayer.id!!)

                assertEquals(player, foundPlayer)
                assertNotEquals(player.nickName, foundPlayer.nickName)
                assertNotEquals(player.user, foundPlayer.user)
                assertEquals(foundPlayer.user, user2)
            }

            cleanUp()
        }
    }
}
