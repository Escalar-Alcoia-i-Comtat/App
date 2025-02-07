import cache.StorageProvider
import cache.storageProvider
import database.setRoomDatabaseBuilder
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun debugBuild() {
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    // Initialize the database
    setRoomDatabaseBuilder()
}
