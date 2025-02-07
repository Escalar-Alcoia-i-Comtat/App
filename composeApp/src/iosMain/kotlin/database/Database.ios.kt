package database

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.NativeSQLiteDriver

actual val driver: SQLiteDriver = NativeSQLiteDriver()

fun setRoomDatabaseBuilder() {
    roomDatabaseBuilder = getDatabaseBuilder()
}
