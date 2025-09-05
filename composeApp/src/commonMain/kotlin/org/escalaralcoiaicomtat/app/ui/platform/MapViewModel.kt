package org.escalaralcoiaicomtat.app.ui.platform

import androidx.lifecycle.ViewModel

expect class MapViewModel(): ViewModel {
    fun zoomIn()

    fun zoomOut()
}
