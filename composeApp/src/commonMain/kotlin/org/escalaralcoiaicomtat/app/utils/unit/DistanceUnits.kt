package org.escalaralcoiaicomtat.app.utils.unit

import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class DistanceUnits(
    val label: StringResource,
    val valueFormat: StringResource,
    val factory: (Double) -> DistanceUnit
) {
    METER(
        Res.string.unit_label_meter,
        Res.string.unit_value_meter,
        { Meter(it) }
    ),
    FEET(
        Res.string.unit_label_feet,
        Res.string.unit_value_feet,
        { Feet(it) }
    )
}
