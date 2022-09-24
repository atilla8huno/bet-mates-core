package app.betmates.core.api

import app.betmates.core.api.dto.SignUpRequest
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
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
internal class LoginControllerITest : ControllerTest() {

    @Test
    fun `should accept POST request on sign up API`() = testApplication {
        // given
        val request = Json.encodeToString(
            value =
            SignUpRequest(
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
}
