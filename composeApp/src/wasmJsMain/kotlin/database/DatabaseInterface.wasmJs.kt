package database

import data.Area
import data.Path
import data.Sector
import data.Zone

actual object DatabaseInterface {
    actual fun areas(): DataTypeInterface<Area> = LocalStorageDataTypeInterface("area", Area.serializer())

    actual fun zones(): DataTypeInterface<Zone> = LocalStorageDataTypeInterface("zone", Zone.serializer())

    actual fun sectors(): DataTypeInterface<Sector> = LocalStorageDataTypeInterface("sector", Sector.serializer())

    actual fun paths(): DataTypeInterface<Path> = LocalStorageDataTypeInterface("path", Path.serializer())
}
