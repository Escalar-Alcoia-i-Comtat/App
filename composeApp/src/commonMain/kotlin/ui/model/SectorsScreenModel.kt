package ui.model

import cache.DataCache.Areas.allSectors
import cache.DataCache.Areas.allZones
import data.Sector
import data.Zone

class SectorsScreenModel : DataScreenModel<Zone, Sector>(
    childrenListAccessor = { parentId ->
        allSectors()?.filter { it.getParentId() == parentId } ?: emptyList()
    },
    parentListAccessor = { id -> allZones()?.find { it.id == id } }
)
