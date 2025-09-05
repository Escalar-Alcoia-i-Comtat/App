package org.escalaralcoiaicomtat.app.ui.platform

import androidx.lifecycle.ViewModel

expect class MapViewModel(): ViewModel {
    val supportsZoomButtons: Boolean

    fun zoomIn()

    fun zoomOut()
}
