package database

import data.Area
import data.Path
import data.Sector
import data.Zone

actual object DatabaseInterface {
    actual fun areas(): DataTypeInterface<Area> = appDatabase.areas().asInterface()

    actual fun zones(): DataTypeInterface<Zone> = appDatabase.zones().asInterface()

    actual fun sectors(): DataTypeInterface<Sector> = appDatabase.sectors().asInterface()

    actual fun paths(): DataTypeInterface<Path> = appDatabase.paths().asInterface()
}
