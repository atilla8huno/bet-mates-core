package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.domain.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class UserServiceITest : RepositoryTest() {

    private lateinit var userService: UserService

    @BeforeTest
    fun init() {
        userService = UserServiceImpl(database = db)
    }

    @Test
    fun `should save a user in the database and find it by its ID`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = User(
                    name = "Alfa Romeo",
                    email = "alfaromeo@alfaromeo.com"
                )
                assertNull(user.id)

                // when
                val userSaved = userService.save(user)
                val userFound = userService.findById(user.id!!)

                // then
                assertNotNull(userSaved)
                assertNotNull(userFound)
                assertEquals(userSaved, userFound)

                assertEquals(userSaved.name, userFound.name)
                assertEquals(userSaved.email, userFound.email)
                assertEquals(userSaved.username, userFound.username)
                assertEquals(userSaved.isActive(), userFound.isActive())
            }

            cleanUp()
        }
    }
}
