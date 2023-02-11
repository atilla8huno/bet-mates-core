package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.PlayerRequest
import app.betmates.core.api.dto.PlayerResponse
import app.betmates.core.db.service.PlayerService
import app.betmates.core.domain.Player
import app.betmates.core.domain.User
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
internal class UpdatePlayerCommandUTest {

    private lateinit var updatePlayerCommand: Command<PlayerRequest, PlayerResponse>
    private lateinit var playerService: PlayerService

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

        playerService = mockk()
        updatePlayerCommand = UpdatePlayerCommand(playerService)
    }

    @Test
    fun `should the update command call the service to update the properties of player`() = runTest {
        // given
        val request = PlayerRequest(
            id = 1L,
            userId = 1L,
            nickName = "The Rocket"
        )

        val expectedPlayer = Player(
            id = 1L,
            nickName = request.nickName,
            user = User(id = request.userId)
        )

        coEvery {
            playerService.update(any())
        } returns expectedPlayer

        coEvery {
            playerService.findById(eq(request.id))
        } returns expectedPlayer

        // when
        val response = updatePlayerCommand.execute(request)

        // then
        assertNotNull(response)
        assertEquals(1L, response.id)
        assertEquals(request.nickName, response.nickName)

        coVerify {
            playerService.findById(eq(request.id))
        }
        coVerify {
            playerService.update(any())
        }
    }

    @Test
    fun `should throw exception if player is not found by ID provided`() = runTest {
        // given
        val request = PlayerRequest(
            id = 1L,
            userId = 1L,
            nickName = "The Rocket"
        )

        coEvery {
            playerService.findById(eq(request.id))
        } returns null

        // when
        assertThrows<IllegalStateException>("Player not found for ID ${request.id}") {
            // when
            updatePlayerCommand.execute(request)
        }

        coVerify {
            playerService.findById(eq(request.id))
        }
        coVerify {
            playerService.update(any()) wasNot Called
        }
    }
}
