package platform

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
expect fun PlatformNavHandler(navHandler: NavController)
