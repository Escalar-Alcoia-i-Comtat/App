package database.adapter

import app.cash.sqldelight.ColumnAdapter
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString

inline fun <reified Type: Any> SerializableAdapter() = object : ColumnAdapter<Type, String> {
    override fun decode(databaseValue: String): Type = DefaultJson.decodeFromString(databaseValue)

    override fun encode(value: Type): String = DefaultJson.encodeToString(value)
}
