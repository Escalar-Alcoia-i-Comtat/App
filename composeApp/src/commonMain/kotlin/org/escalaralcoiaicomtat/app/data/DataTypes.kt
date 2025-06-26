package org.escalaralcoiaicomtat.app.data

import org.escalaralcoiaicomtat.app.data.generic.SunTime
import kotlin.time.Clock
import kotlin.uuid.Uuid

sealed class DataTypes<out DT : DataType>(
    val path: String,
    val parentDataType: DataTypes<*>?,
) {
    val name: String get() = this::class.simpleName ?: error("Class doesn't have a valid name")

    abstract fun default(): DT

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is String) return name == other
        if (other == null || this::class != other::class) return false

        other as DataTypes<*>

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }

    data object Area : DataTypes<org.escalaralcoiaicomtat.app.data.Area>("area", null) {
        override fun default(): org.escalaralcoiaicomtat.app.data.Area = Area(
            id = 0,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            displayName = "",
            image = Uuid.random(),
        )
    }

    data object Zone : DataTypes<org.escalaralcoiaicomtat.app.data.Zone>("zone", Area) {
        override fun default(): org.escalaralcoiaicomtat.app.data.Zone = Zone(
            id = 0,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            displayName = "",
            image = null,
            kmz = null,
            point = null,
            points = emptyList(),
            parentAreaId = 0,
        )
    }

    data object Sector : DataTypes<org.escalaralcoiaicomtat.app.data.Sector>("sector", Zone) {
        override fun default(): org.escalaralcoiaicomtat.app.data.Sector = Sector(
            id = 0,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            displayName = "",
            image = null,
            gpx = null,
            tracks = null,
            kidsApt = false,
            weight = "",
            walkingTime = null,
            point = null,
            sunTime = SunTime.None,
            parentZoneId = 0,
        )
    }

    data object Path : DataTypes<org.escalaralcoiaicomtat.app.data.Path>("path", Sector) {
        override fun default(): org.escalaralcoiaicomtat.app.data.Path = Path(
            id = 0,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            displayName = "",
            sketchId = 0u,
            height = null,
            grade = null,
            ending = null,
            pitches = null,
            stringCount = null,
            paraboltCount = null,
            burilCount = null,
            pitonCount = null,
            spitCount = null,
            tensorCount = null,
            nutRequired = false,
            friendRequired = false,
            lanyardRequired = false,
            nailRequired = false,
            pitonRequired = false,
            stapesRequired = false,
            showDescription = false,
            description = null,
            builder = null,
            reBuilders = null,
            images = null,
            parentSectorId = 0,
        )
    }

    companion object {
        private val entries get() = sequenceOf(Area, Zone, Sector, Path)

        fun findByName(name: String): DataTypes<DataType>? = entries.find { it.name == name }

        fun valueOf(name: String): DataTypes<DataType> =
            entries.find { it.name == name }
                ?: throw IllegalArgumentException("Unknown data type: $name")
    }
}
