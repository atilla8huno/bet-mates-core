package app.betmates.core.api

import app.betmates.core.api.dto.UserRequest
import app.betmates.core.ktor.plugins.configureRouting
import app.betmates.core.ktor.plugins.configureSerialization
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.MapApplicationConfig
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
        environment {
            config = MapApplicationConfig(
                "ktor.environment" to "test"
            )
        }
        application {
            configureRouting()
            configureSerialization()
        }

        val request = Json.encodeToString(
            value =
            UserRequest(
                name = "Hey Joe",
                email = "heyjoe@gmail.com",
                username = "heyjoe",
                password = "heyjoe"
            )
        )

        client.post("/api/sign-up") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
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
