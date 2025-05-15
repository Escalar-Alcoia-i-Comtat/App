package org.escalaralcoiaicomtat.app.database.migrations

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

@RenameColumn(tableName = "PathEntity", fromColumnName = "gradeValue", toColumnName = "grade")
class Migration3To4 : AutoMigrationSpec
