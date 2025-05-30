package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.ui.icons.suntime.SunTimeIcons
import org.escalaralcoiaicomtat.app.ui.icons.suntime.WeatherSunny
import org.escalaralcoiaicomtat.app.ui.icons.suntime.WeatherSunnyOff
import org.escalaralcoiaicomtat.app.ui.icons.suntime.WeatherSunsetDown
import org.escalaralcoiaicomtat.app.ui.icons.suntime.WeatherSunsetUp
import org.jetbrains.compose.resources.stringResource

/**
 * Represents different times of the day.
 */
@Serializable
enum class SunTime {
    None, Morning, Afternoon, Day;

    fun icon(): ImageVector = when(this) {
        None -> SunTimeIcons.WeatherSunnyOff
        Morning -> SunTimeIcons.WeatherSunsetUp
        Afternoon -> SunTimeIcons.WeatherSunsetDown
        Day -> SunTimeIcons.WeatherSunny
    }

    @Composable
    fun label(): String = stringResource(
        when(this) {
            None -> Res.string.sector_sun_none_label
            Morning -> Res.string.sector_sun_morning_label
            Afternoon -> Res.string.sector_sun_afternoon_label
            Day -> Res.string.sector_sun_all_label
        }
    )

    @Composable
    fun message(): String = stringResource(
        when(this) {
            None -> Res.string.sector_sun_none_description
            Morning -> Res.string.sector_sun_morning_description
            Afternoon -> Res.string.sector_sun_afternoon_description
            Day -> Res.string.sector_sun_all_description
        }
    )
}
