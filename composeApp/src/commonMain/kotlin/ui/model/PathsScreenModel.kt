package ui.model

import data.Path
import data.Sector
import database.DatabaseInterface

class PathsScreenModel : DataScreenModel<Sector, Path>(
    childrenListAccessor = { parentId -> DatabaseInterface.paths().getByParentId(parentId) },
    parentListAccessor = { id -> DatabaseInterface.sectors().get(id) }
)
