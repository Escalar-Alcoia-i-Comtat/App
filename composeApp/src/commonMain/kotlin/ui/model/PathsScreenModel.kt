package ui.model

import data.Path
import data.Sector
import database.database

class PathsScreenModel(
    appScreenModel: AppScreenModel
) : DataScreenModel<Sector, Path>(
    appScreenModel,
    parentQuery = { id -> database.sectorQueries.get(id).executeAsOneOrNull()?.let { Sector(it) } },
    childrenQuery = { id -> database.pathQueries.getAllByParent(id).executeAsList().map { Path(it) } }
)
