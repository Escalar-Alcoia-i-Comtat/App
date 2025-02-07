package database

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

expect val driver: SQLiteDriver

lateinit var roomDatabaseBuilder: RoomDatabase.Builder<AppDatabase>

private fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase> = roomDatabaseBuilder
): AppDatabase {
    return builder
        .addMigrations()
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(driver)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

val appDatabase by lazy { getRoomDatabase() }
