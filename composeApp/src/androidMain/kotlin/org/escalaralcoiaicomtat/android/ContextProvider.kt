package org.escalaralcoiaicomtat.android

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

internal lateinit var applicationContext: Context
    private set

/**
 * Initializes [applicationContext] using a [ContentProvider].
 * It doesn't support any operation, just initializes with the given context.
 */
internal class ContextProvider: ContentProvider() {
    override fun onCreate(): Boolean {
        val context = context ?: error("Context cannot be null")
        applicationContext = context.applicationContext
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        error("Not allowed.")
    }

    override fun getType(uri: Uri): String? {
        error("Not allowed.")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        error("Not allowed.")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        error("Not allowed.")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        error("Not allowed.")
    }
}
