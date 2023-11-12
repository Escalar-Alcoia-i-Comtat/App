package network.response

import kotlinx.serialization.Serializable

@Serializable
abstract class Response(
    val success: Boolean
)
