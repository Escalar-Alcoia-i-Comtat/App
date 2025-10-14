expect open class PlatformTestSuite() {
    /**
     * Called before each test.
     *
     * Throw [UnsupportedOperationException] if the platform doesn't support running instrumented tests.
     */
    open suspend fun before()

    open suspend fun after()
}
