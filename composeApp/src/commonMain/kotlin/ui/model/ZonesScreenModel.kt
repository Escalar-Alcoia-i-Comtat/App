package ui.model

import data.Area
import data.Zone
import kotlinx.coroutines.flow.first
import sync.DataSync

class ZonesScreenModel : DataScreenModel<Area, Zone>(
    childrenListAccessor = { parentId ->
        DataSync.zones.first()?.filter { it.getParentId() == parentId } ?: emptyList()
    },
    parentListAccessor = { id -> DataSync.areas.first()?.find { it.id == id } }
)
