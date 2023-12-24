package ui.screen

import data.Sector
import data.Zone
import ui.model.SectorsScreenModel

class SectorsScreen(id: Long) : DataScreen<Zone, Sector>(id, 2, { SectorsScreenModel(it) }, { PathsScreen(it) })
