package data.editable

import data.generic.ExternalTrack
import data.generic.ExternalTrack.Type

data class EditableExternalTrack(
    val type: Type? = Type.Wikiloc,
    val url: String = "",
): Editable<ExternalTrack> {
    // TODO: Validate URL
    override fun validate(): Boolean = type != null && url.isNotBlank()

    override fun build(): ExternalTrack {
        require(validate()) { "The element is not valid" }
        return ExternalTrack(type!!, url)
    }
}
