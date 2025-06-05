package org.escalaralcoiaicomtat.app.ui.lang

import kotlinx.datetime.format.DateTimeComponents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.serialization.DateTimeComponentsSerializer

@Serializable
data class ContributorCredit(
    val email: String,
    val username: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("change_count") val changeCount: Int,
    @Serializable(DateTimeComponentsSerializer::class) @SerialName("date_joined") val dateJoined: DateTimeComponents,
)
