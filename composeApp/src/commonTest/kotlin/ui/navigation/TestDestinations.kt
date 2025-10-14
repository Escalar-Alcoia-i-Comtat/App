package ui.navigation

import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.ui.navigation.Destinations
import kotlin.test.Test
import kotlin.test.assertEquals

class TestDestinations {
    @Test
    fun `test parse`() {
        assertEquals(Destinations.Root, Destinations.parse(""))
        assertEquals(Destinations.Root, Destinations.parse("#"))

        assertEquals(Destinations.Root, Destinations.parse("root"))
        assertEquals(Destinations.Root, Destinations.parse("#root"))

        assertEquals(Destinations.Intro, Destinations.parse("intro"))

        assertEquals(Destinations.Editor(DataTypes.Area, null), Destinations.parse("editor/${DataTypes.Area.name}/new"))
        assertEquals(Destinations.Editor(DataTypes.Area, null, 10), Destinations.parse("editor/${DataTypes.Area.name}/new/10"))
        assertEquals(Destinations.Editor(DataTypes.Area, 5, 10), Destinations.parse("editor/${DataTypes.Area.name}/5/10"))

        assertEquals(Destinations.Area(0), Destinations.parse("0"))
        assertEquals(Destinations.Zone(0, 10), Destinations.parse("0/10"))
        assertEquals(Destinations.Sector(0, 10, 5), Destinations.parse("0/10/5"))
        assertEquals(Destinations.Sector(0, 10, 5, 7), Destinations.parse("0/10/5/7"))
    }
}
