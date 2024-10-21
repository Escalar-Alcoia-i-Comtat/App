package ui.model

import cache.DataCache.Areas.allAreas
import cache.DataCache.Areas.allZones
import data.Area
import data.Zone

class ZonesScreenModel : DataScreenModel<Area, Zone>(
    childrenListAccessor = { parentId ->
        allZones()?.filter { it.getParentId() == parentId } ?: emptyList()
    },
    parentListAccessor = { id -> allAreas()?.find { it.id == id } }
)
