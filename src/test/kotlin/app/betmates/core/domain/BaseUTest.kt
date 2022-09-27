package app.betmates.core.domain

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class BaseUTest {

    @Test
    fun `instance of domain should accept ID`() {
        // given
        val domain: Base = FootballTeam("A")
        assertNull(domain.id)

        // when
        domain.id = 10L

        // then
        assertNotNull(domain.id)
    }

    @Test
    fun `two instances with the same ID should be equal`() {
        // given
        val domain1: Base = FootballTeam("Real Madrid").apply { id = 1L }
        val domain2: Base = FootballTeam("Portugal").apply { id = 1L }

        // when
        val isEqual1 = domain1 == domain2
        val isEqual2 = domain2 == domain1

        // then
        assertTrue { isEqual1 }
        assertTrue { isEqual2 }
    }
}
