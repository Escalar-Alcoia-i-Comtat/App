package database

import data.Area
import data.Path
import data.Sector
import data.Zone

val areasInterface = DatabaseDataTypeInterface("area", Area.serializer())
val zonesInterface = DatabaseDataTypeInterface("zone", Zone.serializer(), parentKey = "area_id")
val sectorsInterface = DatabaseDataTypeInterface("sector", Sector.serializer(), parentKey = "zone_id")
val pathsInterface = DatabaseDataTypeInterface("path", Path.serializer(), parentKey = "sector_id")

val interfaces = listOf(areasInterface, zonesInterface, sectorsInterface, pathsInterface)
