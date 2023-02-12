package app.betmates.core.db

import app.betmates.core.ITest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
internal abstract class RepositoryTest : ITest() {

    abstract fun `should save the domain in the database`()
    abstract fun `should update the domain in the database`()
    abstract fun `should throw exception if entry is not found by id on update`()
    abstract fun `should delete the domain in the database`()
    abstract fun `should delete the domain in the database by id`()
    abstract fun `should throw exception if entry is not found by id on delete`()
    abstract fun `should find a record in the database by id`()
    abstract fun `should find all records in the database`()
    abstract fun `should find all records paginated in the database`()
    abstract fun `should map entity to domain`()
}
