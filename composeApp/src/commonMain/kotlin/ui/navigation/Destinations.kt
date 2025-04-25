package ui.navigation

import data.DataTypes
import kotlinx.serialization.Serializable

object Destinations {
    @Serializable
    data object Root : Destination() {
        override val name: String = "Root"
        override val id: Long? = null
    }

    @Serializable
    data object Intro : Destination() {
        override val name: String = "Intro"
        override val id: Long? = null
    }

    @Serializable
    data class Area(
        val areaId: Long
    ) : Destination() {
        override val name: String = "Area"
        override val id: Long = areaId

        fun up(): Root = Root
        fun down(zoneId: Long) = Zone(areaId, zoneId)

        override fun toString(): String {
            return "$areaId"
        }
    }

    @Serializable
    data class Zone(
        val parentAreaId: Long,
        val zoneId: Long
    ) : Destination() {
        override val name: String = "Zone"
        override val id: Long = zoneId

        fun up(): Area = Area(parentAreaId)
        fun down(sectorId: Long) = Sector(parentAreaId, zoneId, sectorId)

        override fun toString(): String {
            return "${parentAreaId}/$zoneId"
        }
    }

    @Serializable
    data class Sector(
        val parentAreaId: Long,
        val parentZoneId: Long,
        val sectorId: Long,
        val pathId: Long? = null
    ) : Destination() {
        override val name: String = "Sector"
        override val id: Long = sectorId

        fun up(): Zone = Zone(parentAreaId, parentZoneId)

        override fun toString(): String {
            return "${parentAreaId}/${parentZoneId}/$sectorId${pathId?.let { "/$it" } ?: ""}"
        }
    }

    @Serializable
    data class Editor(
        val dataTypes: String,
        override val id: Long?,
        val parentId: Long?,
    ) : Destination() {
        constructor(dataTypes: DataTypes<*>, id: Long?, parentId: Long? = null) : this(dataTypes.name, id, parentId)

        override val name: String = "Editor"

        override fun toString(): String {
            return "${name.lowercase()}/${dataTypes}/${id ?: "new"}/${parentId ?: "null"}"
        }
    }

    fun parse(path: String): Destination {
        val pieces = path.removePrefix("#").split('/').filter(String::isNotEmpty)
        return when {
            pieces.isEmpty() -> Root

            pieces[0] == Root.name.lowercase() -> Root
            pieces[0] == Intro.name.lowercase() -> Intro
            pieces[0] == "editor" -> Editor(pieces[1], pieces[2].toLongOrNull(), pieces.getOrNull(3)?.toLongOrNull())

            pieces.size == 1 -> Area(pieces[0].toLong())
            pieces.size == 2 -> Zone(pieces[0].toLong(), pieces[1].toLong())
            pieces.size in (3..4) -> Sector(
                pieces[0].toLong(),
                pieces[1].toLong(),
                pieces[2].toLong(),
                pieces.getOrNull(3)?.toLong()
            )

            else -> throw IllegalArgumentException("Invalid path: $path")
        }
    }
}
