import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.database.setRoomDatabaseBuilder

actual open class PlatformTestSuite actual constructor() {
    actual open suspend fun before() {
        storageProvider = StorageProvider()

        // Initialize the database
        setRoomDatabaseBuilder()
    }

    actual open suspend fun after() {
    }
}
