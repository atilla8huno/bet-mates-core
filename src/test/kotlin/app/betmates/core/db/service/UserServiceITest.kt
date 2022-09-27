package app.betmates.core.db.service

import app.betmates.core.db.RepositoryTest
import app.betmates.core.db.entity.UserEntity
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.domain.User
import app.betmates.core.domain.toHex
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
internal class UserServiceITest : RepositoryTest() {

    private lateinit var userService: UserService

    @BeforeTest
    fun init() {
        userService = UserServiceImpl()
    }

    @Test
    override fun `should save the domain in the database`() = transaction {
        runTest {
            // given
            val user = User(
                name = "Alfa Romeo",
                email = "alfaromeo@alfaromeo.com"
            )
            assertNull(user.id)

            // when
            val userSaved = userService.saveOrUpdate(user)
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
    }

    @Test
    override fun `should map entity to domain`() = transaction {
        runTest {
            // given
            val user = userService.save(User("Zinedine Zidane", "zizou@rm.es"))
            val entity = UserEntity.findById(user.id!!)

            // when
            val domain = userService.mapToDomain(entity!!)

            // then
            assertNotNull(domain)
            assertEquals(user.name, domain.name)
            assertEquals(user.email, domain.email)
            assertEquals(user.username, domain.username)
            assertEquals(user.isActive(), domain.isActive())
        }
    }

    @Test
    override fun `should find all records in the database`() = transaction {
        runTest {
            // given
            val user1 = userService.save(User("User 1", "user1@1.com"))
            val user2 = userService.save(User("User 2", "user2@2.com"))
            val user3 = userService.save(User("User 3", "user3@3.com"))
            val user4 = userService.save(User("User 4", "user4@4.com"))

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
    }

    @Test
    override fun `should update the domain in the database`() = transaction {
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
            updatedUser = userService.saveOrUpdate(updatedUser)

            // then
            val foundUser = userService.findById(user.id!!)!!

            assertEquals(updatedUser, foundUser)
            assertNotEquals(user.name, foundUser.name)
            assertNotEquals(user.email, foundUser.email)
            assertNotEquals(user.username, foundUser.username)

            assertFalse { foundUser.isActive() }
        }
    }

    @Test
    override fun `should delete the domain in the database`() = transaction {
        runTest {
            // given
            val user = userService.save(User("User 1", "user@1.com"))
            assertNotNull(userService.findById(user.id!!))

            // when
            userService.delete(user)

            // then
            val foundUser = userService.findById(user.id!!)

            assertNull(foundUser)
        }
    }

    @Test
    override fun `should find a record in the database by id`() = transaction {
        runTest {
            // given
            val user = userService.save(User("User 1", "user@1.com"))

            // when
            val foundUser = userService.findById(user.id!!)

            // then
            assertNotNull(foundUser)
            assertEquals(user, foundUser)
        }
    }

    @Test
    fun `should find an user in the database by email`() = transaction {
        runTest {
            // given
            val email = "user@1.com"
            val user = userService.save(User("User 1", email))

            // when
            val foundUser = userService.findByEmail(email)

            // then
            assertNotNull(foundUser)
            assertEquals(user, foundUser)
        }
    }

    @Test
    fun `should find an user in the database by username and password`() = transaction {
        runTest {
            // given
            val username = "usercool"
            val password = "123456"
            val user = userService.save(
                User("User 1", "user@a.com", username).apply { acceptPassword(password) }
            )

            // when
            val foundUser = userService.findByUsernameAndPassword(username, encrypt(password))

            // then
            assertNotNull(foundUser)
            assertEquals(user, foundUser)
        }
    }

    @Test
    fun `should update the password of an user in the database`() = transaction {
        runTest {
            // given
            var user = User("User 1", "user@1.com")
            val oldPassword = "123abc"
            user.acceptPassword(oldPassword)

            user = userService.save(user)

            // when
            val newPassword = "Th1sI5M0r3S4c4r3!!!"
            user = userService.updatePassword(user, newPassword)!!

            // then
            assertEquals(encrypt(newPassword), user.encryptedPassword)
        }
    }

    private fun encrypt(password: String): String {
        val hashBytes: ByteArray = MessageDigest.getInstance("SHA3-256").digest(
            password.toByteArray(Charsets.UTF_8)
        )
        return hashBytes.toHex()
    }
}
