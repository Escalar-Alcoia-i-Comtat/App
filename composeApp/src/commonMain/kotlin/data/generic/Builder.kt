package data.generic

import kotlinx.serialization.Serializable

@Serializable
data class Builder(
    val name: String? = null,
    val date: String? = null
) {
    fun orNull(): Builder? {
        return if (name.isNullOrBlank() && date.isNullOrBlank()) null
        else this
    }
}
