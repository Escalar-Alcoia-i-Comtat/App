package org.escalaralcoiaicomtat.app.database.entity

import kotlinx.datetime.Instant
import org.escalaralcoiaicomtat.app.data.Entity

interface DatabaseEntity<Type: Entity> {
    val id: Long
    val timestamp: Instant

    suspend fun convert(): Type
}
