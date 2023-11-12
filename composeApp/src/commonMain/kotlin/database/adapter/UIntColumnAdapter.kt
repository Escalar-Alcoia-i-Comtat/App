package database.adapter

import app.cash.sqldelight.ColumnAdapter

object UIntColumnAdapter: ColumnAdapter<UInt, Long> {
    override fun decode(databaseValue: Long): UInt = databaseValue.toUInt()

    override fun encode(value: UInt): Long = value.toLong()
}
