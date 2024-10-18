package ui.model

import cache.DataCache
import data.Sector
import data.Zone

class SectorsScreenModel : DataScreenModel<Zone, Sector>(
    parentCache = DataCache.Zones,
    childrenCache = DataCache.Sectors,
)
