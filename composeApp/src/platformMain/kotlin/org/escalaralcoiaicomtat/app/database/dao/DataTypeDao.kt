package org.escalaralcoiaicomtat.app.database.dao

import org.escalaralcoiaicomtat.app.data.DataType
import org.escalaralcoiaicomtat.app.database.DataTypeInterface
import org.escalaralcoiaicomtat.app.database.dao.BaseDao.EntityInterfaceImpl
import org.escalaralcoiaicomtat.app.database.entity.DatabaseEntity

interface DataTypeDao<Type : DataType, Entity : DatabaseEntity<Type>> : BaseDao<Type, Entity> {
    /**
     * Tries to get an item by its parent id.
     * May throw [UnsupportedOperationException] if the item does not have a parent.
     */
    suspend fun getByParentId(parentId: Long): List<Entity>

    override fun asInterface(): DataTypeInterface<Type> = DataTypeInterfaceImpl(this)

    class DataTypeInterfaceImpl<Type : DataType, Entity : DatabaseEntity<Type>>(
        dao: DataTypeDao<Type, Entity>,
    ) : EntityInterfaceImpl<Type, Entity>(dao), DataTypeInterface<Type> {
        override suspend fun getByParentId(parentId: Long): List<Type> {
            return (dao as DataTypeDao<Type, Entity>).getByParentId(parentId).map { it.convert() }
        }
    }
}
