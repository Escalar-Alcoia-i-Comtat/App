package ui.screen

import data.Area
import data.Zone
import ui.model.ZonesScreenModel

class ZonesScreen(id: Long) : DataScreen<Area, Zone>(id, 1, { ZonesScreenModel() }, { SectorsScreen(it) })
