package org.escalaralcoiaicomtat.app.ui.model

import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import org.escalaralcoiaicomtat.app.database.DatabaseInterface
import org.escalaralcoiaicomtat.app.network.ContactBackend

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

    fun addFile(file: PlatformFile) {
        val state = state.value
        _state.tryEmit(
            state.copy(files = state.files + file)
        )
    }

    fun removeFile(file: PlatformFile) {
        val state = state.value
        _state.tryEmit(
            state.copy(files = state.files - file)
        )
    }

    fun send() {
        val state = state.value

        launch {
            try {
                _isLoading.emit(true)
                ContactBackend.sendReport(
                    name = state.name,
                    email = state.email,
                    message = state.message,
                    files = state.files,
                )
                // TODO: Notify user
            } finally {
                _isLoading.emit(false)
            }
        }
    }


    data class State(
        val name: String = "",
        val email: String = "",
        val message: String = "",
        val files: List<PlatformFile> = emptyList(),
    )

    companion object {
        const val MAX_FILES_SIZE = 10L * 1024 * 1024
    }

}
