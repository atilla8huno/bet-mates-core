package app.betmates.core.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class UserDAOITest : DbTestUtil() {

    @Test
    fun `should save and find a user in the database`() {
        Database.connect(url = "jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root")
        transaction {
            // given
            setUp()

            val userId = UserDAO.new {
                name = "Johnny Doe"
                email = "Johnny.doe@google.com"
            }.id

            // when
            val theUser = UserDAO.findById(userId)

            // then
            assertNotNull(theUser)
            assertEquals(userId.value, theUser.id.value)

            cleanUp()
        }
    }
}
