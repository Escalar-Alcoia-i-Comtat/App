package org.escalaralcoiaicomtat.android

import android.app.Application
import cache.StorageProvider
import cache.storageProvider
import database.getDatabaseBuilder
import database.roomDatabaseBuilder
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the logging library
        Napier.base(DebugAntilog())

        Napier.v { "Napier is ready." }

        // Initialize the storage provider
        storageProvider = StorageProvider(this)

        // Initialize the Room Database Builder
        roomDatabaseBuilder = getDatabaseBuilder(this)
    }
}
