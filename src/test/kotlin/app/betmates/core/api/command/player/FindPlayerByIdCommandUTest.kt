package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.PlayerResponse
import app.betmates.core.db.service.PlayerService
import app.betmates.core.domain.Player
import app.betmates.core.domain.User
import app.betmates.core.exception.NotFoundException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.assertThrows
import javax.management.Query.eq
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@ExperimentalCoroutinesApi
internal class FindPlayerByIdCommandUTest {

    private lateinit var findPlayerByIdCommand: Command<Long, PlayerResponse>
    private lateinit var playerService: PlayerService

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

        playerService = mockk()
        findPlayerByIdCommand = FindPlayerByIdCommand(playerService)
    }

    @Test
    fun `should the find by id command call the service to find player with given ID`() = runTest {
        // given
        val request = 1L
        coEvery {
            playerService.findById(eq(request))
        } returns async {
            Player(
                id = request,
                nickName = "Player",
                user = User()
            )
        }

        // when
        val response = findPlayerByIdCommand.execute(request)

        // then
        assertEquals(request, response.id)
        assertFalse { response.nickName.isBlank() }
        coVerify {
            playerService.findById(eq(request))
        }
    }

    @Test
    fun `should throw exception when ID is not found`() = runTest {
        // given
        val request = 1L
        coEvery {
            playerService.findById(eq(request))
        } returns async { null }

        // when
        assertThrows<NotFoundException>("Entry not found for id 1") {
            findPlayerByIdCommand.execute(request)
        }

        // then
        coVerify {
            playerService.findById(eq(request))
        }
    }
}
