package cache

import build.BuildKonfig
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.set
import data.Area
import data.DataType
import data.DataTypeWithParent
import data.Path
import data.Sector
import data.Zone
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import io.ktor.utils.io.core.toByteArray
import kotlin.concurrent.Volatile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import utils.IO

@OptIn(ExperimentalSettingsApi::class)
object DataCache {
    private val mutex = Semaphore(1)

    private val json = Json

    sealed class Cache<T : DataType>(
        private val serializer: KSerializer<T>
    ) {
        abstract val key: String

        /**
         * Flow that emits the data stored in the cache, or null if it has not been initialized yet.
         *
         * **Only available if file-based-caching is enabled.**
         */
        private val dataFlow = if (BuildKonfig.FILE_BASED_CACHE) {
            MutableStateFlow(emptyList<T>())
        } else {
            null
        }

        private val cacheFile get() = storageProvider.cacheDirectory + "data" + "${key}.json"

        /**
         * Returns a flow that emits the data stored in the cache, or null if it has not been
         * initialized yet.
         *
         * If file-based-caching is enabled, the flow will emit the data stored in the cache file.
         * Note that this flow does not support external modifications to the cache file.
         *
         * If file-based-caching is disabled, the flow will emit the data stored in the settings.
         *
         * This function only reads data, does not write it neither in memory or cache.
         */
        private fun getDataOrNullFlow(): Flow<List<T>?> {
            // Note that File-based-caches do not properly support Flows, so we only catch
            // modifications made by the app, not externally
            return dataFlow?.asSharedFlow() ?: settings.toFlowSettings(Dispatchers.IO)
                .getStringOrNullFlow(key)
                .map { str ->
                    str?.let { json.decodeFromString(ListSerializer(serializer), it) }
                }
        }

        /**
         * Updates the flow with the current data stored in the cache.
         *
         * Takes the value currently stored in [list].
         *
         * **Only available if file-based-caching is enabled.**
         */
        private fun updateFlow(list: List<T>) {
            // Only update the flow if the data has changed
            if (dataFlow?.value == list) return

            val success = dataFlow
                ?.also { Napier.d { "Updating data flow ($key) with ${list.size} entries..." } }
                ?.tryEmit(list) ?: true
            if (!success) {
                Napier.w { "Could not send data write to flow." }
            }
        }

        private fun getDataOrNull(): List<T>? {
            return if (BuildKonfig.FILE_BASED_CACHE) {
                if (cacheFile.exists()) {
                    cacheFile.readAllBytes().decodeToString()
                } else {
                    null
                }
            } else {
                settings.getStringOrNull(key)
            }
                ?.let { json.decodeFromString(ListSerializer(serializer), it) }
                ?.also { updateFlow(it) }
        }

        private fun setData(data: List<T>) {
            if (BuildKonfig.FILE_BASED_CACHE) {
                // File-based cache is enabled
                val dataString = json.encodeToString(ListSerializer(serializer), data)
                cacheFile.write(dataString.toByteArray())
                updateFlow(data)
            } else {
                // File-based cache is disabled
                settings[key] = json.encodeToString(ListSerializer(serializer), data)
            }
        }

        suspend fun list(): List<T>? = mutex.withPermit { getDataOrNull() }

        fun flow() = getDataOrNullFlow()

        suspend fun get(id: Long): T? = list()?.find { it.id == id }

        suspend fun <T : DataTypeWithParent> Cache<T>.findByParent(parentId: Long): List<T> =
            list()?.filter { it.getParentId() == parentId } ?: emptyList()

        suspend fun insertOrUpdate(
            data: List<T>,
            progressCallback: suspend (current: Int, max: Int) -> Unit
        ) = mutex.withPermit {
            val list = (getDataOrNull() ?: emptyList()).toMutableList()
            for ((i, element) in data.withIndex()) {
                progressCallback(i, data.size)
                val index = list.indexOfFirst { it.id == element.id }
                if (index != -1) {
                    list[index] = element
                } else {
                    list.add(element)
                }
            }
            setData(list)
        }
    }

    data object Areas : Cache<Area>(Area.serializer()) {
        override val key = SettingsKeys.AREAS

        suspend fun allAreas(): List<Area>? = list()

        suspend fun allZones(): List<Zone>? = list()?.flatMap { it.zones }

        suspend fun allSectors(): List<Sector>? = allZones()?.flatMap { it.sectors }

        suspend fun allPaths(): List<Path>? = allSectors()?.flatMap { it.paths }
    }
}
