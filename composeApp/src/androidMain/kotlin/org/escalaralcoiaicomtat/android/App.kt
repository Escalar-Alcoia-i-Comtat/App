package org.escalaralcoiaicomtat.android

import android.app.Application
import database.DriverFactory
import database.createDatabase
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import network.ConnectivityStatus
import network.connectivityStatus

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the logging library
        Napier.base(DebugAntilog())

        Napier.v { "Napier is ready." }

        // Create the database
        createDatabase(DriverFactory(this))

        connectivityStatus = ConnectivityStatus(this)
    }
}
