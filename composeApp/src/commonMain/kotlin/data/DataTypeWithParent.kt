package data

interface DataTypeWithParent : DataType {
    val parentId: Long

    fun copy(parentId: Long): DataTypeWithParent
}
