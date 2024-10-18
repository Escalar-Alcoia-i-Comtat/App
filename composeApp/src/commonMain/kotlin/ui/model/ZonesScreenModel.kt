package ui.model

import cache.DataCache
import data.Area
import data.Zone

class ZonesScreenModel : DataScreenModel<Area, Zone>(
    parentCache = DataCache.Areas,
    childrenCache = DataCache.Zones,
)
