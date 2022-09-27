package app.betmates.core.api

import app.betmates.core.api.dto.SignInRequest
import app.betmates.core.api.dto.SignInResponse
import app.betmates.core.api.dto.SignUpRequest
import app.betmates.core.api.dto.SignUpResponse
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
internal class LoginControllerITest : ControllerTest() {

    @Test
    fun `should accept POST request on sign up API`() = testApplication {
        // given
        val customUsername = "heyjoe"

        /*
        {
           "name":"Hey Joe",
           "email":"heyjoe@gmail.com",
           "username":"heyjoe",
           "password":"heyjoe"
        }
         */
        val request = Json.encodeToString(
            value = SignUpRequest(
                name = "Hey Joe",
                email = "heyjoe@gmail.com",
                username = customUsername,
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

            /*
            {
                "id": 1,
                "username": "heyjoe"
            }
             */
            val response = Json.decodeFromString(SignUpResponse.serializer(), bodyAsText())

            assertEquals(1, response.id)
            assertEquals(customUsername, response.username)
        }
    }

    @Test
    fun `should authenticate user with email and password and then send a token in the response`() = testApplication {
        // given
        val email = "userX@b.c"
        val username = "userX"
        val password = "123456"

        insertUser(email, username, password)

        /*
        {
           "email":"userX@b.c",
           "password":"123456"
        }
         */
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

            /*
            {
               "username":"userX",
               "token":"xxx",
               "expiresAt":000
            }
             */
            val response = Json.decodeFromString(SignInResponse.serializer(), bodyAsText())

            assertEquals(username, response.username)
            assertFalse { response.token.isBlank() }
            assertTrue { response.expiresAt > System.currentTimeMillis() }
        }
    }
}
