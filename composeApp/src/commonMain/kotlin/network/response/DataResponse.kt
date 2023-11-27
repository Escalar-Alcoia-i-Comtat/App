package network.response

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import network.response.data.DataResponseType

object DataResponse {
    inline fun <reified DataType : DataResponseType> decode(
        value: String,
        json: Json = Json
    ): DataType {
        val element = json.decodeFromString<JsonElement>(value).jsonObject
        val success = element.getValue("success").jsonPrimitive.boolean
        if (!success) {
            json.decodeFromString<ErrorResponse>(value).throwException<Unit>(null)
        }
        val data = element.getValue("data").jsonObject
        return json.decodeFromJsonElement(data)
    }
}
