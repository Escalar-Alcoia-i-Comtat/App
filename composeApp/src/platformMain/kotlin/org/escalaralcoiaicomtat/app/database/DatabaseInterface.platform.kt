package org.escalaralcoiaicomtat.app.database

import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone

actual object DatabaseInterface {
    actual fun areas(): DataTypeInterface<Area> = appDatabase.areas().asInterface()

    actual fun zones(): DataTypeInterface<Zone> = appDatabase.zones().asInterface()

    actual fun sectors(): DataTypeInterface<Sector> = appDatabase.sectors().asInterface()

    actual fun paths(): DataTypeInterface<Path> = appDatabase.paths().asInterface()

    actual fun blocking(): BlockingInterface = appDatabase.blocking().asInterface()
}
