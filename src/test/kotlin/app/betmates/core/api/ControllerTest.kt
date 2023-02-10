package app.betmates.core.api

import app.betmates.core.ITest
import app.betmates.core.api.dto.SignInRequest
import app.betmates.core.api.dto.SignInResponse
import app.betmates.core.db.entity.UserEntity
import app.betmates.core.db.entity.UserTable.email
import app.betmates.core.domain.User
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private const val DEFAULT_EMAIL = "test@gmail.com"
private const val DEFAULT_PASSWORD = "123456"
private const val DEFAULT_USERNAME = "test"

@ExperimentalCoroutinesApi
internal abstract class ControllerTest : ITest() {

    fun insertUser(
        customEmail: String = DEFAULT_EMAIL,
        customUsername: String = DEFAULT_USERNAME,
        customPassword: String = DEFAULT_PASSWORD
    ) = transaction {
        UserEntity.new {
            name = "Tester User"
            email = customEmail
            username = customUsername
            password = Bcrypt.hash(customPassword, User.SALT_ROUNDS).toString(Charsets.UTF_8)
        }
    }

    suspend fun authenticateUser(client: HttpClient): String {
        insertUser()

        val request = Json.encodeToString(
            value = SignInRequest(
                email = DEFAULT_EMAIL,
                password = DEFAULT_PASSWORD
            )
        )

        var token: String?
        client.post("/api/sign-in") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)

            val response = Json.decodeFromString(SignInResponse.serializer(), bodyAsText())
            token = response.token
            assertFalse { response.token.isBlank() }
        }

        return token!!
    }
}
