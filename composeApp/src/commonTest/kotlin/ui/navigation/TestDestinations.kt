package ui.navigation

import data.DataTypes
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

        assertEquals(Destinations.Editor(DataTypes.Area, null), Destinations.parse("editor_${DataTypes.Area.name}_new"))
        assertEquals(Destinations.Editor(DataTypes.Area, null, 10), Destinations.parse("editor_${DataTypes.Area.name}_new_10"))
        assertEquals(Destinations.Editor(DataTypes.Area, 5, 10), Destinations.parse("editor_${DataTypes.Area.name}_5_10"))

        assertEquals(Destinations.Area(0), Destinations.parse("0"))
        assertEquals(Destinations.Zone(0, 10), Destinations.parse("0_10"))
        assertEquals(Destinations.Sector(0, 10, 5), Destinations.parse("0_10_5"))
        assertEquals(Destinations.Sector(0, 10, 5, 7), Destinations.parse("0_10_5_7"))
    }
}
