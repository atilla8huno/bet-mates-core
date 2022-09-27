package app.betmates.core.api

import app.betmates.core.api.dto.SignInRequest
import app.betmates.core.api.dto.SignUpRequest
import app.betmates.core.db.entity.UserEntity
import app.betmates.core.domain.User
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
internal class LoginControllerITest : ControllerTest() {

    @Test
    fun `should accept POST request on sign up API`() = testApplication {
        // given
        val request = Json.encodeToString(
            value = SignUpRequest(
                name = "Hey Joe",
                email = "heyjoe@gmail.com",
                username = "heyjoe",
                password = "heyjoe"
            )
        )

        // when
        client.post("/api/sign-up") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(
                """
                {
                    "id": 1,
                    "username": "heyjoe"
                }
                """.trimIndent(),
                bodyAsText()
            )
        }
    }

    @Test
    fun `should authenticate user with email and password and then send a token in the response`() = testApplication {
        // given
        val email = "userX@b.c"
        val username = "123456"
        val password = "userX"

        insertUser(email, username, password)

        val request = Json.encodeToString(
            value = SignInRequest(
                email = email,
                password = password
            )
        )

        // when
        client.post("/api/sign-in") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.OK, status)

            val response = bodyAsText()
            assertTrue { response.contains("token") }
            assertTrue { response.contains("username") }
            assertTrue { response.contains(username) }
        }
    }

    private fun insertUser(
        customEmail: String,
        customUsername: String,
        customPassword: String
    ) = transaction {
        UserEntity.new {
            name = "user X"
            email = customEmail
            username = customUsername
            password = User.encrypt(customPassword)
        }
    }
}
