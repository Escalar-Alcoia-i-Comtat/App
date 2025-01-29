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
    data class Area(val areaId: Long) : Destination {
        override val path: String = "/area/$areaId"
        override val name: String = "Area"
        override val id: Long = areaId
    }

    @Serializable
    data class Zone(val zoneId: Long) : Destination {
        override val path: String = "/zone/$zoneId"
        override val name: String = "Zone"
        override val id: Long = zoneId
    }

    @Serializable
    data class Sector(val sectorId: Long, val pathId: Long? = null) : Destination {
        override val path: String = "/sector/$sectorId${pathId?.let { "?path=$it" } ?: ""}"
        override val name: String = "Sector"
        override val id: Long = sectorId
    }
}
