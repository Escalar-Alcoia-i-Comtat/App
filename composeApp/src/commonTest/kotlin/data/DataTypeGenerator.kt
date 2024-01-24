package data

import data.generic.Builder
import data.generic.Ending
import data.generic.LatLng
import data.generic.PitchInfo
import data.generic.Point

/**
 * Allows generating [DataType]s without the need of specifying each argument.
 * Provides default values for all of them.
 */
object DataTypeGenerator {
    fun newArea(
        id: Long = 0L,
        timestamp: Long = 0L,
        displayName: String = "",
        image: String = "",
        webUrl: String = "",
        zones: List<Zone> = emptyList()
    ): Area = Area(id, timestamp, displayName, image, webUrl, zones)

    fun newZone(
        id: Long = 0L,
        timestamp: Long = 0L,
        displayName: String = "",
        image: String = "",
        webUrl: String = "",
        kmzUUID: String = "",
        point: LatLng? = null,
        points: List<Point> = emptyList(),
        parentAreaId: Long = 0L,
        sectors: List<Sector> = emptyList()
    ): Zone = Zone(
        id,
        timestamp,
        displayName,
        image,
        webUrl,
        kmzUUID,
        point,
        points,
        parentAreaId,
        sectors
    )

    fun newSector(
        id: Long = 0L,
        timestamp: Long = 0L,
        displayName: String = "",
        image: String = "",
        kidsApt: Boolean = false,
        weight: String = "zzz",
        walkingTime: Long? = null,
        point: LatLng? = null,
        sunTime: String? = null,
        parentZoneId: Long = 0L,
        paths: List<Path> = emptyList()
    ): Sector = Sector(
        id,
        timestamp,
        displayName,
        image,
        kidsApt,
        weight,
        walkingTime,
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
        grade: String? = null,
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
        images: List<String>? = null,
        parentSectorId: Long = 0L
    ): Path = Path(
        id,
        timestamp,
        displayName,
        sketchId,
        height,
        grade,
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
