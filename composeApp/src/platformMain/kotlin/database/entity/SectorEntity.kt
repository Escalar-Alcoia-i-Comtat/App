package database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import data.Sector
import data.generic.ExternalTrack
import data.generic.LatLng
import data.generic.SunTime
import database.appDatabase
import kotlinx.datetime.Instant

@Entity(
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
    val image: String,
    val gpx: String?,
    val tracks: List<ExternalTrack>?,
    val kidsApt: Boolean,
    val weight: String,
    val walkingTime: Long?,
    val point: LatLng?,
    val sunTime: SunTime,
    val parentZoneId: Long,
) : DatabaseEntity<Sector> {
    suspend fun paths(): List<PathEntity> = appDatabase.paths().findBySectorId(id)

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
        paths().map { it.convert() }
    )
}
