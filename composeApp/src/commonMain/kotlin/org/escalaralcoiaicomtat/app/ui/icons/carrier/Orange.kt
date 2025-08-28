package org.escalaralcoiaicomtat.app.ui.icons.carrier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PhoneCarrierIcons.Orange: ImageVector
    get() {
        if (_Orange != null) {
            return _Orange!!
        }
        _Orange = ImageVector.Builder(
            name = "Orange",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(0f, 0f)
                horizontalLineToRelative(24f)
                verticalLineToRelative(24f)
                lineTo(0f, 24f)
                lineTo(0f, 0f)
                close()
                moveTo(3.43f, 20.572f)
                horizontalLineToRelative(17.143f)
                verticalLineToRelative(-3.429f)
                lineTo(3.43f, 17.143f)
                verticalLineToRelative(3.429f)
                close()
            }
        }.build()

        return _Orange!!
    }

@Suppress("ObjectPropertyName")
private var _Orange: ImageVector? = null
