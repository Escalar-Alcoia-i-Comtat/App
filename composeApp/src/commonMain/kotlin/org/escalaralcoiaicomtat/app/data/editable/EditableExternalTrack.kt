package org.escalaralcoiaicomtat.app.data.editable

import org.escalaralcoiaicomtat.app.data.generic.ExternalTrack

data class EditableExternalTrack(
    val type: ExternalTrack.Type = ExternalTrack.Type.Wikiloc,
    val url: String = "",
): Editable<ExternalTrack> {
    private val wikilocRegex = "https://(www\\.)?wikiloc\\.com/[a-zA-Z-]+/[a-zA-Z-]+-\\d+".toRegex()

    override fun validate(): Boolean = when (type) {
        ExternalTrack.Type.Wikiloc -> wikilocRegex.matches(url)
    }

    override fun build(): ExternalTrack {
        require(validate()) { "The element is not valid" }
        return ExternalTrack(type, url)
    }
}
