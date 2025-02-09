package database

import data.Area
import data.DataType
import data.DataTypes
import data.Path
import data.Sector
import data.Zone

expect object DatabaseInterface {
    fun areas(): DataTypeInterface<Area>
    fun zones(): DataTypeInterface<Zone>
    fun sectors(): DataTypeInterface<Sector>
    fun paths(): DataTypeInterface<Path>
}

@Suppress("UNCHECKED_CAST")
fun <DT: DataType> DatabaseInterface.byType(type: DataTypes<DT>): DataTypeInterface<DT> = when (type) {
    DataTypes.Area -> areas()
    DataTypes.Zone -> zones()
    DataTypes.Sector -> sectors()
    DataTypes.Path -> paths()
} as DataTypeInterface<DT>
