package database

import database.adapter.SerializableAdapter
import database.adapter.UIntColumnAdapter
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
    return Database(
        driver = driver,
        ZoneAdapter = Zone.Adapter(
            pointAdapter = SerializableAdapter(),
            pointsAdapter = SerializableAdapter()
        ),
        SectorAdapter = Sector.Adapter(
            pointAdapter = SerializableAdapter()
        ),
        PathAdapter = Path.Adapter(
            endingAdapter = SerializableAdapter(),
            pitchesAdapter = SerializableAdapter(),
            builderAdapter = SerializableAdapter(),
            reBuildersAdapter = SerializableAdapter(),
            imagesAdapter = SerializableAdapter(),
            sketchIdAdapter = UIntColumnAdapter,
            heightAdapter = UIntColumnAdapter,
            stringCountAdapter = UIntColumnAdapter,
            paraboltCountAdapter = UIntColumnAdapter,
            burilCountAdapter = UIntColumnAdapter,
            pitonCountAdapter = UIntColumnAdapter,
            spitCountAdapter = UIntColumnAdapter,
            tensorCountAdapter = UIntColumnAdapter
        )
    ).also {
        Napier.i { "Created database." }
        database = it
    }
}
