package ui.model

import data.Sector
import data.Zone
import database.DatabaseInterface

class SectorsScreenModel : DataScreenModel<Zone, Sector>(
    childrenListAccessor = { parentId -> DatabaseInterface.sectors().getByParentId(parentId) },
    parentListAccessor = { id -> DatabaseInterface.zones().get(id) }
)
