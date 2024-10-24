package ui.model

import data.Path
import data.Sector
import kotlinx.coroutines.flow.first
import sync.DataSync

class PathsScreenModel : DataScreenModel<Sector, Path>(
    childrenListAccessor = { parentId ->
        DataSync.paths.first()?.filter { it.getParentId() == parentId } ?: emptyList()
    },
    parentListAccessor = { id -> DataSync.sectors.first()?.find { it.id == id } }
)
