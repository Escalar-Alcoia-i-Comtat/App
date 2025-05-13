package org.escalaralcoiaicomtat.app.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.escalaralcoiaicomtat.app.database.dao.AreasDao
import org.escalaralcoiaicomtat.app.database.dao.BlockingDao
import org.escalaralcoiaicomtat.app.database.dao.PathsDao
import org.escalaralcoiaicomtat.app.database.dao.SectorsDao
import org.escalaralcoiaicomtat.app.database.dao.ZonesDao
import org.escalaralcoiaicomtat.app.database.entity.AreaEntity
import org.escalaralcoiaicomtat.app.database.entity.BlockingEntity
import org.escalaralcoiaicomtat.app.database.entity.PathEntity
import org.escalaralcoiaicomtat.app.database.entity.SectorEntity
import org.escalaralcoiaicomtat.app.database.entity.ZoneEntity
import org.escalaralcoiaicomtat.app.database.migrations.Migration1To2

@Database(
    exportSchema = true,
    entities = [
        AreaEntity::class,
        ZoneEntity::class,
        SectorEntity::class,
        PathEntity::class,
        BlockingEntity::class,
    ],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = Migration1To2::class),
        AutoMigration(from = 2, to = 3),
    ]
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun areas(): AreasDao

    abstract fun zones(): ZonesDao

    abstract fun sectors(): SectorsDao

    abstract fun paths(): PathsDao

    abstract fun blocking(): BlockingDao
}
