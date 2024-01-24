package utils.unit

import androidx.compose.runtime.Composable
import com.russhwolf.settings.set
import database.SettingsKeys
import database.settings
import dev.icerock.moko.resources.compose.stringResource

class UnitsConfiguration {
    var units: DistanceUnits
        get() {
            val units = settings.getString(SettingsKeys.DISTANCE_UNITS, DistanceUnits.METER.name)
            return DistanceUnits.valueOf(units)
        }
        set(value) {
            settings[SettingsKeys.DISTANCE_UNITS] = value.name
        }

    fun Double.asDistance(): DistanceUnit = units.factory(this)

    @Composable
    fun Double.asDistanceValue(): String {
        val valueFormat = units.valueFormat
        val distance = units.factory(this)
        val value = distance.value
        return stringResource(valueFormat, value)
    }
}
