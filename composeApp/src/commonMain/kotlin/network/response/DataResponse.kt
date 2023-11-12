package network.response

import kotlinx.serialization.Serializable

@Serializable
class DataResponse<DataType: Any>(
    val data: DataType
): Response(true)
