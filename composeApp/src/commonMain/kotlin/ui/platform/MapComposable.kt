package ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapComposable(modifier: Modifier = Modifier, kmzUUID: String? = null)
