package platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.escalaralcoiaicomtat.app.store

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Reference: https://exyte.com/blog/jetpack-compose-multiplatform
    LaunchedEffect(enabled) {
        store.events.collect {
            if (enabled) {
                onBack()
            }
        }
    }
}
