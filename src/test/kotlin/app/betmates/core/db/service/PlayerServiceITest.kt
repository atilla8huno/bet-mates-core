package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
import app.betmates.core.db.entity.PlayerEntity
import app.betmates.core.db.entity.PlayerTeamTable.player
import app.betmates.core.db.service.impl.PlayerServiceImpl
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.domain.Player
import app.betmates.core.domain.User
import app.betmates.core.exception.NotFoundException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
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
internal class PlayerServiceITest : RepositoryTest() {

    private lateinit var playerService: PlayerService
    private lateinit var userService: UserService

    @BeforeTest
    fun init() {
        userService = UserServiceImpl()
        playerService = PlayerServiceImpl(userService)
    }

    @Test
    override fun `should save the domain in the database`() = transaction {
        runTest {
            // given
            val user = userService.save(User(name = "Cristiano Ronaldo", email = "cris@cr7.com"))

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
    }

    @Test
    override fun `should find a record in the database by id`() = transaction {
        runTest {
            // given
            val user = userService.save(User(name = "Cristiano Ronaldo", email = "cris@cr7.com"))
            val player = playerService.save(Player(nickName = "CR7", user = user))

            // when
            val foundPlayer = playerService.findById(player.id!!)

            // then
            assertNotNull(foundPlayer)
            assertEquals(player, foundPlayer)
        }
    }

    @Test
    override fun `should map entity to domain`() = transaction {
        runTest {
            // given
            val user = User(name = "Zinedine Zidane", email = "zizou@rm.es")
            val player = playerService.save(Player(nickName = "Zizou", user = user))

            val entity = PlayerEntity.findById(player.id!!)

            // when
            val domain = playerService.mapToDomain(entity!!)

            // then
            assertNotNull(domain)
            assertEquals(player, domain)
            assertEquals(player.nickName, domain.nickName)
            assertEquals(player.memberOf(), domain.memberOf())
            assertEquals(player.user, domain.user)
        }
    }

    @Test
    override fun `should find all records in the database`() = transaction {
        runTest {
            // given
            val user = userService.save(User(name = "User 1", email = "user@1.com"))
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
    }

    @Test
    override fun `should delete the domain in the database`() = transaction {
        runTest {
            // given
            val user = User(name = "User 1", email = "user@1.com")
            val player = playerService.save(Player(nickName = "CR7", user = user))
            assertNotNull(playerService.findById(player.id!!))

            // when
            playerService.delete(player)

            // then
            val foundPlayer = playerService.findById(player.id!!)

            assertNull(foundPlayer)
            // no cascade
            assertNotNull(userService.findById(player.user.id!!))
        }
    }

    @Test
    override fun `should delete the domain in the database by id`() = transaction {
        runTest {
            // given
            val user = User(name = "User 1", email = "user@1.com")
            val player = playerService.save(Player(nickName = "CR7", user = user))
            assertNotNull(playerService.findById(player.id!!))

            // when
            playerService.deleteById(player.id!!)

            // then
            val foundPlayer = playerService.findById(player.id!!)

            assertNull(foundPlayer)
            // no cascade
            assertNotNull(userService.findById(player.user.id!!))
        }
    }

    @Test
    override fun `should throw exception if entry is not found by id on delete`() = transaction {
        runTest {
            GlobalScope.launch(exceptionHandler) {
                // given
                val id = 1L
                assertNull(playerService.findById(id))

                // when
                playerService.deleteById(id)
            }.join()

            // then
            assertTrue { exceptions.size == 1 }
            assertTrue { exceptions[0] is NotFoundException }
            assertEquals("Entry not found for ID 1", exceptions[0].message)
        }
    }

    @Test
    override fun `should update the domain in the database`() = transaction {
        runTest {
            // given
            val user1 = userService.save(User(name = "User 1", email = "user1@1.com"))
            val user2 = userService.save(User(name = "User 2", email = "user2@2.com"))
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
    }

    @Test
    override fun `should throw exception if entry is not found by id on update`() = transaction {
        runTest {
            GlobalScope.launch(exceptionHandler) {
                // given
                val player = Player(id = 1L, nickName = "Messi", user = User())

                // when
                playerService.update(player)
            }.join()

            // then
            assertTrue { exceptions.size == 1 }
            assertTrue { exceptions[0] is NotFoundException }
            assertEquals("Entry not found for ID 1", exceptions[0].message)
        }
    }

    @Test
    fun `should find a player in the database by nickName`() = transaction {
        runTest {
            // given
            val user = userService.save(User(name = "User 1", email = "user@1.com"))
            playerService.save(Player(nickName = "Player 1", user = user))

            // then
            assertTrue {
                // when
                playerService.existsByNickName("Player 1")
            }
            assertFalse {
                // when
                playerService.existsByNickName("Wrong NickName")
            }
        }
    }
}
