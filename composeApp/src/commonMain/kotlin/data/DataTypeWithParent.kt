package data

interface DataTypeWithParent : DataType {
    fun getParentId(): Long

    fun copy(parentId: Long): DataTypeWithParent
}
