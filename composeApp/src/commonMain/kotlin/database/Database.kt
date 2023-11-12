package database

import io.github.aakira.napier.Napier

const val DATABASE_FILE_NAME = "database.db"

/**
 * Stores the initialized instance for accessing the local database.
 * The specific platform must initialize it with their own [DriverFactory] and running
 * [createDatabase].
 *
 * @throws UninitializedPropertyAccessException If it has not been initialized yet.
 */
lateinit var database: Database
    private set

/**
 * Creates the local database using the given [driverFactory], and stores it automatically into
 * [database].
 */
fun createDatabase(driverFactory: DriverFactory): Database {
    Napier.d { "Creating database driver..." }
    val driver = driverFactory.createDriver()

    Napier.d { "Creating database..." }
    return Database(driver).also {
        Napier.i { "Created database." }
        database = it
    }
}
