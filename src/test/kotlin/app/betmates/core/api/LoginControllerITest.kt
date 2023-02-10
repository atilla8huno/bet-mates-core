package app.betmates.core.api

import app.betmates.core.api.dto.SignInRequest
import app.betmates.core.api.dto.SignInResponse
import app.betmates.core.api.dto.SignUpRequest
import app.betmates.core.api.dto.SignUpResponse
import io.ktor.client.request.get
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
            assertEquals(HttpStatusCode.Created, status)

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

        // does not allow access without authentication
        client.get("/").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
            assertEquals("Authentication token is not valid or has expired", bodyAsText())
        }

        // { "email":"userX@b.c", "password":"123456" }
        val request = Json.encodeToString(
            value = SignInRequest(
                email = email,
                password = password
            )
        )

        // when
        var token: String?
        client.post("/api/sign-in") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.OK, status)

            // { "username":"userX", "token":"xxx", "expiresAt":000 }
            val response = Json.decodeFromString(SignInResponse.serializer(), bodyAsText())
            token = response.token

            assertEquals(username, response.username)
            assertFalse { response.token.isBlank() }
            assertTrue { response.expiresAt > System.currentTimeMillis() }
        }

        // can access with token in headers
        client.get("/") {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun `should return error 401 Unauthorized when trying to log in with wrong credentials`() = testApplication {
        // given
        val email = "userY@aaa.bbb"
        val username = "userXXX"
        val password = "123456"

        insertUser(email, username, password)

        // does not allow access without authentication
        client.get("/").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
            assertEquals("Authentication token is not valid or has expired", bodyAsText())
        }

        // { "email":"userY@aaa.bbb", "password":"WrongStuff" }
        val request = Json.encodeToString(
            value = SignInRequest(
                email = email,
                password = "WrongStuff"
            )
        )

        // when
        client.post("/api/sign-in") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.Unauthorized, status)
            assertEquals("Email or password is incorrect", bodyAsText())
        }

        // does not allow access without authentication
        client.get("/").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
            assertEquals("Authentication token is not valid or has expired", bodyAsText())
        }
    }

    @Test
    fun `should return error 409 Conflict when trying to sign up an user that already exists`() = testApplication {
        // given
        val request = Json.encodeToString(
            value = SignUpRequest(
                name = "Hey Joe",
                email = "joeee@gmail.com",
                username = "joeee",
                password = "heyjoe"
            )
        )

        client.post("/api/sign-up") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }

        // when
        client.post("/api/sign-up") {
            contentType(ContentType.Application.Json)
            // same request
            setBody(request)
        }.apply {
            // then
            assertEquals(HttpStatusCode.Conflict, status)
            assertEquals("E-mail/Username already exists", bodyAsText())
        }
    }
}
