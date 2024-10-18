package cache

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.set
import data.Area
import data.DataType
import data.DataTypeWithImage
import data.DataTypeWithParent
import data.Path
import data.Sector
import data.Zone
import database.SettingsKeys
import database.settings
import kotlin.concurrent.Volatile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import utils.IO

@OptIn(ExperimentalSettingsApi::class)
object DataCache {
    private val mutex = Semaphore(1)

    private val json = Json

    sealed class Cache<T: DataType> {
        @Volatile
        private var list: List<T>? = null

        abstract val key: String

        suspend fun list(): List<T>? = mutex.withPermit {
            return list ?: settings.getStringOrNull(key)
                ?.let { json.decodeFromString<List<T>>(it) }
                .also { list = it }
        }

        fun flow() = settings.toFlowSettings(Dispatchers.IO)
            .getStringOrNullFlow(key)
            .map { str -> str?.let { json.decodeFromString<List<T>>(it) } }

        suspend fun get(id: Long): T? = list()?.find { it.id == id }

        suspend fun insert(value: T) = mutex.withPermit {
            list = (list ?: emptyList()) + value
            settings[key] = json.encodeToString(list)
        }

        suspend fun update(value: T) = mutex.withPermit {
            var found = false
            list = (list ?: emptyList()).map {
                if (it.id == value.id) {
                    found = true
                    value
                } else {
                    it
                }
            }
            check(found) { "${value::class.simpleName}#${value.id} not found. Could not update." }
            settings[key] = json.encodeToString(list)
        }

        suspend fun <T: DataTypeWithParent> Cache<T>.findByParent(parentId: Long): List<T> =
            list()?.filter { it.getParentId() == parentId } ?: emptyList()
    }

    data object Areas: Cache<Area>() {
        override val key = SettingsKeys.AREAS
    }

    data object Zones: Cache<Zone>() {
        override val key = SettingsKeys.ZONES
    }

    data object Sectors: Cache<Sector>() {
        override val key = SettingsKeys.SECTORS
    }

    data object Paths: Cache<Path>() {
        override val key = SettingsKeys.PATHS
    }
}
