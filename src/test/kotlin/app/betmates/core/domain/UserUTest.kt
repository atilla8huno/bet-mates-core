package app.betmates.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class UserUTest {

    @Test
    fun `should create User instance`() {
        // given
        val name = "John Doe"
        val email = "John.Doe@gmail.com"

        // when
        val user = User(
            name = name,
            email = email
        )

        // then
        assertNotNull(user)
        assertEquals(name, user.name)
        assertEquals(email, user.email)
        assertTrue { email.split("@")[0] == user.username }
        assertTrue { user.isActive() }
    }

    @Test
    fun `should deactivate user`() {
        // given
        val user = User(
            email = "email@mail.com"
        )

        // when
        user.deactivate()

        // then
        assertFalse { user.isActive() }
    }

    @Test
    fun `should accept password and encrypt it into SHA3-256 hash`() {
        // given
        val password = "I51tS3c4r3?"
        val expectedHash = "2190797d646271ef99ab9077d737ace1aec386586a602da8753a9dcdea2eba2e"
        val user = User(
            email = "email@mail.com"
        )
        assertNull(user.encryptedPassword)

        // when
        user.acceptPassword(password)

        // then
        assertEquals(expectedHash, user.encryptedPassword!!)
    }

    @Test
    fun `should two instances of user with same email be equal`() {
        // given
        val user1 = User(
            email = "email@mail.com"
        )
        val user2 = User(
            email = "email@mail.com"
        )
        val user3 = User(
            email = "another@one.com"
        )

        // when
        val user1EqualsToUser2 = user1 == user2
        val user1EqualsToUser3 = user1 == user3
        val user2EqualsToUser1 = user2 == user1
        val user2EqualsToUser3 = user2 == user3

        // then
        assertTrue { user1EqualsToUser2 && user2EqualsToUser1 }
        assertFalse { user1EqualsToUser3 && user2EqualsToUser3 }
    }
}
