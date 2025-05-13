package org.escalaralcoiaicomtat.android

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.database.getDatabaseBuilder
import org.escalaralcoiaicomtat.app.database.roomDatabaseBuilder
import org.escalaralcoiaicomtat.app.initializeSentry
import org.escalaralcoiaicomtat.app.sync.SyncManager

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
    }
}
