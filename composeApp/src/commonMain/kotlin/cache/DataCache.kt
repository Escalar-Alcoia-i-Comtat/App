package cache

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
import kotlin.concurrent.Volatile
import kotlinx.coroutines.Dispatchers
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

    sealed class Cache<T: DataType>(
        private val serializer: KSerializer<T>
    ) {
        @Volatile
        private var list: List<T>? = null

        abstract val key: String

        suspend fun list(): List<T>? = mutex.withPermit {
            return list ?: settings.getStringOrNull(key)
                ?.let { json.decodeFromString(ListSerializer(serializer), it) }
                .also { list = it }
        }

        fun flow() = settings.toFlowSettings(Dispatchers.IO)
            .getStringOrNullFlow(key)
            .map { str -> str?.let { json.decodeFromString(ListSerializer(serializer), it) } }

        suspend fun get(id: Long): T? = list()?.find { it.id == id }

        suspend fun insert(value: T) = mutex.withPermit {
            list = (list ?: emptyList()) + value
            val str = json.encodeToString(ListSerializer(serializer), list!!)
            settings[key] = str
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
            val str = json.encodeToString(ListSerializer(serializer), list!!)
            settings[key] = str
        }

        suspend fun <T: DataTypeWithParent> Cache<T>.findByParent(parentId: Long): List<T> =
            list()?.filter { it.getParentId() == parentId } ?: emptyList()
    }

    data object Areas: Cache<Area>(Area.serializer()) {
        override val key = SettingsKeys.AREAS
    }

    data object Zones: Cache<Zone>(Zone.serializer()) {
        override val key = SettingsKeys.ZONES
    }

    data object Sectors: Cache<Sector>(Sector.serializer()) {
        override val key = SettingsKeys.SECTORS
    }

    data object Paths: Cache<Path>(Path.serializer()) {
        override val key = SettingsKeys.PATHS
    }
}
