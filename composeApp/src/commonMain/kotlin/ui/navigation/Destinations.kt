package ui.navigation

import kotlinx.serialization.Serializable

object Destinations {
    @Serializable
    data object Root : Destination {
        override val path: String = "/"
        override val name: String = "Root"
        override val id: Long? = null
    }

    @Serializable
    data object Intro : Destination {
        override val path: String = "/intro"
        override val name: String = "Intro"
        override val id: Long? = null
    }

    @Serializable
    data class Area(
        val areaId: Long
    ) : Destination {
        override val path: String = "/$areaId"
        override val name: String = "Area"
        override val id: Long = areaId

        fun up(): Root = Root
        fun down(zoneId: Long) = Zone(areaId, zoneId)
    }

    @Serializable
    data class Zone(
        val parentAreaId: Long,
        val zoneId: Long
    ) : Destination {
        override val path: String = "/$parentAreaId/$zoneId"
        override val name: String = "Zone"
        override val id: Long = zoneId

        fun up(): Area = Area(parentAreaId)
        fun down(sectorId: Long) = Sector(parentAreaId, zoneId, sectorId)
    }

    @Serializable
    data class Sector(
        val parentAreaId: Long,
        val parentZoneId: Long,
        val sectorId: Long,
        val pathId: Long? = null
    ) : Destination {
        override val path: String = "/$parentAreaId/$parentZoneId/$sectorId${pathId?.let { "?path=$it" } ?: ""}"
        override val name: String = "Sector"
        override val id: Long = sectorId

        fun up(): Zone = Zone(parentAreaId, parentZoneId)
    }
}
