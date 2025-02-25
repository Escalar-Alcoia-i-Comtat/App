package sync

import com.russhwolf.settings.set
import data.DataTypes
import database.DatabaseInterface
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import network.Backend

object DataSync : SyncProcess() {
    private const val ARG_CAUSE = "cause"
    private const val ARG_ID = "id"
    private const val ARG_TYPE = "type"

    enum class Cause {
        Push, Scheduled, Manual
    }

    suspend fun start(
        cause: Cause,
        syncId: Pair<DataTypes<*>, Int>? = null
    ) = start(
        mapOf(ARG_CAUSE to cause.name, ARG_ID to syncId?.second, ARG_TYPE to syncId?.first)
    )

    /**
     * Synchronizes the data from the server with the local database.
     *
     * @throws IllegalStateException If there's not a valid network connection available.
     */
    override suspend fun SyncContext.synchronize() {
        val cause = getString(ARG_CAUSE)?.let(Cause::valueOf)
        val id = getString(ARG_ID)?.toIntOrNull()
        val type = getString(ARG_TYPE)?.let { t -> DataTypes.findByName(t) }

        Napier.i { "Running data synchronization..." }
        setStatus(Status.RUNNING.Indeterminate)

        val progress: suspend (current: Long, total: Long) -> Unit = { current, total ->
            setStatus(Status.RUNNING(current.toFloat() / total))
        }

        if (id != null && type != null) {
            Napier.d { "Fetching $type#$id from server..." }
            when (type) {
                DataTypes.Area -> {
                    val item = Backend.area(id, progress)
                    if (item != null) {
                        Napier.d { "Storing Area#$id..." }
                        DatabaseInterface.areas().updateOrInsert(item)
                    } else {
                        Napier.e { "Could not find Area#$id in server." }
                    }
                }

                DataTypes.Zone -> {
                    val item = Backend.zone(id, progress)
                    if (item != null) {
                        Napier.d { "Storing Zone#$id..." }
                        DatabaseInterface.zones().updateOrInsert(item)
                    } else {
                        Napier.e { "Could not find Zone#$id in server." }
                    }
                }

                DataTypes.Sector -> {
                    val item = Backend.sector(id, progress)
                    if (item != null) {
                        Napier.d { "Storing Sector#$id..." }
                        DatabaseInterface.sectors().updateOrInsert(item)
                    } else {
                        Napier.e { "Could not find Sector#$id in server." }
                    }
                }

                DataTypes.Path -> {
                    val item = Backend.path(id, progress)
                    if (item != null) {
                        Napier.d { "Storing Path#$id..." }
                        DatabaseInterface.paths().updateOrInsert(item)
                    } else {
                        Napier.e { "Could not find Path#$id in server." }
                    }
                }
            }
        } else @Suppress("DEPRECATION") {
            Napier.d { "Fetching tree from server..." }
            val areas = Backend.tree(progress)
            Napier.d { "Got ${areas.size} areas" }

            setStatus(Status.RUNNING.Indeterminate)

            val zones = areas.flatMap { it.zones }
            val sectors = zones.flatMap { it.sectors }
            val paths = sectors.flatMap { it.paths }

            Napier.d { "Saving ${areas.size} areas..." }
            DatabaseInterface.areas().updateOrInsert(areas)
            Napier.d { "Saving ${zones.size} zones..." }
            DatabaseInterface.zones().updateOrInsert(zones)
            Napier.d { "Saving ${sectors.size} sectors..." }
            DatabaseInterface.sectors().updateOrInsert(sectors)
            Napier.d { "Saving ${paths.size} paths..." }
            DatabaseInterface.paths().updateOrInsert(paths)
        }

        settings[SettingsKeys.LAST_SYNC_TIME] = Clock.System.now().toEpochMilliseconds()
        settings[SettingsKeys.LAST_SYNC_CAUSE] = cause?.name
    }
}
