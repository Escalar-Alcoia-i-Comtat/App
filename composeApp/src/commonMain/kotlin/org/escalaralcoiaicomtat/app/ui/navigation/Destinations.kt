package org.escalaralcoiaicomtat.app.ui.navigation

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.DataTypes
import kotlin.uuid.Uuid

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
    data class Report(
        val sectorId: Long? = null,
        val pathId: Long? = null,
    ): Destination() {
        override val name: String = "Report"
        override val id: Long? = sectorId ?: pathId

        override fun toString(): String {
            return "report" + when {
                sectorId != null -> "/sectors/$sectorId"
                pathId != null -> "/paths/$pathId"
                else -> ""
            }
        }

        fun isNull(): Boolean = sectorId == null && pathId == null
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

    @Serializable
    data class Map(private val _kmz: String? = null) : Destination() {
        constructor(kmz: Uuid? = null): this(kmz?.toString())

        override val id: Long? = null
        override val name: String = "Map"

        val kmz: Uuid? get() = _kmz?.let { Uuid.parse(it) }

        override fun toString(): String {
            return "map${_kmz?.let { "/$it" } ?: ""}"
        }
    }

    fun parse(path: String): Destination {
        val pieces = path.removePrefix("#").split('/').filter(String::isNotEmpty)
        return when {
            pieces.isEmpty() -> Root

            pieces[0] == Root.name.lowercase() -> Root
            pieces[0] == Intro.name.lowercase() -> Intro
            pieces[0] == "editor" -> Editor(pieces[1], pieces[2].toLongOrNull(), pieces.getOrNull(3)?.toLongOrNull())
            pieces[0] == "map" -> Map(pieces.getOrNull(1)?.let { Uuid.parse(it) })
            pieces[0] == "report" -> Report(
                sectorId = pieces.getOrNull(2)?.toLongOrNull().takeIf { pieces.getOrNull(1) == "sectors" },
                pathId = pieces.getOrNull(2)?.toLongOrNull().takeIf { pieces.getOrNull(1) == "paths" },
            )

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
