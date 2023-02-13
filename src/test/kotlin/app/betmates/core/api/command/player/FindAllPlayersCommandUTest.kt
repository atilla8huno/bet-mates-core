package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.PaginatedRequest
import app.betmates.core.api.dto.PaginatedResponse
import app.betmates.core.api.dto.PlayerResponse
import app.betmates.core.db.service.PlayerService
import app.betmates.core.domain.Player
import app.betmates.core.domain.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
internal class FindAllPlayersCommandUTest {

    private lateinit var findAllPlayersCommand: Command<PaginatedRequest, PaginatedResponse<PlayerResponse>>
    private lateinit var playerService: PlayerService

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

        playerService = mockk()
        findAllPlayersCommand = FindAllPlayersCommand(playerService)
    }

    @Test
    fun `should return paginated records of a player`() = runTest {
        // given
        val limit = 3
        val offset = 0
        val expectedTotal = 100L
        val request = PaginatedRequest(limit, offset)

        coEvery {
            playerService.count()
        } returns expectedTotal

        coEvery {
            playerService.findAllPaginated(eq(limit), eq(offset))
        } returns listOf(
            Player(1L, "Player 1", user = User()),
            Player(2L, "Player 2", user = User()),
            Player(3L, "Player 3", user = User())
        ).asFlow()

        // when
        val response = findAllPlayersCommand.execute(request)

        // then
        assertEquals(limit, response.limit)
        assertEquals(offset, response.offset)
        assertEquals(expectedTotal, response.total)
        assertEquals(3, response.data.size)

        coVerify {
            playerService.findAllPaginated(eq(limit), eq(offset))
        }
        coVerify {
            playerService.count()
        }
    }
}
