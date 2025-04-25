package org.escalaralcoiaicomtat.app.database.entity

import kotlinx.datetime.Instant
import org.escalaralcoiaicomtat.app.data.DataType

interface DatabaseEntity<Type: DataType> {
    val id: Long
    val timestamp: Instant

    suspend fun convert(): Type
}
