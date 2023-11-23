package network.response

import kotlinx.serialization.Serializable
import network.response.data.DataResponseType

@Serializable
class DataResponse<DataType: DataResponseType>(
    val data: DataType
): Response(true)
