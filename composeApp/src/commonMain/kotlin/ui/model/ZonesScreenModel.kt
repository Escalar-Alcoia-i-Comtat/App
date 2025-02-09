package ui.model

import data.Area
import data.Zone
import database.DatabaseInterface

class ZonesScreenModel : DataScreenModel<Area, Zone>(
    childrenListAccessor = { parentId -> DatabaseInterface.zones().getByParentId(parentId) },
    parentListAccessor = { id -> DatabaseInterface.areas().get(id) }
)
