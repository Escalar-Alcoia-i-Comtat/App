package org.escalaralcoiaicomtat.app.ui.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import org.escalaralcoiaicomtat.app.database.DatabaseInterface

class ReportScreenModel(
    sectorId: Long?,
    pathId: Long?,
) : ViewModelBase() {

    private val sectorsInterface = DatabaseInterface.sectors()
    private val pathsInterface = DatabaseInterface.paths()

    val sector = sectorId?.let { sectorsInterface.getLive(it) } ?: emptyFlow()
    val path = pathId?.let { pathsInterface.getLive(it) } ?: emptyFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    private val _state = MutableStateFlow(State())
    val state get() = _state.asStateFlow()

    fun onNameChange(name: String) {
        _state.tryEmit(state.value.copy(name = name))
    }

    fun onEmailChange(email: String) {
        _state.tryEmit(state.value.copy(email = email))
    }

    fun onMessageChange(message: String) {
        _state.tryEmit(state.value.copy(message = message))
    }

    fun send() {
        // TODO
    }


    data class State(
        val name: String = "",
        val email: String = "",
        val message: String = "",
    )

}
