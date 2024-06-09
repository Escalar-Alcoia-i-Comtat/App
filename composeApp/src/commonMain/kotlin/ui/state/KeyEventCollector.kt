package ui.state

import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.flow.MutableStateFlow

val keyEventsFlow = MutableStateFlow<KeyEvent?>(null)
