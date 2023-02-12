package app.betmates.core.api.command.player

import app.betmates.core.api.command.Command
import app.betmates.core.db.service.PlayerService
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

@ExperimentalCoroutinesApi
internal class DeletePlayerCommandUTest {

    private lateinit var deletePlayerCommand: Command<Long, Unit>
    private lateinit var playerService: PlayerService

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

        playerService = mockk()
        deletePlayerCommand = DeletePlayerCommand(playerService)
    }

    @Test
    fun `should the delete command call the service to remove a player`() = runTest {
        // given
        val request = 1L
        coEvery {
            playerService.deleteById(eq(request))
        } just Runs

        // when
        deletePlayerCommand.execute(request)

        // then
        coVerify {
            playerService.deleteById(eq(request))
        }
    }
}
