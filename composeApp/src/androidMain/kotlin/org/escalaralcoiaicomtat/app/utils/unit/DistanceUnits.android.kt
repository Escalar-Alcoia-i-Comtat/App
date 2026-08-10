package org.escalaralcoiaicomtat.app.utils.unit

import android.icu.util.LocaleData
import android.icu.util.LocaleData.MeasurementSystem
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Converts a [LocaleData.MeasurementSystem] to a [DistanceUnits].
 */
@RequiresApi(Build.VERSION_CODES.P)
fun MeasurementSystem?.toDistanceUnits(): DistanceUnits {
    return when (this) {
        MeasurementSystem.SI -> DistanceUnits.METER
        MeasurementSystem.US, MeasurementSystem.UK -> DistanceUnits.FEET
        else -> DistanceUnits.METER
    }
}
