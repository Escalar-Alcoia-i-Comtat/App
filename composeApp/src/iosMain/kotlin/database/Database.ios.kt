package database

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.NativeSQLiteDriver
import org.escalaralcoiaicomtat.app.database.roomDatabaseBuilder

actual val driver: SQLiteDriver = NativeSQLiteDriver()

fun setRoomDatabaseBuilder() {
    roomDatabaseBuilder = getDatabaseBuilder()
}
