package app.betmates.core.api.command

import app.betmates.core.api.command.impl.UserCommandImpl
import app.betmates.core.api.dto.UserRequest
import app.betmates.core.db.service.UserService
import app.betmates.core.domain.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
internal class UserCommandUTest {

    private lateinit var userCommand: UserCommand
    private lateinit var userService: UserService

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

        userService = mockk()
        userCommand = UserCommandImpl(userService)
    }

    @Test
    fun `should the sign up command call the service to save an user given a request`() = runTest {
        // given
        val request = UserRequest(
            name = "Ronald O'Sullivan",
            email = "ronnie@osullivan.com",
            username = "ronnie",
            password = "abc123"
        )

        coEvery {
            userService.save(any())
        } returns User(request.name, request.email, request.username).apply {
            acceptPassword(request.password)
            id = 1L
        }

        // when
        val response = userCommand.signUp(request)

        // then
        assertNotNull(response)
        assertEquals(1L, response.id)
        assertEquals(request.username, response.username)

        coVerify {
            userService.save(any())
        }
    }
}
