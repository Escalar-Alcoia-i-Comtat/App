package database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import cache.storageProvider
import io.github.aakira.napier.Napier
import java.util.Properties

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        Napier.v { "Creating cache directory..." }
        val cacheDirectory = storageProvider.cacheDirectory
        if (!cacheDirectory.exists()) cacheDirectory.mkdirs()

        val file = cacheDirectory + DATABASE_FILE_NAME
        val alreadyExists = file.exists()

        Napier.d { "Creating database into $file..." }
        val driver: SqlDriver = JdbcSqliteDriver(
            url = "jdbc:sqlite:$file",
            properties = Properties().apply {
                put("foreign_keys", "true")
            }
        )
        if (!alreadyExists) {
            Napier.i { "Database doesn't exist, creating now..." }
            Database.Schema.create(driver)
        }
        return driver
    }
}
