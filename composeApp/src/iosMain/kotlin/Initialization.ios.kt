import database.DriverFactory
import database.createDatabase
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun debugBuild() {
    Napier.base(DebugAntilog())

    // Initialize the database
    createDatabase(DriverFactory())
}
