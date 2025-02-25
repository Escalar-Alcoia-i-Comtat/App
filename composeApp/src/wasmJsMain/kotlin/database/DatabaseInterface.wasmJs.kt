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

    actual fun areas(): DataTypeInterface<Area> = DatabaseDataTypeInterface("area", Area.serializer())

    actual fun zones(): DataTypeInterface<Zone> = DatabaseDataTypeInterface("zone", Zone.serializer())

    actual fun sectors(): DataTypeInterface<Sector> = DatabaseDataTypeInterface("sector", Sector.serializer())

    actual fun paths(): DataTypeInterface<Path> = DatabaseDataTypeInterface("path", Path.serializer())
}
