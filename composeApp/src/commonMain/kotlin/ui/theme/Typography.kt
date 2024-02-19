package ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import escalaralcoiaicomtat.composeapp.generated.resources.Jost_Regular
import escalaralcoiaicomtat.composeapp.generated.resources.Res
import escalaralcoiaicomtat.composeapp.generated.resources.Roboto_Regular
import escalaralcoiaicomtat.composeapp.generated.resources.Rubik_Bold
import escalaralcoiaicomtat.composeapp.generated.resources.Rubik_Medium
import org.jetbrains.compose.resources.Font

private val defaultTypography = Typography()

private object Jost {
    val regular: FontFamily
        @Composable
        get() = FontFamily(Font(Res.font.Jost_Regular))
}

private object Roboto {
    val regular: FontFamily
        @Composable
        get() = FontFamily(Font(Res.font.Roboto_Regular))
}

private object Rubik {
    val bold: FontFamily
        @Composable
        get() = FontFamily(Font(Res.font.Rubik_Bold))

    val medium: FontFamily
        @Composable
        get() = FontFamily(Font(Res.font.Rubik_Medium))
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
