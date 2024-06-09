package ui.state

import androidx.compose.ui.input.key.KeyEvent

fun interface KeyEventCollector {
    companion object {
        private val collectors = mutableListOf<KeyEventCollector>()

        fun startCollecting(collector: KeyEventCollector) {
            collectors.add(collector)
        }

        fun stopCollecting(collector: KeyEventCollector) {
            collectors.remove(collector)
        }

        fun emit(event: KeyEvent): Boolean {
            for (collector in collectors) {
                if (collector(event)) {
                    return true
                }
            }
            return false
        }
    }

    operator fun invoke(event: KeyEvent): Boolean
}
