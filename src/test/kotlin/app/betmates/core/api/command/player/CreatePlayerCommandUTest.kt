package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.PlayerRequest
import app.betmates.core.api.dto.PlayerResponse
import app.betmates.core.db.service.PlayerService
import app.betmates.core.domain.Player
import app.betmates.core.domain.User
import app.betmates.core.exception.ConflictException
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.assertThrows
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
internal class CreatePlayerCommandUTest {

    private lateinit var savePlayerCommand: Command<PlayerRequest, PlayerResponse>
    private lateinit var playerService: PlayerService

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

        playerService = mockk()
        savePlayerCommand = CreatePlayerCommand(playerService)
    }

    @Test
    fun `should the save player command call the service to save a player from a given request`() = runTest {
        // given
        val request = PlayerRequest(
            userId = 1L,
            nickName = "The Rocket"
        )

        coEvery {
            playerService.save(any())
        } returns Player(
            id = 1L,
            nickName = request.nickName,
            user = User(id = request.userId, email = "ronnie@therocket.com")
        )
        coEvery {
            playerService.existsByNickName(eq(request.nickName))
        } returns false

        // when
        val response = savePlayerCommand.execute(request)

        // then
        assertNotNull(response)
        assertEquals(1L, response.id)
        assertEquals(request.nickName, response.nickName)

        coVerify {
            playerService.save(any())
        }
    }

    @Test
    fun `should not save a player if there is one already with the same nickname`() = runTest {
        // given
        val request = PlayerRequest(
            userId = 1L,
            nickName = "The Rocket"
        )

        coEvery {
            playerService.existsByNickName(eq(request.nickName))
        } returns true

        // then
        assertThrows<ConflictException>("NickName already exists") {
            // when
            savePlayerCommand.execute(request)
        }

        coVerify {
            playerService.existsByNickName(eq(request.nickName))
        }

        coVerify {
            playerService.save(any()) wasNot Called
        }
    }
}
