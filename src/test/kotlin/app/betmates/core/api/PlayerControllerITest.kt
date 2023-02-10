package app.betmates.core.api

import app.betmates.core.api.dto.PlayerRequest
import app.betmates.core.api.dto.PlayerResponse
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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
    fun `should accept POST request on create player API`() = testApplication {
        // given
        val token = authenticateUser(client)

        /*
        {
          "nickName": "The Rocket",
          "userId": 1
        }
         */
        val expectedNickName = "The Rocket"
        val request = Json.encodeToString(
            value = PlayerRequest(
                userId = 1L,
                nickName = expectedNickName
            )
        )

        // when
        client.post("/api/player") {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.Created, status)

            /*
            {
                "id": 1,
                "nickName": "The Rocket"
            }
             */
            val response = Json.decodeFromString(PlayerResponse.serializer(), bodyAsText())

            assertEquals(1, response.id)
            assertEquals(expectedNickName, response.nickName)
        }
    }

    @Test
    fun `should return error 409 Conflict when trying to create a player that already exists`() = testApplication {
        // given
        val token = authenticateUser(client)

        val expectedNickName = "The Rocket"
        val request = Json.encodeToString(
            value = PlayerRequest(
                userId = 1L,
                nickName = expectedNickName
            )
        )

        client.post("/api/player") {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }

        // when same request again
        client.post("/api/player") {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.Conflict, status)
            assertEquals("NickName already exists", bodyAsText())
        }
    }

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
