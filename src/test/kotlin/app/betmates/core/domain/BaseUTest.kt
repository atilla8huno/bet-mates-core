package app.betmates.core.domain

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class BaseUTest {

    @Test
    fun `instance of domain should accept ID`() {
        // given
        val domain: Base = Player("CR7")
        assertNull(domain.id)

        // when
        domain.id = 10L

        // then
        assertNotNull(domain.id)
    }

    @Test
    fun `two instances with the same ID should be equal`() {
        // given
        val domain1: Base = Player("CR7 do Real Madrid").apply { id = 1L }
        val domain2: Base = Player("CR7 de Portugal").apply { id = 1L }

        // when
        val isEqual1 = domain1.equals(domain2)
        val isEqual2 = domain2.equals(domain1)

        // then
        assertTrue { isEqual1 }
        assertTrue { isEqual2 }
    }
}
