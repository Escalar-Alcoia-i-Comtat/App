package org.escalaralcoiaicomtat.app.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    abstract val name: String

    abstract val id: Long?

    override fun toString(): String {
        return name.lowercase()
    }
}
