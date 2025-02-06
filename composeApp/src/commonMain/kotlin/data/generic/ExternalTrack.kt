package data.generic

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import ui.icons.WikilocLogo

@Serializable
data class ExternalTrack(
    val type: Type,
    val url: String
) {
    enum class Type(val icon: ImageVector, val displayName: String = "Wikiloc") {
        Wikiloc(WikilocLogo)
    }
}
