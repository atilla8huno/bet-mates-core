package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
import app.betmates.core.db.entity.UserRepository
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
    override fun `should save the domain in the database and find it by its ID`() {
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

    @Test
    override fun `should map entity to domain`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("Zinedine Zidane", "zizou@rm.es"))

                val entity = UserRepository.findById(user.id!!)

                // when
                val domain = userService.mapToDomain(entity!!)

                // then
                assertNotNull(domain)
                assertEquals(user.name, domain.name)
                assertEquals(user.email, domain.email)
                assertEquals(user.username, domain.username)
                assertEquals(user.isActive(), domain.isActive())
            }

            cleanUp()
        }
    }
}
