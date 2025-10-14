import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.database.getDatabaseBuilder
import org.escalaralcoiaicomtat.app.database.roomDatabaseBuilder

actual open class PlatformTestSuite {
    actual open suspend fun before() {
        storageProvider = StorageProvider()
        roomDatabaseBuilder = getDatabaseBuilder()
    }

    actual open suspend fun after() {
    }
}
