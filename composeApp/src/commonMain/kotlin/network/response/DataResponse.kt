package network.response

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import network.response.data.DataResponseType
import json as defaultJson

object DataResponse {
    fun <DataType : DataResponseType> decode(
        value: String,
        deserializer: DeserializationStrategy<DataType>,
        json: Json = defaultJson
    ): DataType {
        val element = json.decodeFromString<JsonElement>(value).jsonObject
        val success = element.getValue("success").jsonPrimitive.boolean
        if (!success) {
            json.decodeFromString<ErrorResponse>(value).throwException<Unit>(null)
        }
        val data = element.getValue("data").jsonObject
        return json.decodeFromJsonElement(deserializer, data)
    }

    fun decode(
        value: String,
        json: Json = defaultJson
    ) {
        val element = json.decodeFromString<JsonElement>(value).jsonObject
        val success = element.getValue("success").jsonPrimitive.boolean
        if (!success) {
            json.decodeFromString<ErrorResponse>(value).throwException<Unit>(null)
        }
    }
}
