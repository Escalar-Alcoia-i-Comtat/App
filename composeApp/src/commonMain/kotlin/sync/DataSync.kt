package sync

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import data.Area
import data.Path
import data.Sector
import data.Zone
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import network.Backend

@OptIn(ExperimentalSettingsApi::class)
object DataSync : SyncProcess<List<Area>>() {
    /**
     * Synchronizes the data from the server with the local database.
     *
     * @throws IllegalStateException If there's not a valid network connection available.
     */
    override suspend fun synchronize(): List<Area> = try {
        Napier.i { "Running data synchronization..." }
        mutableStatus.emit(Status.RUNNING.Indeterminate)

        Napier.d { "Fetching tree from server..." }
        val areas = Backend.tree()
        Napier.d { "Got ${areas.size} areas" }

        val zones = areas.flatMap { it.zones }
        val sectors = zones.flatMap { it.sectors }
        val paths = sectors.flatMap { it.paths }

        Napier.d { "Saving areas..." }
        settings[SettingsKeys.AREAS] = Json.encodeToString(ListSerializer(Area.serializer()), areas)
        Napier.d { "Saving zones..." }
        settings[SettingsKeys.ZONES] = Json.encodeToString(ListSerializer(Zone.serializer()), zones)
        Napier.d { "Saving sectors..." }
        settings[SettingsKeys.SECTORS] = Json.encodeToString(ListSerializer(Sector.serializer()), sectors)
        Napier.d { "Saving paths..." }
        settings[SettingsKeys.PATHS] = Json.encodeToString(ListSerializer(Path.serializer()), paths)

        settings[SettingsKeys.LAST_SYNC] = Clock.System.now().toEpochMilliseconds()

        areas
    } catch (e: Exception) {
        Napier.e(throwable = e) { "Could not synchronize with server." }
        throw e
    } finally {
        mutableStatus.emit(Status.FINISHED)
    }

    val areas = settings.getStringOrNullFlow(SettingsKeys.AREAS)
        .map { json ->
            json?.let { Json.decodeFromString(ListSerializer(Area.serializer()), it) }
        }
    val zones = settings.getStringOrNullFlow(SettingsKeys.ZONES)
        .map { json ->
            json?.let { Json.decodeFromString(ListSerializer(Zone.serializer()), it) }
        }
    val sectors = settings.getStringOrNullFlow(SettingsKeys.SECTORS)
        .map { json ->
            json?.let { Json.decodeFromString(ListSerializer(Sector.serializer()), it) }
        }
    val paths = settings.getStringOrNullFlow(SettingsKeys.PATHS)
        .map { json ->
            json?.let { Json.decodeFromString(ListSerializer(Path.serializer()), it) }
        }
}
