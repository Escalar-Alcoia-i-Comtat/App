package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.ui.icons.carrier.Movistar
import org.escalaralcoiaicomtat.app.ui.icons.carrier.Orange
import org.escalaralcoiaicomtat.app.ui.icons.carrier.PhoneCarrierIcons
import org.escalaralcoiaicomtat.app.ui.icons.carrier.Vodafone

@Serializable
enum class PhoneCarrier(
    val color: Color,
    val icon: ImageVector,
    val displayName: String,
    val usedBy: List<String>
) {
    MOVISTAR(Color(0xFF019DF4), PhoneCarrierIcons.Movistar, "Movistar", listOf("Movistar", "O2", "Digi.Mobil")),
    ORANGE(Color(0xFFFF7900), PhoneCarrierIcons.Orange, "Orange", listOf("Orange", "Simyo")),
    VODAFONE(Color(0xFFE60000), PhoneCarrierIcons.Vodafone, "Vodafone", listOf("Vodafone", "Pepephone"))
}
