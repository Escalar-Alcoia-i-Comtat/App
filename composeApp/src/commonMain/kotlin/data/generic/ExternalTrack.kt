package data.generic

import kotlinx.serialization.Serializable

@Serializable
data class ExternalTrack(
    val type: Type,
    val url: String
) {
    enum class Type {
        Wikiloc
    }
}
