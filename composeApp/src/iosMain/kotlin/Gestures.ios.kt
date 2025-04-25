import org.escalaralcoiaicomtat.app.store
import org.escalaralcoiaicomtat.app.utils.Action

fun onBackGesture() {
    store.send(Action.OnBackPressed)
}
