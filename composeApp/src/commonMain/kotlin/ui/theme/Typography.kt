package ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.fontFamilyResource
import resources.MR

private val defaultTypography = Typography()

private object Jost {
    val regular: FontFamily
        @Composable
        get() = fontFamilyResource(MR.fonts.Jost.regular)
}

private object Roboto {
    val regular: FontFamily
        @Composable
        get() = fontFamilyResource(MR.fonts.Roboto.regular)
}

private object Rubik {
    val bold: FontFamily
        @Composable
        get() = fontFamilyResource(MR.fonts.Rubik.bold)

    val medium: FontFamily
        @Composable
        get() = fontFamilyResource(MR.fonts.Rubik.medium)
}

// Set of Material typography styles to start with
val Typography: Typography
    @Composable
    get() = Typography(
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = Jost.regular),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = Jost.regular),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = Jost.regular),

        titleLarge = defaultTypography.titleLarge.copy(fontFamily = Jost.regular),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = Jost.regular),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = Jost.regular),

        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = Roboto.regular),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = Roboto.regular),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = Roboto.regular),

        labelLarge = defaultTypography.labelLarge.copy(fontFamily = Rubik.bold, fontSize = 18.sp),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = Rubik.medium, fontSize = 16.sp),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = Rubik.medium, fontSize = 14.sp)
    )
