package org.escalaralcoiaicomtat.app.database

import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone

val areasInterface = DatabaseDataTypeInterface("area", Area.serializer())
val zonesInterface = DatabaseDataTypeInterface("zone", Zone.serializer(), parentKey = "area_id")
val sectorsInterface = DatabaseDataTypeInterface("sector", Sector.serializer(), parentKey = "zone_id")
val pathsInterface = DatabaseDataTypeInterface("path", Path.serializer(), parentKey = "sector_id")
val blockingInterface = DatabaseEntityInterface("blocking", Blocking.serializer(), parentKey = "path_id")

val interfaces = listOf(areasInterface, zonesInterface, sectorsInterface, pathsInterface, blockingInterface)
