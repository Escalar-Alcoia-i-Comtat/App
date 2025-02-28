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
fun <DT : DataType> DatabaseInterface.byType(type: DataTypes<DT>): DataTypeInterface<DT> =
    when (type) {
        DataTypes.Area -> areas()
        DataTypes.Zone -> zones()
        DataTypes.Sector -> sectors()
        DataTypes.Path -> paths()
    } as DataTypeInterface<DT>

/**
 * Should return the parent interface of this one, eg, the interface that fetches the parents
 * of this interface.
 *
 * If parents are not supported by the data type, [UnsupportedOperationException] shall be thrown.
 *
 * @param childType The type of the current [DataTypeInterface].
 *
 * @return The interface that can fetch the [childType]'s parent.
 *
 * @throws UnsupportedOperationException If the [childType] doesn't have parents.
 */
@Suppress("UnusedReceiverParameter")
fun <DT : DataType> DataTypeInterface<DT>.parentInterface(
    childType: DataTypes<DT>
): DataTypeInterface<DataType> {
    val int = when (childType) {
        DataTypes.Area -> throw UnsupportedOperationException()
        DataTypes.Zone -> DatabaseInterface.areas()
        DataTypes.Sector -> DatabaseInterface.zones()
        DataTypes.Path -> DatabaseInterface.sectors()
    }
    @Suppress("UNCHECKED_CAST")
    return int as DataTypeInterface<DataType>
}
