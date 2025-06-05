package org.escalaralcoiaicomtat.app.ui

import kotlinx.coroutines.runBlocking
import org.escalaralcoiaicomtat.app.ui.lang.Language
import org.junit.Test
import kotlin.test.assertEquals

class TestLocales {
    @Test
    fun test_contributors() {
        val contributors = runBlocking { Locales.contributors().toList() }
        assertEquals(3, contributors.size)
        contributors[0].let { (lang, list) ->
            assertEquals(Language.Catalan, lang)
            assertEquals(2, list.size)
            assertEquals("username1", list[0].username)
            assertEquals("username2", list[1].username)
        }
        contributors[1].let { (lang, list) ->
            assertEquals(Language.French, lang)
            assertEquals(1, list.size)
            assertEquals("username2", list[0].username)
        }
        contributors[2].let { (lang, list) ->
            assertEquals(Language.Spanish, lang)
            assertEquals(2, list.size)
            assertEquals("username1", list[0].username)
            assertEquals("username2", list[1].username)
        }
    }
}