package app.betmates.core.api

import app.betmates.core.ITest
import app.betmates.core.db.entity.UserEntity
import app.betmates.core.domain.User
import com.toxicbakery.bcrypt.Bcrypt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.exposed.sql.transactions.transaction

@ExperimentalCoroutinesApi
internal abstract class ControllerTest : ITest() {

    fun insertUser(
        customEmail: String,
        customUsername: String,
        customPassword: String
    ) = transaction {
        UserEntity.new {
            name = "user X"
            email = customEmail
            username = customUsername
            password = Bcrypt.hash(customPassword, User.SALT_ROUNDS).toString(Charsets.UTF_8)
        }
    }
}
