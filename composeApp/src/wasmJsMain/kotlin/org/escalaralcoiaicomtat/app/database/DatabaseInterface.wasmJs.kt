package org.escalaralcoiaicomtat.app.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone

actual object DatabaseInterface {
    init {
        CoroutineScope(Dispatchers.Default).launch { Database.open() }
    }

    actual fun areas(): DataTypeInterface<Area> = areasInterface

    actual fun zones(): DataTypeInterface<Zone> = zonesInterface

    actual fun sectors(): DataTypeInterface<Sector> = sectorsInterface

    actual fun paths(): DataTypeInterface<Path> = pathsInterface

    actual fun blocking(): EntityInterface<Blocking> = blockingInterface
}
