package org.escalaralcoiaicomtat.app.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

actual fun ViewModel.async(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.IO) { block() }
}
