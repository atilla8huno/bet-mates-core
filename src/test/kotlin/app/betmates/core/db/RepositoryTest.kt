package app.betmates.core.db

import app.betmates.core.ITest
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
internal abstract class RepositoryTest : ITest() {

    abstract fun `should save the domain in the database`()
    abstract fun `should update the domain in the database`()
    abstract fun `should delete the domain in the database`()
    abstract fun `should delete the domain in the database by id`()
    abstract fun `should find a record in the database by id`()
    abstract fun `should find all records in the database`()
    abstract fun `should map entity to domain`()
}
