package org.escalaralcoiaicomtat.app.database.migrations

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn(tableName = "AreaEntity", columnName = "webUrl")
@DeleteColumn(tableName = "ZoneEntity", columnName = "webUrl")
class Migration1To2 : AutoMigrationSpec
