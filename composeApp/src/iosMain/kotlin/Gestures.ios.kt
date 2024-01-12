import utils.Action

fun onBackGesture() {
    store.send(Action.OnBackPressed)
}
