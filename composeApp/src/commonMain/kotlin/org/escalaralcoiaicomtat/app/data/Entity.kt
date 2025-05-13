package org.escalaralcoiaicomtat.app.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface Entity {
    val id: Long
    val timestamp: Long

    fun copy(
        id: Long = this.id,
        timestamp: Long = this.timestamp
    ): Entity
}
