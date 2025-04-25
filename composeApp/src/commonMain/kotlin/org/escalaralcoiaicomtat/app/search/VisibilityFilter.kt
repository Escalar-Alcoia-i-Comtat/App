package org.escalaralcoiaicomtat.app.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

sealed class VisibilityFilter(visible: Boolean) : Filter<Any>(visible) {
    data object Visible : VisibilityFilter(true)
    data object Invisible : VisibilityFilter(false)

    override val valueFalse: Filter<Any> = Invisible
    override val valueTrue: Filter<Any> = Visible

    @Composable
    override fun Label() {
        Text(stringResource(Res.string.search_filter_visibility))
    }

    override fun show(obj: Any): Boolean = value
}
