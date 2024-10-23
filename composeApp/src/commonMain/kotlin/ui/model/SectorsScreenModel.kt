package ui.model

import data.Sector
import data.Zone
import kotlinx.coroutines.flow.first
import sync.DataSync

class SectorsScreenModel : DataScreenModel<Zone, Sector>(
    childrenListAccessor = { parentId ->
        DataSync.sectors.first()?.filter { it.getParentId() == parentId } ?: emptyList()
    },
    parentListAccessor = { id -> DataSync.zones.first()?.find { it.id == id } }
)
