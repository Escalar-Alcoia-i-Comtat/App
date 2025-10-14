import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider

actual open class PlatformTestSuite actual constructor() {
    actual open suspend fun before() {
        storageProvider = StorageProvider()
    }

    actual open suspend fun after() {
    }
}
