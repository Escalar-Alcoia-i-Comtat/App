package org.escalaralcoiaicomtat.app.database

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.escalaralcoiaicomtat.app.database.migrations.Migration4To5
import org.escalaralcoiaicomtat.app.database.migrations.Migration5To6

expect val driver: SQLiteDriver

lateinit var roomDatabaseBuilder: RoomDatabase.Builder<AppDatabase>

private fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase> = roomDatabaseBuilder
): AppDatabase {
    return builder
        .addMigrations(Migration4To5, Migration5To6)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(driver)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

val appDatabase by lazy { getRoomDatabase() }
