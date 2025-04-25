package org.escalaralcoiaicomtat.app.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val WikilocLogo: ImageVector
    get() {
        if (_WikilocLogo != null) {
            return _WikilocLogo!!
        }
        _WikilocLogo = ImageVector.Builder(
            name = "WikilocLogo",
            defaultWidth = 128.dp,
            defaultHeight = 128.dp,
            viewportWidth = 33.867f,
            viewportHeight = 33.867f
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(-195.553f, -60.039f)
                    lineTo(114.668f, -60.039f)
                    lineTo(114.668f, 80.841f)
                    lineTo(-195.553f, 80.841f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF4C8C2B))) {
                    moveToRelative(15.879f, 0.033f)
                    curveToRelative(-4.426f, 0.279f, -8.734f, 2.3f, -11.81f, 5.965f)
                    curveToRelative(-5.518f, 6.576f, -5.134f, 16.14f, 0.587f, 22.254f)
                    curveToRelative(-0.587f, -1.357f, -0.958f, -2.814f, -1.091f, -4.33f)
                    curveToRelative(-0.324f, -3.709f, 0.815f, -7.321f, 3.208f, -10.173f)
                    curveToRelative(1.388f, -1.654f, 2.778f, -2.899f, 4.252f, -3.805f)
                    curveToRelative(1.307f, -0.804f, 2.514f, -1.244f, 3.579f, -1.631f)
                    curveToRelative(0.859f, -0.313f, 1.671f, -0.608f, 2.457f, -1.066f)
                    curveToRelative(0.866f, -0.504f, 1.62f, -1.157f, 2.373f, -2.055f)
                    curveToRelative(1.17f, -1.394f, 1.439f, -3.251f, 0.869f, -4.853f)
                    curveToRelative(-1.46f, -0.298f, -2.949f, -0.398f, -4.424f, -0.305f)
                    close()
                    moveTo(26.552f, 3.027f)
                    arcToRelative(10.582f, 10.582f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.232f, 1.461f)
                    curveToRelative(0.242f, 2.77f, -0.609f, 5.469f, -2.396f, 7.599f)
                    curveToRelative(-1.484f, 1.769f, -2.568f, 2.452f, -3.525f, 3.056f)
                    curveToRelative(-0.867f, 0.547f, -1.687f, 1.065f, -2.901f, 2.512f)
                    curveToRelative(-1.932f, 2.302f, -2.852f, 5.219f, -2.59f, 8.212f)
                    curveToRelative(0.258f, 2.952f, 1.635f, 5.63f, 3.88f, 7.556f)
                    curveToRelative(3.973f, -0.553f, 7.764f, -2.522f, 10.545f, -5.838f)
                    curveToRelative(5.962f, -7.104f, 5.035f, -17.697f, -2.07f, -23.658f)
                    arcToRelative(16.975f, 16.975f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.175f, -0.902f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFA300))) {
                    moveToRelative(16.933f, 16.793f)
                    curveToRelative(2.699f, -3.216f, 3.728f, -2.352f, 6.426f, -5.568f)
                    curveToRelative(2.342f, -2.791f, 2.715f, -6.578f, 1.267f, -9.675f)
                    arcToRelative(16.994f, 16.994f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.897f, -1.146f)
                    curveToRelative(0.501f, 1.909f, 0.099f, 4.024f, -1.266f, 5.651f)
                    curveToRelative(-4.048f, 4.824f, -7.264f, 2.125f, -12.661f, 8.557f)
                    curveToRelative(-4.27f, 5.089f, -3.828f, 12.563f, 0.862f, 17.12f)
                    curveToRelative(2.684f, 1.489f, 5.796f, 2.27f, 9.064f, 2.115f)
                    curveToRelative(-4.627f, -4.563f, -5.045f, -11.99f, -0.795f, -17.054f)
                }
            }
        }.build()

        return _WikilocLogo!!
    }

@Suppress("ObjectPropertyName")
private var _WikilocLogo: ImageVector? = null
