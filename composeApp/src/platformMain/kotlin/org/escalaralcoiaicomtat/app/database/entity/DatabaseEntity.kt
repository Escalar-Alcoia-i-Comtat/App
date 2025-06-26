package org.escalaralcoiaicomtat.app.database.entity

import org.escalaralcoiaicomtat.app.data.Entity
import kotlin.time.Instant

interface DatabaseEntity<Type: Entity> {
    val id: Long
    val timestamp: Instant

    suspend fun convert(): Type
}
