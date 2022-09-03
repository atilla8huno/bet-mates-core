package app.betmates.core.util

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertFalse

internal class PropertyUtilUTest {

    @ParameterizedTest
    @MethodSource("configsToRead")
    fun <T : PropertyUtil> `should read Config properties`(
        reader: T,
        properties: List<String>
    ) {
        // given args
        properties.forEach {
            // then
            assertFalse {
                // when
                reader.getProperty(it).isNullOrBlank()
            }
        }
    }

    companion object {
        @JvmStatic
        private fun configsToRead(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(ConfigProperties(), listOf("dummy")),
                Arguments.of(DatabaseProperties(), listOf("db.username", "db.password"))
            )
        }
    }
}
