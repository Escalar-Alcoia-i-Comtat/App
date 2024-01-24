package ui.platform

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.PluralsResource

@Composable
expect fun pluralResource(plural: PluralsResource, number: Int, vararg args: Any): String
