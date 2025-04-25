package org.escalaralcoiaicomtat.app.cache

lateinit var storageProvider: StorageProvider

expect class StorageProvider {
    val cacheDirectory: File
}
