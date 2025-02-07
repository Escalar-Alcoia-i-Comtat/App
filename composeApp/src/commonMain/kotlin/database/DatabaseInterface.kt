package database

import data.Area
import data.Path
import data.Sector
import data.Zone

expect object DatabaseInterface {
    fun areas(): DataTypeInterface<Area>
    fun zones(): DataTypeInterface<Zone>
    fun sectors(): DataTypeInterface<Sector>
    fun paths(): DataTypeInterface<Path>
}
