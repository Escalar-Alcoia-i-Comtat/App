import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.test.runTest

open class TestSuite : PlatformTestSuite() {
    override suspend fun before() {
        super.before()

        // Initialize the logging library
        Napier.base(DebugAntilog())
    }

    fun test(block: suspend () -> Unit) {
        runTest {
            try {
                before()
                block()
            } catch (_: UnsupportedOperationException){
                Napier.w { "Skipping since this platform doesn't support instrumented tests." }
            } finally {
                after()
            }
        }
    }
}
