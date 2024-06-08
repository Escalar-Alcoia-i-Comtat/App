package ui.model

import data.Path
import data.Sector
import database.database

class PathsScreenModel : DataScreenModel<Sector, Path>(
    parentQuery = { id -> database.sectorQueries.get(id).executeAsOneOrNull()?.let { Sector(it) } },
    childrenQuery = { id -> database.pathQueries.getAllByParent(id).executeAsList().map { Path(it) } }
)
