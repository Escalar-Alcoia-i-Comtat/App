package database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import database.dao.AreasDao
import database.dao.PathsDao
import database.dao.SectorsDao
import database.dao.ZonesDao
import database.entity.AreaEntity
import database.entity.PathEntity
import database.entity.SectorEntity
import database.entity.ZoneEntity

@Database(
    entities = [AreaEntity::class, ZoneEntity::class, SectorEntity::class, PathEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun areas(): AreasDao

    abstract fun zones(): ZonesDao

    abstract fun sectors(): SectorsDao

    abstract fun paths(): PathsDao
}
