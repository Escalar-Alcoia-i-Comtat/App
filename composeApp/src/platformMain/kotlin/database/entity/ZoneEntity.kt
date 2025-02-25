package database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import data.Zone
import data.generic.LatLng
import data.generic.Point
import database.appDatabase
import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

@Entity(
    indices = [Index("parentAreaId", unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = AreaEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentAreaId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class ZoneEntity(
    @PrimaryKey override val id: Long,
    override val timestamp: Instant,
    val displayName: String,
    val image: Uuid,
    val kmzUUID: Uuid,
    val point: LatLng?,
    val points: List<Point>,
    val parentAreaId: Long
) : DatabaseEntity<Zone> {
    suspend fun sectors(): List<SectorEntity> = appDatabase.sectors().findByZoneId(id)

    override suspend fun convert(): Zone = Zone(
        id,
        timestamp.toEpochMilliseconds(),
        displayName,
        image,
        kmzUUID,
        point,
        points,
        parentAreaId,
        sectors().map { it.convert() }
    )
}
