package org.escalaralcoiaicomtat.app.ui.icons.carrier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PhoneCarrierIcons.Vodafone: ImageVector
    get() {
        if (_Vodafone != null) {
            return _Vodafone!!
        }
        _Vodafone = ImageVector.Builder(
            name = "Vodafone",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 0f)
                arcTo(12f, 12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 12f)
                arcTo(12f, 12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 24f)
                arcTo(12f, 12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 24f, 12f)
                arcTo(12f, 12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 0f)
                moveTo(16.25f, 1.12f)
                curveTo(16.57f, 1.12f, 16.9f, 1.15f, 17.11f, 1.22f)
                curveTo(14.94f, 1.67f, 13.21f, 3.69f, 13.22f, 6f)
                curveTo(13.22f, 6.05f, 13.22f, 6.11f, 13.23f, 6.17f)
                curveTo(16.87f, 7.06f, 18.5f, 9.25f, 18.5f, 12.28f)
                curveTo(18.54f, 15.31f, 16.14f, 18.64f, 12.09f, 18.65f)
                curveTo(8.82f, 18.66f, 5.41f, 15.86f, 5.39f, 11.37f)
                curveTo(5.38f, 8.4f, 7f, 5.54f, 9.04f, 3.85f)
                curveTo(11.04f, 2.19f, 13.77f, 1.13f, 16.25f, 1.12f)
                close()
            }
        }.build()

        return _Vodafone!!
    }

@Suppress("ObjectPropertyName")
private var _Vodafone: ImageVector? = null
