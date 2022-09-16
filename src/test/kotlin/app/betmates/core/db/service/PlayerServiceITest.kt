package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
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
import kotlin.test.assertNotNull

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
    fun `should save a player, associate it to an user and find it by its ID`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("Cristiano Ronaldo", "cris@cr7.com"))
                val player = Player("CR7")

                // when
                player.associateTo(user)
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
}
