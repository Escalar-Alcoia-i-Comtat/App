package ui.model

import data.Sector
import data.Zone
import database.database

class SectorsScreenModel : DataScreenModel<Zone, Sector>(
    parentQuery = { id -> database.zoneQueries.get(id).executeAsOneOrNull()?.let { Zone(it) } },
    childrenQuery = { id -> database.sectorQueries.getAllByParent(id).executeAsList().map { Sector(it) } }
)
