package org.escalaralcoiaicomtat.app

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.database.setRoomDatabaseBuilder

fun debugBuild() {
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    // Initialize the database
    setRoomDatabaseBuilder()
}
