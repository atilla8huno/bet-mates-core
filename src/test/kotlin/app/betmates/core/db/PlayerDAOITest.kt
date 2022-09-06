package app.betmates.core.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class PlayerDAOITest : DbTestUtil() {

    @Test
    fun `should save and find a player in the database`() {
        Database.connect(url = "jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root")
        transaction {
            // given
            setUp()

            val theUser = UserDAO.new {
                name = "Johnny Doe"
                email = "Johnny.doe@google.com"
            }
            val playerId = PlayerDAO.new {
                nickName = "John da Massa"
                user = theUser
            }.id.value

            // when
            val thePlayer = PlayerDAO.findById(playerId)

            // then
            assertNotNull(thePlayer)
            assertEquals(playerId, thePlayer.id.value)

            cleanUp()
        }
    }
}
