package ui.model

import data.Area
import data.Zone
import database.database

class ZonesScreenModel(
    appScreenModel: AppScreenModel
) : DataScreenModel<Area, Zone>(
    appScreenModel,
    parentQuery = { id -> database.areaQueries.get(id).executeAsOneOrNull()?.let { Area(it) } },
    childrenQuery = { id -> database.zoneQueries.getAllByParent(id).executeAsList().map { Zone(it) } }
)
