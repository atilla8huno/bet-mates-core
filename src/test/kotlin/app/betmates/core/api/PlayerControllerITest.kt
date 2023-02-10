package app.betmates.core.api

import app.betmates.core.api.dto.PlayerRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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
internal class PlayerControllerITest : ControllerTest() {

    @Test
    fun `should not accept POST request on create player API without authentication`() = testApplication {
        // given
        /*
        {
          "nickName": "The Rocket",
          "userId": 1
        }
         */
        val request = Json.encodeToString(
            value = PlayerRequest(
                userId = 1L,
                nickName = "The Rocket"
            )
        )

        // when
        client.post("/api/player") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }
}
