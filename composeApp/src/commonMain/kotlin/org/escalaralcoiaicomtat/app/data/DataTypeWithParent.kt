package org.escalaralcoiaicomtat.app.data

interface DataTypeWithParent : DataType {
    val parentId: Long

    fun copy(parentId: Long): DataTypeWithParent
}
