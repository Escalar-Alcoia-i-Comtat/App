package org.escalaralcoiaicomtat.app.ui

object Locales {
    val list = sequenceOf("en", "ast", "ca", "es", "eu", "gl")
    val fallback = "en"

    fun valueOf(tag: String): String {
        // First check if the tag is contained
        list.find { it == tag }?.let { return it }
        // If it isn't, maybe it has a region suffix, remove it and find again
        val noRegion = tag.substringBefore('-') // eg. en-US
        list.find { it == noRegion }?.let { return it }
        // Otherwise, the language is not supported, return the fallback
        return fallback
    }
}
