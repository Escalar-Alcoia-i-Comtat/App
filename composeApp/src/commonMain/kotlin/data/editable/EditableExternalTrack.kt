package data.editable

import data.generic.ExternalTrack
import data.generic.ExternalTrack.Type

data class EditableExternalTrack(
    val type: Type = Type.Wikiloc,
    val url: String = "",
): Editable<ExternalTrack> {
    private val wikilocRegex = "https://(www\\.)?wikiloc\\.com/[a-zA-Z-]+/[a-zA-Z-]+-\\d+".toRegex()

    override fun validate(): Boolean = when (type) {
        Type.Wikiloc -> wikilocRegex.matches(url)
    }

    override fun build(): ExternalTrack {
        require(validate()) { "The element is not valid" }
        return ExternalTrack(type, url)
    }
}
