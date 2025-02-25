package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import data.Area
import database.appDatabase
import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

@Entity
data class AreaEntity(
    @PrimaryKey override val id: Long,
    override val timestamp: Instant,
    val displayName: String,
    val image: Uuid,
) : DatabaseEntity<Area> {
    suspend fun zones(): List<ZoneEntity> = appDatabase.zones().findByAreaId(id)

    override suspend fun convert(): Area = Area(
        id,
        timestamp.toEpochMilliseconds(),
        displayName,
        image,
        zones().map { it.convert() }
    )
}
