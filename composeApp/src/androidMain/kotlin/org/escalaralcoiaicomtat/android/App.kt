package org.escalaralcoiaicomtat.android

import android.app.Application
import android.icu.util.LocaleData
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import com.russhwolf.settings.set
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.getDatabaseBuilder
import org.escalaralcoiaicomtat.app.database.roomDatabaseBuilder
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.initializeSentry
import org.escalaralcoiaicomtat.app.sync.SyncManager
import org.escalaralcoiaicomtat.app.utils.unit.toDistanceUnits

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize crash reports
        initializeSentry()

        // Initialize the logging library
        Napier.base(DebugAntilog())

        Napier.v { "Napier is ready." }

        // Initialize the storage provider
        storageProvider = StorageProvider(this)

        // Initialize the Room Database Builder
        roomDatabaseBuilder = getDatabaseBuilder(this)

        // Schedule the sync workers
        SyncManager.scheduleWorker()

        // If API level is greater or equal than P
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            updateDistanceUnits()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun updateDistanceUnits() {
        // Set the default distance unit if none is set
        if (!settings.hasKey(SettingsKeys.DISTANCE_UNITS)) {
            val system = LocaleData.getMeasurementSystem(ULocale.getDefault()).toDistanceUnits()
            Napier.i { "Setting distance units to $system as per system preference." }
            settings[SettingsKeys.DISTANCE_UNITS] = system.name
            settings[SettingsKeys.DISTANCE_UNITS_SYSTEM] = true
        } else {
            val storedUnits = settings.getStringOrNull(SettingsKeys.DISTANCE_UNITS) ?: return
            val systemUnits = LocaleData.getMeasurementSystem(ULocale.getDefault()).toDistanceUnits()
            val isFromSystem = settings.getBoolean(SettingsKeys.DISTANCE_UNITS_SYSTEM, false)
            if (storedUnits != systemUnits.name && isFromSystem) {
                Napier.i { "Updating distance units to $systemUnits as per system preference." }
                settings[SettingsKeys.DISTANCE_UNITS] = systemUnits.name
                settings[SettingsKeys.DISTANCE_UNITS_SYSTEM] = true
            }
        }
    }
}
