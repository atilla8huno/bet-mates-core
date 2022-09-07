package app.betmates.core.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
internal class PlayerDAOITest : DAOTest() {

    @Test
    fun `should save and find a player in the database`() = runTest {
        // given
        newSuspendedTransaction(Dispatchers.Default, db = db) {
            setUp()

            val newUser = suspendedTransaction {
                UserDAO.new {
                    name = "Johnny Doe"
                    email = "Johnny.doe@google.com"
                }
            }

            suspendedTransaction {
                PlayerDAO.new {
                    nickName = "John da Massa"
                    user = newUser
                }
            }

            suspendedTransaction {
                // when
                val foundPlayer = PlayerDAO.find { PlayerEntity.nickName eq "John da Massa" }.singleOrNull()

                // then
                assertNotNull(foundPlayer)
            }

            cleanUp()
        }
    }
}
