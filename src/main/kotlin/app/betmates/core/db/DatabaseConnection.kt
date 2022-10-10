package app.betmates.core.db

import app.betmates.core.util.DatabaseProperties
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseConnection {
    val database by lazy {
        Database.connect(getDataSource())
    }
}

fun initDatabase() {
    val flyway = Flyway
        .configure()
        .baselineOnMigrate(true)
        .dataSource(getDataSource())
        .load()

    flyway.migrate()
}

private fun getDataSource(): HikariDataSource {
    val hikariConfig = HikariConfig()
    hikariConfig.jdbcUrl = DatabaseProperties.jdbcUrl
    hikariConfig.driverClassName = DatabaseProperties.driver
    hikariConfig.username = DatabaseProperties.username
    hikariConfig.password = DatabaseProperties.password
    hikariConfig.addDataSourceProperty("dataSource.database", DatabaseProperties.database)

    return HikariDataSource(hikariConfig)
}
