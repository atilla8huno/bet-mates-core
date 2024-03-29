package app.betmates.core.api.command.login

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.SignInRequest
import app.betmates.core.api.dto.SignInResponse
import app.betmates.core.db.service.UserService
import app.betmates.core.domain.User
import app.betmates.core.exception.AuthenticationFailed
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
internal class SignInCommandUTest {

    private lateinit var signInCommand: Command<SignInRequest, SignInResponse>
    private lateinit var userService: UserService

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))

        userService = mockk()
        signInCommand = SignInCommand(userService)
    }

    @Test
    fun `should the sign in command authenticate an user and generate a token`() = runTest {
        // given
        val request = SignInRequest(
            email = "ronnie@osullivan.com",
            password = "abc123"
        )

        coEvery {
            userService.findByEmailAndPassword(eq(request.email), eq(request.password))
        } returns User(email = request.email)

        // when
        val response = signInCommand.execute(request)

        // then
        assertNotNull(response)
        assertNotNull(response.username)
        assertFalse { response.token.isBlank() }
        assertTrue {
            response.expiresAt > System.currentTimeMillis() + 59000L &&
                response.expiresAt < System.currentTimeMillis() + 61000L
        }

        coVerify {
            userService.findByEmailAndPassword(eq(request.email), eq(request.password))
        }
    }

    @Test
    fun `should the sign in command fail to find an user by email and password then an exception is thrown`() = runTest {
        // given
        val request = SignInRequest(
            email = "ronnie@osullivan.com",
            password = "abc123"
        )

        coEvery {
            userService.findByEmailAndPassword(any(), any())
        } returns null

        // then
        assertThrows<AuthenticationFailed>("Email or password is incorrect.") {
            // when
            signInCommand.execute(request)
        }

        coVerify {
            userService.findByEmailAndPassword(any(), any())
        }
    }
}
