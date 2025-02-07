package database.entity

import data.DataType
import kotlinx.datetime.Instant

interface DatabaseEntity<Type: DataType> {
    val id: Long
    val timestamp: Instant

    suspend fun convert(): Type
}
