package app.betmates.core.util

import java.util.Properties

sealed class PropertyUtil(
    private val fileName: String
) {
    private val properties: Properties = Properties()

    init {
        val file = this::class.java.classLoader.getResourceAsStream(fileName)
        properties.load(file)
    }

    fun getProperty(key: String): String? = properties.getProperty(key)
}

class ConfigProperties : PropertyUtil("config.properties")

class DatabaseProperties : PropertyUtil("database.properties")
