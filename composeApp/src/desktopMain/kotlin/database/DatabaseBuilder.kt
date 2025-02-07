package database

import androidx.room.Room
import androidx.room.RoomDatabase
import cache.File
import cache.storageProvider

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(storageProvider.cacheDirectory, "escalaralcoiaicomtat.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.path,
    )
}
