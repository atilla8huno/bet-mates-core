package app.betmates.core

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

@ExperimentalCoroutinesApi
internal class CoroutineTest {

    private val apiService = mockk<ApiService>()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(TestScope().testScheduler))
    }

    @Test
    fun `should call API to get user asynchronously`() = runTest {
        // given
        val userId = "123"

        coEvery {
            apiService.getUser(userId)
        } returns User("User from tests")

        val presenter = UserPresenter(apiService)

        // when
        presenter.getAsync(userId)

        // then
        coVerify {
            apiService.getUser(userId)
        }
    }
}

class UserPresenter(
    private val apiService: ApiService
) {
    suspend fun getAsync(id: String) = coroutineScope {
        launch {
            apiService.getUser(id)
        }
    }
}

interface ApiService {
    fun getUser(id: String): User
}

data class User(val name: String)
