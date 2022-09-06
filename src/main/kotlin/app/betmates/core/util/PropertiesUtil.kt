package app.betmates.core.util

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.config.tryGetString

sealed class PropertiesUtil {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    fun getProperty(key: String): String? = config.tryGetString(key)
}

object DatabaseProperties : PropertiesUtil() {
    val username = getProperty("ktor.db.username")
    val password = getProperty("ktor.db.password")
    val host = getProperty("ktor.db.host")
    val port = getProperty("ktor.db.port")
    val database = getProperty("ktor.db.database")
    val options = getProperty("ktor.db.options")
    val driver = getProperty("ktor.db.driver")

    val jdbcUrl = "jdbc:postgresql://$host:$port/$database?options=$options"
}
