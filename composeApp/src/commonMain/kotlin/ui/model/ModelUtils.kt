package ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.stateIn(
    vm: ViewModel,
    defaultValue: T,
    scope: CoroutineScope = vm.viewModelScope,
    sharing: SharingStarted = SharingStarted.WhileSubscribed(5_000)
) = stateIn(scope, sharing, defaultValue)
