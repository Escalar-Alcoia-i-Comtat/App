package ui.navigation

import kotlinx.serialization.Serializable

object Destinations {
    @Serializable
    data object Root : Destination

    @Serializable
    data object Intro : Destination

    @Serializable
    data class Area(val areaId: Long) : Destination

    @Serializable
    data class Zone(val zoneId: Long) : Destination

    @Serializable
    data class Sector(val sectorId: Long, val pathId: Long? = null) : Destination
}
