import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.database.getDatabaseBuilder
import org.escalaralcoiaicomtat.app.database.roomDatabaseBuilder

actual open class PlatformTestSuite actual constructor() {
    lateinit var instrumentationContext: Context
    lateinit var applicationContext: Context

    actual open suspend fun before() {
        try {
            mockkStatic(Log::println)
            every { Log.println(any(), any(), any()) } answers {
                val priority: Int? = arg(0)
                val msg: String? = arg(1)
                val tr: Throwable? = arg(2)
                println("$priority: $msg")
                tr?.printStackTrace()
                0
            }

            val instrumentation = InstrumentationRegistry.getInstrumentation()
            instrumentationContext = instrumentation.context
            applicationContext = instrumentation.targetContext

            // Initialize the storage provider
            storageProvider = StorageProvider(applicationContext)

            // Initialize the Room Database Builder
            roomDatabaseBuilder = getDatabaseBuilder(applicationContext)
        } catch (e: IllegalStateException) {
            throw UnsupportedOperationException("Instrumented tests are not supported on this platform.", e)
        }
    }

    actual open suspend fun after() {
        unmockkStatic(Log::println)
    }
}
