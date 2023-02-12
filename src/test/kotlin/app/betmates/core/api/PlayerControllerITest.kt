package app.betmates.core.api

import app.betmates.core.api.dto.PlayerRequest
import app.betmates.core.api.dto.PlayerResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.client.utils.EmptyContent.headers
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

    @Test
    fun `should accept PUT request on update player API`() = testApplication {
        // given
        val token = authenticateUser(client)
        savePlayer(client, token)

        /*
        {
          "id": 1,
          "userId": 1,
          "nickName": "The Rocket"
        }
         */
        val expectedNickName = "The Rocket"
        val request = Json.encodeToString(
            value = PlayerRequest(
                id = 1L,
                userId = 1L,
                nickName = expectedNickName
            )
        )

        // when
        client.put("/api/player/1") {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.OK, status)

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
    fun `should not accept PUT request on update player API with ID non-numeric`() = testApplication {
        // given
        val token = authenticateUser(client)

        val request = Json.encodeToString(
            value = PlayerRequest(
                id = 1L,
                userId = 1L,
                nickName = "The Rocket"
            )
        )

        // when ID not a number
        client.put("/api/player/thisIsNotANumber") {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Invalid ID", bodyAsText())
        }
    }

    @Test
    fun `should accept DELETE request on delete player API`() = testApplication {
        // given
        val token = authenticateUser(client)
        savePlayer(client, token)

        // when
        client.delete("/api/player/1") {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.apply {
            // then
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun `should not accept DELETE request on delete player API with ID non-numeric`() = testApplication {
        // given
        val token = authenticateUser(client)

        // when ID not a number
        client.delete("/api/player/thisIsNotANumber") {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.apply {
            // then
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Invalid ID", bodyAsText())
        }
    }

    private suspend fun savePlayer(
        client: HttpClient,
        token: String
    ) {
        val request = Json.encodeToString(
            value = PlayerRequest(
                userId = 1L,
                nickName = "SuperTester"
            )
        )

        client.post("/api/player") {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }
    }
}
