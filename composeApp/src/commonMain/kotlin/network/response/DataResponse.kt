package network.response

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import network.response.data.DataResponseType

object DataResponse {
    fun <DataType : DataResponseType> decode(
        value: String,
        deserializer: DeserializationStrategy<DataType>,
        json: Json = Json
    ): DataType {
        val element = json.decodeFromString<JsonElement>(value).jsonObject
        val success = element.getValue("success").jsonPrimitive.boolean
        if (!success) {
            json.decodeFromString<ErrorResponse>(value).throwException<Unit>(null)
        }
        val data = element.getValue("data").jsonObject
        return json.decodeFromJsonElement(deserializer, data)
    }
}
