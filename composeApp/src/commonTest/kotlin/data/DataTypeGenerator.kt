package data

import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.data.generic.Builder
import org.escalaralcoiaicomtat.app.data.generic.Ending
import org.escalaralcoiaicomtat.app.data.generic.ExternalTrack
import org.escalaralcoiaicomtat.app.data.generic.GradeValue
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.data.generic.PhoneCarrier
import org.escalaralcoiaicomtat.app.data.generic.PhoneSignalAvailability
import org.escalaralcoiaicomtat.app.data.generic.PhoneSignalStrength
import org.escalaralcoiaicomtat.app.data.generic.PitchInfo
import org.escalaralcoiaicomtat.app.data.generic.Point
import org.escalaralcoiaicomtat.app.data.generic.SunTime
import kotlin.uuid.Uuid

/**
 * Allows generating [org.escalaralcoiaicomtat.app.data.DataType]s without the need of specifying each argument.
 * Provides default values for all of them.
 */
object DataTypeGenerator {
    fun newArea(
        id: Long = 0L,
        timestamp: Long = 0L,
        displayName: String = "",
        image: Uuid = Uuid.parse("ac69f4b2-d059-42d2-847b-5f09358d55f3"),
        zones: List<Zone> = emptyList()
    ): Area = Area(id, timestamp, displayName, image, zones)

    fun newZone(
        id: Long = 0L,
        timestamp: Long = 0L,
        displayName: String = "",
        image: Uuid = Uuid.parse("ac69f4b2-d059-42d2-847b-5f09358d55f3"),
        kmz: Uuid = Uuid.parse("ac69f4b2-d059-42d2-847b-5f09358d55f4"),
        point: LatLng? = null,
        points: List<Point> = emptyList(),
        parentAreaId: Long = 0L,
        sectors: List<Sector> = emptyList()
    ): Zone = Zone(
        id,
        timestamp,
        displayName,
        image,
        kmz,
        point,
        points,
        parentAreaId,
        sectors
    )

    fun newSector(
        id: Long = 0L,
        timestamp: Long = 0L,
        displayName: String = "",
        image: Uuid = Uuid.parse("ac69f4b2-d059-42d2-847b-5f09358d55f3"),
        gpx: Uuid = Uuid.parse("ac69f4b2-d059-42d2-847b-5f09358d55f4"),
        tracks: List<ExternalTrack> = emptyList(),
        kidsApt: Boolean = false,
        weight: String = "zzz",
        walkingTime: Long? = null,
        phoneSignalAvailability: List<PhoneSignalAvailability>? = null,
        point: LatLng? = null,
        sunTime: SunTime = SunTime.Morning,
        parentZoneId: Long = 0L,
        paths: List<Path> = emptyList()
    ): Sector = Sector(
        id,
        timestamp,
        displayName,
        image,
        gpx,
        tracks,
        kidsApt,
        weight,
        walkingTime,
        phoneSignalAvailability,
        point,
        sunTime,
        parentZoneId,
        paths
    )

    fun newPath(
        id: Long = 0L,
        timestamp: Long = 0L,
        displayName: String = "",
        sketchId: UInt = 0U,
        height: UInt? = null,
        grade: GradeValue? = null,
        aidGrade: GradeValue? = null,
        ending: Ending? = null,
        pitches: List<PitchInfo>? = null,
        stringCount: UInt? = null,
        paraboltCount: UInt? = null,
        burilCount: UInt? = null,
        pitonCount: UInt? = null,
        spitCount: UInt? = null,
        tensorCount: UInt? = null,
        nutRequired: Boolean = false,
        friendRequired: Boolean = false,
        lanyardRequired: Boolean = false,
        nailRequired: Boolean = false,
        pitonRequired: Boolean = false,
        stapesRequired: Boolean = false,
        showDescription: Boolean = false,
        description: String? = null,
        builder: Builder? = null,
        reBuilders: List<Builder>? = null,
        images: List<Uuid>? = null,
        parentSectorId: Long = 0L
    ): Path = Path(
        id,
        timestamp,
        displayName,
        sketchId,
        height,
        grade,
        aidGrade,
        ending,
        pitches,
        stringCount,
        paraboltCount,
        burilCount,
        pitonCount,
        spitCount,
        tensorCount,
        nutRequired,
        friendRequired,
        lanyardRequired,
        nailRequired,
        pitonRequired,
        stapesRequired,
        showDescription,
        description,
        builder,
        reBuilders,
        images,
        parentSectorId
    )
}
