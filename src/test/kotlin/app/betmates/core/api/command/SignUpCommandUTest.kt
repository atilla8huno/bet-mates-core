package app.betmates.core.api.command

import app.betmates.core.api.dto.SignUpRequest
import app.betmates.core.api.dto.SignUpResponse
import app.betmates.core.db.service.UserService
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
internal class SignUpCommandUTest {

    private lateinit var signUpCommand: Command<SignUpRequest, SignUpResponse>
    private lateinit var userService: UserService

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

        userService = mockk()
        signUpCommand = SignUpCommand(userService)
    }

    @Test
    fun `should the sign up command call the service to save an user given a request`() = runTest {
        // given
        val request = SignUpRequest(
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

        coEvery {
            userService.existsByEmailOrUsername(any(), any())
        } returns false

        // when
        val response = signUpCommand.execute(request)

        // then
        assertNotNull(response)
        assertEquals(1L, response.id)
        assertEquals(request.username, response.username)

        coVerify {
            userService.save(any())
        }
    }

    @Test
    fun `should not save user if already exists`() = runTest {
        // given
        val request = SignUpRequest(
            name = "Ronald O'Sullivan",
            email = "ronnie@osullivan.com",
            username = "ronnie",
            password = "abc123"
        )

        coEvery {
            userService.existsByEmailOrUsername(eq(request.email), eq(request.username))
        } returns true

        // then
        assertThrows<IllegalArgumentException>("E-mail/Username already exists.") {
            // when
            signUpCommand.execute(request)
        }

        coVerify {
            userService.existsByEmailOrUsername(eq(request.email), eq(request.username))
        }

        coVerify {
            userService.save(any()) wasNot Called
        }
    }
}
