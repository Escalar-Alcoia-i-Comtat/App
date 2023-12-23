package search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed class VisibilityFilter(visible: Boolean) : Filter<Any>(visible) {
    data object Visible : VisibilityFilter(true)
    data object Invisible : VisibilityFilter(false)

    override val valueFalse: Filter<Any> = Invisible
    override val valueTrue: Filter<Any> = Visible

    @Composable
    override fun Label() {
        // TODO : Translations
        Text("Visible")
    }

    override fun show(obj: Any): Boolean = value
}
