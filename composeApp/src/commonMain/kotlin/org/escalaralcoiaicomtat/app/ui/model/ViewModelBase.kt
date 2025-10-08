package org.escalaralcoiaicomtat.app.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.escalaralcoiaicomtat.app.utils.IO

abstract class ViewModelBase : ViewModel() {
    fun <T> Flow<T>.stateIn(
        defaultValue: T,
        scope: CoroutineScope = viewModelScope,
        sharing: SharingStarted = SharingStarted.WhileSubscribed(5_000)
    ) = stateIn(scope, sharing, defaultValue)

    fun launch(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(dispatcher, start, block)
}

expect fun ViewModel.async(block: suspend CoroutineScope.() -> Unit)
