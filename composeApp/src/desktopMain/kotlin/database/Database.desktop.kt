package database

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual val driver: SQLiteDriver = BundledSQLiteDriver()
