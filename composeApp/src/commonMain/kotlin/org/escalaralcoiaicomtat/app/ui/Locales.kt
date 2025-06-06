package org.escalaralcoiaicomtat.app.ui

import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.escalaralcoiaicomtat.app.ui.lang.ContributorCredit
import org.escalaralcoiaicomtat.app.ui.lang.Language

object Locales {
    val list = sequenceOf("en", "ca", "es", "fr")
    val fallback = Language.English

    fun valueOf(tag: String): Language {
        // First check if the tag is contained
        Language.all.find { it.key == tag }?.let { return it }
        // If it isn't, maybe it has a region suffix, remove it and find again
        val noRegion = tag.substringBefore('-') // eg. en-US
        Language.all.find { it.key == noRegion }?.let { return it }
        // Otherwise, the language is not supported, return the fallback
        return fallback
    }

    suspend fun contributors(): Map<Language, List<ContributorCredit>> {
        val credits = Res.readBytes("files/translations/credits.json").decodeToString()
        return Json.decodeFromString(JsonElement.serializer(), credits)
            .jsonArray
            // Convert the array into JSON objects
            .let { array -> (0 until array.size).map { array[it].jsonObject } }
            // There will always be only one key per object
            .associate { block ->
                val lang = block.keys.first()
                val language = Language.all.find { l -> l.displayName == lang }
                language ?: error("Could not find $lang in supported languages.")
                language to block[lang]!!
            }
            // Decode the ContributorCredit entry
            .mapValues { (_, obj) ->
                Json.decodeFromJsonElement(ListSerializer(ContributorCredit.serializer()), obj)
            }
            .toMap()
    }
}
