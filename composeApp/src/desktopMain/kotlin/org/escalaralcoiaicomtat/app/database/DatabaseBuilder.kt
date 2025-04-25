package org.escalaralcoiaicomtat.app.database

import androidx.room.Room
import androidx.room.RoomDatabase
import org.escalaralcoiaicomtat.app.cache.File
import org.escalaralcoiaicomtat.app.cache.storageProvider

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(storageProvider.cacheDirectory, "escalaralcoiaicomtat.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.path,
    )
}
