package network.response.data

import kotlinx.serialization.Serializable

/**
 * Represents the type of data that a response from the server contains.
 *
 * Doesn't have any code, it just enforced that the super class is serializable as well.
 */
@Serializable
sealed interface DataResponseType
