package database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import cache.Files.exists
import cache.Files.mkdirs
import cache.storageProvider
import java.util.Properties

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val cacheDirectory = storageProvider.cacheDirectory
        if (!cacheDirectory.exists()) cacheDirectory.mkdirs()

        val file = cacheDirectory + DATABASE_FILE_NAME
        val alreadyExists = file.exists()

        val driver: SqlDriver = JdbcSqliteDriver(
            url = "jdbc:sqlite:$file",
            properties = Properties().apply {
                put("foreign_keys", "true")
            }
        )
        if (!alreadyExists) {
            Database.Schema.create(driver)
        }
        return driver
    }
}
