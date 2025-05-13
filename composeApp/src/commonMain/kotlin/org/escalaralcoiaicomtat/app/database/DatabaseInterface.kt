package org.escalaralcoiaicomtat.app.database

import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.data.DataType
import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone

expect object DatabaseInterface {
    fun areas(): DataTypeInterface<Area>
    fun zones(): DataTypeInterface<Zone>
    fun sectors(): DataTypeInterface<Sector>
    fun paths(): DataTypeInterface<Path>

    fun blocking(): EntityInterface<Blocking>
}

@Suppress("UNCHECKED_CAST")
fun <DT : DataType> DatabaseInterface.byType(type: DataTypes<DT>): EntityInterface<DT> =
    when (type) {
        DataTypes.Area -> areas()
        DataTypes.Zone -> zones()
        DataTypes.Sector -> sectors()
        DataTypes.Path -> paths()
    } as EntityInterface<DT>

/**
 * Should return the parent interface of this one, eg, the interface that fetches the parents
 * of this interface.
 *
 * If parents are not supported by the data type, [UnsupportedOperationException] shall be thrown.
 *
 * @param childType The type of the current [EntityInterface].
 *
 * @return The interface that can fetch the [childType]'s parent.
 *
 * @throws UnsupportedOperationException If the [childType] doesn't have parents.
 */
@Suppress("UnusedReceiverParameter")
fun <DT : DataType> EntityInterface<DT>.parentInterface(
    childType: DataTypes<DT>
): EntityInterface<DataType> {
    val int = when (childType) {
        DataTypes.Area -> throw UnsupportedOperationException()
        DataTypes.Zone -> DatabaseInterface.areas()
        DataTypes.Sector -> DatabaseInterface.zones()
        DataTypes.Path -> DatabaseInterface.sectors()
    }
    @Suppress("UNCHECKED_CAST")
    return int as EntityInterface<DataType>
}
