package org.escalaralcoiaicomtat.app

import org.escalaralcoiaicomtat.app.utils.Action

fun onBackGesture() {
    store.send(Action.OnBackPressed)
}
