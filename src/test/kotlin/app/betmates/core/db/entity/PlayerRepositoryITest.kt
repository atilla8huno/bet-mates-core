package app.betmates.core.db.entity

import app.betmates.core.db.RepositoryTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
internal class PlayerRepositoryITest : RepositoryTest() {

    @Test
    fun `should save and find a player in the database`() = runTest {
        // given
        newSuspendedTransaction(Dispatchers.Default, db = db) {
            setUp()

            val newUser = suspendedTransaction {
                UserRepository.new {
                    name = "Johnny Doe"
                    email = "Johnny.doe@google.com"
                }
            }

            suspendedTransaction {
                PlayerRepository.new {
                    nickName = "John da Massa"
                    user = newUser
                }
            }

            suspendedTransaction {
                // when
                val foundPlayer = PlayerRepository.find { PlayerEntity.nickName eq "John da Massa" }.singleOrNull()

                // then
                assertNotNull(foundPlayer)
            }

            cleanUp()
        }
    }
}
