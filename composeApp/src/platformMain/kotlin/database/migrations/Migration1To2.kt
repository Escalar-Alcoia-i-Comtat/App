package database.migrations

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn(tableName = "AreaEntity", columnName = "webUrl")
@DeleteColumn(tableName = "ZoneEntity", columnName = "webUrl")
interface Migration1To2 : AutoMigrationSpec
