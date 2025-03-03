package network.response.data

import data.DataType
import kotlinx.serialization.Serializable

@Serializable
data class UpdateResponseData<DT: DataType>(
    val element: DT
): DataResponseType
