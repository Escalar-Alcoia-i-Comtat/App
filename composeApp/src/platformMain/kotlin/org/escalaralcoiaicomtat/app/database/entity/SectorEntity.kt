package org.escalaralcoiaicomtat.app.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.generic.ExternalTrack
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.data.generic.SunTime
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    indices = [Index("parentZoneId", unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = ZoneEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentZoneId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class SectorEntity(
    @PrimaryKey override val id: Long,
    override val timestamp: Instant,
    val displayName: String,
    val image: Uuid,
    val gpx: Uuid?,
    val tracks: List<ExternalTrack>?,
    val kidsApt: Boolean,
    val weight: String,
    val walkingTime: Long?,
    val point: LatLng?,
    val sunTime: SunTime,
    val parentZoneId: Long,
) : DatabaseEntity<Sector> {
    override suspend fun convert(): Sector = Sector(
        id,
        timestamp.toEpochMilliseconds(),
        displayName,
        image,
        gpx,
        tracks,
        kidsApt,
        weight,
        walkingTime,
        point,
        sunTime,
        parentZoneId,
    )
}
