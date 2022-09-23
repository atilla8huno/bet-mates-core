package app.betmates.core.db

import app.betmates.core.util.DatabaseProperties
import org.jetbrains.exposed.sql.Database

object DatabaseConnection {
    val database by lazy {
        Database.connect(
            url = DatabaseProperties.jdbcUrl,
            driver = DatabaseProperties.driver!!,
            user = DatabaseProperties.username!!,
            password = DatabaseProperties.password ?: ""
        )
    }
}
