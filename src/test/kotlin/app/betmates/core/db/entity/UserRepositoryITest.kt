package app.betmates.core.db.entity

import app.betmates.core.db.RepositoryTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserRepositoryITest : RepositoryTest() {

    @Test
    fun `should save and find a user in the database`() = runTest {
        newSuspendedTransaction(Dispatchers.Default, db = db) {
            setUp()

            val userById = suspendedTransactionAsync {
                // given
                val userId = UserRepository.new {
                    name = "Johnny Doe"
                    email = "Johnny.doe@google.com"
                }.id

                // when
                UserRepository.findById(userId)
            }

            // then
            val actual = userById.await()
            assertNotNull(actual)
            assertEquals("Johnny Doe", actual.name)
            assertEquals("Johnny.doe@google.com", actual.email)

            cleanUp()
        }
    }
}
