package database

import data.Area
import data.Path
import data.Sector
import data.Zone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

actual object DatabaseInterface {
    init {
        CoroutineScope(Dispatchers.Default).launch { Database.open() }
    }

    actual fun areas(): DataTypeInterface<Area> = areasInterface

    actual fun zones(): DataTypeInterface<Zone> = zonesInterface

    actual fun sectors(): DataTypeInterface<Sector> = sectorsInterface

    actual fun paths(): DataTypeInterface<Path> = pathsInterface
}
