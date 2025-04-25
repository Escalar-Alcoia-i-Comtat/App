package org.escalaralcoiaicomtat.app.network.response.data

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.DataType

@Serializable
data class UpdateResponseData<DT: DataType>(
    val element: DT
): DataResponseType
