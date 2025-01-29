package ui.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface Destination {
    @Transient
    val name: String

    @Transient
    val path: String
}
