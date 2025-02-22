package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import data.Area
import database.appDatabase
import kotlinx.datetime.Instant

@Entity
data class AreaEntity(
    @PrimaryKey override val id: Long,
    override val timestamp: Instant,
    val displayName: String,
    val image: String,
    val webUrl: String
) : DatabaseEntity<Area> {
    suspend fun zones(): List<ZoneEntity> = appDatabase.zones().findByAreaId(id)

    override suspend fun convert(): Area = Area(
        id,
        timestamp.toEpochMilliseconds(),
        displayName,
        image,
        webUrl,
        zones().map { it.convert() }
    )
}
