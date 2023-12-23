package search

import androidx.compose.runtime.Composable

abstract class Filter<Type>(
    /**
     * The value associated with the filter.
     */
    val value: Boolean = true
) {
    companion object {
        val Defaults: Array<Filter<Any>> = arrayOf(VisibilityFilter.Visible)
    }

    protected abstract val valueTrue: Filter<Type>

    protected abstract val valueFalse: Filter<Type>

    /**
     * Will be called when drawing the UI, the name that will be shown on the label that identifies the filter.
     */
    @Composable
    abstract fun Label()

    /**
     * Checks whether the object should be shown or not according to the filter.
     *
     * @param obj The object to check for.
     *
     * @return `true` if the object should be displayed, `false` otherwise.
     */
    abstract fun show(obj: Type): Boolean

    fun toggle(): Filter<Type> = if (value) valueFalse else valueTrue
}
