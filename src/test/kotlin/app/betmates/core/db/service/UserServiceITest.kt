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
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserServiceITest : RepositoryTest() {

    private lateinit var userService: UserService

    @BeforeTest
    fun init() {
        userService = UserServiceImpl(database = db)
    }

    @Test
    override fun `should save the domain in the database`() {
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

    @Test
    override fun `should find all records in the database`() {
        transaction {
            setUp()

            runTest {
                // given
                val user1 = userService.save(User("User 1", "user@1.com"))
                val user2 = userService.save(User("User 2", "user@2.com"))
                val user3 = userService.save(User("User 3", "user@3.com"))
                val user4 = userService.save(User("User 4", "user@4.com"))

                // when
                val allUsers = userService.findAll()

                // then
                val list = mutableSetOf<User>()
                allUsers.collect {
                    list.add(it)
                }

                assertTrue { list.contains(user1) }
                assertTrue { list.contains(user2) }
                assertTrue { list.contains(user3) }
                assertTrue { list.contains(user4) }
            }

            cleanUp()
        }
    }

    @Test
    override fun `should update the domain in the database`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("User 1", "user@1.com"))
                assertTrue { user.isActive() }

                var updatedUser = User("New Name", "new@email.com")
                    .apply {
                        id = user.id
                    }.also {
                        it.deactivate()
                    }

                // when
                updatedUser = userService.update(updatedUser)

                // then
                val foundUser = userService.findById(user.id!!)!!

                assertEquals(updatedUser, foundUser)
                assertNotEquals(user.name, foundUser.name)
                assertNotEquals(user.email, foundUser.email)
                assertNotEquals(user.username, foundUser.username)

                assertFalse { foundUser.isActive() }
            }

            cleanUp()
        }
    }

    @Test
    override fun `should delete the domain in the database`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("User 1", "user@1.com"))

                // when
                userService.delete(user)

                // then
                val foundUser = userService.findById(user.id!!)

                assertNull(foundUser)
            }

            cleanUp()
        }
    }

    override fun `should find a record in the database by id`() {
        transaction {
            setUp()

            runTest {
                // given
                val user = userService.save(User("User 1", "user@1.com"))

                // when
                val foundUser = userService.findById(user.id!!)

                // then
                assertNotNull(foundUser)
                assertEquals(user, foundUser)
            }

            cleanUp()
        }
    }
}
