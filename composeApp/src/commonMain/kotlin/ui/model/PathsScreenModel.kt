package ui.model

import cache.DataCache.Areas.allPaths
import cache.DataCache.Areas.allSectors
import data.Path
import data.Sector

class PathsScreenModel : DataScreenModel<Sector, Path>(
    childrenListAccessor = { parentId ->
        allPaths()?.filter { it.getParentId() == parentId } ?: emptyList()
    },
    parentListAccessor = { id -> allSectors()?.find { it.id == id } }
)
