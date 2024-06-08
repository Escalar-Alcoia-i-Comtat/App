package ui.navigation

object Routes {
    const val ROOT = "/"

    const val INTRO = "/intro"

    const val ZONES = "/area/{areaId}"

    const val SECTORS = "/zone/{zoneId}"

    const val PATHS = "/sector/{sectorId}?pathId={pathId}"

    fun area(areaId: Long): String = ZONES.replace("{areaId}", "$areaId")

    fun zone(zoneId: Long): String = SECTORS.replace("{zoneId}", "$zoneId")

    fun sector(sectorId: Long, pathId: Long? = null): String = PATHS
        .replace("{sectorId}", "$sectorId")
        .replace("{pathId}", "${pathId ?: -1}")
}
