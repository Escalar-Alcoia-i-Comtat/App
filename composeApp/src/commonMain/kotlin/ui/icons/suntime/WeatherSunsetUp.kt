package ui.icons.suntime

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SunTimeIcons.WeatherSunsetUp: ImageVector
    get() {
        if (_WeatherSunsetUp != null) {
            return _WeatherSunsetUp!!
        }
        _WeatherSunsetUp = ImageVector.Builder(
            name = "WeatherSunsetUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(3f, 12f)
                horizontalLineTo(7f)
                arcTo(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 7f)
                arcTo(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 17f, 12f)
                horizontalLineTo(21f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 13f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21f, 14f)
                horizontalLineTo(3f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 13f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 12f)
                moveTo(15f, 12f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 9f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 9f, 12f)
                horizontalLineTo(15f)
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
                moveTo(20.65f, 7f)
                lineTo(18.88f, 10.79f)
                curveTo(18.74f, 10f, 18.47f, 9.23f, 18.05f, 8.5f)
                curveTo(17.63f, 7.78f, 17.1f, 7.15f, 16.5f, 6.64f)
                lineTo(20.65f, 7f)
                moveTo(12.71f, 16.3f)
                lineTo(15.82f, 19.41f)
                curveTo(16.21f, 19.8f, 16.21f, 20.43f, 15.82f, 20.82f)
                curveTo(15.43f, 21.21f, 14.8f, 21.21f, 14.41f, 20.82f)
                lineTo(12f, 18.41f)
                lineTo(9.59f, 20.82f)
                curveTo(9.2f, 21.21f, 8.57f, 21.21f, 8.18f, 20.82f)
                curveTo(7.79f, 20.43f, 7.79f, 19.8f, 8.18f, 19.41f)
                lineTo(11.29f, 16.3f)
                curveTo(11.5f, 16.1f, 11.74f, 16f, 12f, 16f)
                curveTo(12.26f, 16f, 12.5f, 16.1f, 12.71f, 16.3f)
                close()
            }
        }.build()

        return _WeatherSunsetUp!!
    }

@Suppress("ObjectPropertyName")
private var _WeatherSunsetUp: ImageVector? = null
