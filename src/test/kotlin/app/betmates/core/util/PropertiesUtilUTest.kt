package app.betmates.core.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class PropertiesUtilUTest {

    @Test
    fun `should have defined JDBC URL`() {
        // given
        val expectedJdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"

        // when
        val jdbcUrl = DatabaseProperties.jdbcUrl

        // then
        assertEquals(expectedJdbcUrl, jdbcUrl)
    }

    @Test
    fun `should read DatabaseProperties properties`() {
        // given DatabaseProperties properties
        // when access its fields
        // then should be defined in application.conf
        assertFalse { DatabaseProperties.username.isNullOrBlank() }
        assertFalse { DatabaseProperties.host.isNullOrBlank() }
        assertFalse { DatabaseProperties.port.isNullOrBlank() }
        assertFalse { DatabaseProperties.database.isNullOrBlank() }
        assertFalse { DatabaseProperties.options.isNullOrBlank() }
        assertFalse { DatabaseProperties.driver.isNullOrBlank() }
    }

    @Test
    fun `should read JwtProperties properties`() {
        // given JwtProperties properties
        // when access its fields
        // then should be defined in application.conf
        assertFalse { JwtProperties.secret.isNullOrBlank() }
        assertFalse { JwtProperties.issuer.isNullOrBlank() }
        assertFalse { JwtProperties.audience.isNullOrBlank() }
        assertFalse { JwtProperties.realm.isNullOrBlank() }
    }
}
