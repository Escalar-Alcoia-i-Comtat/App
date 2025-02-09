package database

import data.Area
import data.DataType
import data.Sector
import data.Zone
import io.github.aakira.napier.Napier
import kotlinx.browser.localStorage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class LocalStorageDataTypeInterface<Type : DataType>(
    private val key: String,
    private val serializer: KSerializer<Type>,
    private val pollingRate: Long = 100,
) : DataTypeInterface<Type> {
    private fun buildKey(id: Long) = "$key#${id}"
    private fun buildKey(item: Type) = buildKey(item.id)

    @Suppress("UNCHECKED_CAST")
    override suspend fun insert(items: List<Type>) {
        for (item in items) {
            try {
                // Clear the children from all data types
                val data = when (item) {
                    is Area -> item.copy(zones = emptyList()) as Type
                    is Zone -> item.copy(sectors = emptyList()) as Type
                    is Sector -> item.copy(paths = emptyList()) as Type
                    else -> item
                }
                val str = Json.encodeToString(serializer, data)
                localStorage.setItem(buildKey(data), str)
            } catch (e: SerializationException) {
                Napier.e(e) { "Could not encode $key #${item.id}" }
            }
        }
    }

    override suspend fun update(items: List<Type>) = insert(items)

    override suspend fun delete(items: List<Type>) {
        for (item in items) {
            localStorage.removeItem(buildKey(item))
        }
    }

    override suspend fun all(): List<Type> {
        return (0 until localStorage.length)
            // Extract the key from the index
            .mapNotNull(localStorage::key)
            // Filter the keys for the current type
            .filter { it.startsWith(key) }
            // Get the item value associated with the key
            .associateWith(localStorage::getItem)
            // Decode the item
            .mapNotNull { (key, value) ->
                if (value == null) return@mapNotNull null
                try {
                    Json.decodeFromString(serializer, value)
                } catch (e: SerializationException) {
                    Napier.e(e) { "Could not decode $key. The stored JSON is not valid." }
                    null
                } catch (e: IllegalArgumentException) {
                    Napier.e(e) { "Could not decode $key. The stored JSON could not be decoded into ${serializer.descriptor.serialName}." }
                    null
                }
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun allLive(): Flow<List<Type>> = channelFlow {
        var values = all()
        trySend(values) // Send the initial values

        var isChannelOpen = true
        while (isChannelOpen) {
            val new = all()
            if (isClosedForSend) {
                isChannelOpen = false
                break
            } else if (new != values) {
                trySend(new)
                    .onSuccess { values = new }
                    .onClosed { isChannelOpen = false }
            }
            delay(pollingRate)
        }
        close()
    }

    override suspend fun get(id: Int): Type? {
        val value = localStorage.getItem(buildKey(id.toLong())) ?: return null
        return try {
            Json.decodeFromString(serializer, value)
        } catch (e: SerializationException) {
            Napier.e(e) { "Could not decode $key. The stored JSON is not valid." }
            null
        } catch (e: IllegalArgumentException) {
            Napier.e(e) { "Could not decode $key. The stored JSON could not be decoded into ${serializer.descriptor.serialName}." }
            null
        }
    }

    override suspend fun getByParentId(parentId: Int): List<Type> {
        TODO("Not yet implemented")
    }
}
