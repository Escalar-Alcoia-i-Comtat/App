package utils.unit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.set
import database.SettingsKeys
import database.settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import utils.format

class UnitsConfiguration {
    fun getUnits(): DistanceUnits {
        val units = settings.getString(SettingsKeys.DISTANCE_UNITS, DistanceUnits.METER.name)
        return DistanceUnits.valueOf(units)
    }

    fun setUnits(units: DistanceUnits) {
        settings[SettingsKeys.DISTANCE_UNITS] = units.name
    }

    @ExperimentalSettingsApi
    private val unitsFlow: Flow<String> = settings.getStringFlow(
        SettingsKeys.DISTANCE_UNITS,
        DistanceUnits.METER.name
    )

    @ExperimentalSettingsApi
    val unitsLive = unitsFlow.map { DistanceUnits.valueOf(it) }

    @Suppress("UNCHECKED_CAST")
    fun <U: DistanceUnit> Double.asDistance(): U = getUnits().factory(this) as U

    @Composable
    @ExperimentalSettingsApi
    fun Double.asDistanceValue(): String {
        val units by unitsLive.collectAsState(DistanceUnits.METER)
        val valueFormat = units.valueFormat
        val distance = units.factory(this)
        val value = distance.value
        return stringResource(valueFormat).format(value)
    }

    @Composable
    @ExperimentalSettingsApi
    fun DistanceUnit.asDistanceValue(): String {
        val units by unitsLive.collectAsState(DistanceUnits.METER)
        val converted = convertTo<DistanceUnit>(units)
        val valueFormat = units.valueFormat
        val distance = units.factory(converted.value)
        val value = distance.value
        return stringResource(valueFormat).format(value)
    }
}
