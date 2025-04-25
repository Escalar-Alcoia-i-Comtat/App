package org.escalaralcoiaicomtat.app.ui.icons.suntime

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SunTimeIcons.WeatherSunny: ImageVector
    get() {
        if (_WeatherSunny != null) {
            return _WeatherSunny!!
        }
        _WeatherSunny = ImageVector.Builder(
            name = "WeatherSunny",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(12f, 7f)
                arcTo(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 17f, 12f)
                arcTo(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 17f)
                arcTo(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7f, 12f)
                arcTo(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 7f)
                moveTo(12f, 9f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 9f, 12f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 15f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 15f, 12f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 9f)
                moveTo(12f, 2f)
                lineTo(14.39f, 5.42f)
                curveTo(13.65f, 5.15f, 12.84f, 5f, 12f, 5f)
                curveTo(11.16f, 5f, 10.35f, 5.15f, 9.61f, 5.42f)
                lineTo(12f, 2f)
                moveTo(3.34f, 7f)
                lineTo(7.5f, 6.65f)
                curveTo(6.9f, 7.16f, 6.36f, 7.78f, 5.94f, 8.5f)
                curveTo(5.5f, 9.24f, 5.25f, 10f, 5.11f, 10.79f)
                lineTo(3.34f, 7f)
                moveTo(3.36f, 17f)
                lineTo(5.12f, 13.23f)
                curveTo(5.26f, 14f, 5.53f, 14.78f, 5.95f, 15.5f)
                curveTo(6.37f, 16.24f, 6.91f, 16.86f, 7.5f, 17.37f)
                lineTo(3.36f, 17f)
                moveTo(20.65f, 7f)
                lineTo(18.88f, 10.79f)
                curveTo(18.74f, 10f, 18.47f, 9.23f, 18.05f, 8.5f)
                curveTo(17.63f, 7.78f, 17.1f, 7.15f, 16.5f, 6.64f)
                lineTo(20.65f, 7f)
                moveTo(20.64f, 17f)
                lineTo(16.5f, 17.36f)
                curveTo(17.09f, 16.85f, 17.62f, 16.22f, 18.04f, 15.5f)
                curveTo(18.46f, 14.77f, 18.73f, 14f, 18.87f, 13.21f)
                lineTo(20.64f, 17f)
                moveTo(12f, 22f)
                lineTo(9.59f, 18.56f)
                curveTo(10.33f, 18.83f, 11.14f, 19f, 12f, 19f)
                curveTo(12.82f, 19f, 13.63f, 18.83f, 14.37f, 18.56f)
                lineTo(12f, 22f)
                close()
            }
        }.build()

        return _WeatherSunny!!
    }

@Suppress("ObjectPropertyName")
private var _WeatherSunny: ImageVector? = null
