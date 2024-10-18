package data

interface DataTypeWithParent : DataType {
    fun getParentId(): Long
}
