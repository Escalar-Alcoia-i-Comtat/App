package org.escalaralcoiaicomtat.app.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

object Migration5To6 : Migration(5, 6) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE SectorEntity ADD COLUMN phoneSignalAvailability TEXT")
    }
}
