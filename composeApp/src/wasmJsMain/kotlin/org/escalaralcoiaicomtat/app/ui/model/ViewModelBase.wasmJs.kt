package org.escalaralcoiaicomtat.app.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise

actual fun ViewModel.async(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.promise {
        block()
    }
}
