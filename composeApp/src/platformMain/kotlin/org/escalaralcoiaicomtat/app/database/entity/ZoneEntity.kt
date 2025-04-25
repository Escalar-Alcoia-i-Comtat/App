package org.escalaralcoiaicomtat.app.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.data.generic.Point
import org.escalaralcoiaicomtat.app.database.appDatabase
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
