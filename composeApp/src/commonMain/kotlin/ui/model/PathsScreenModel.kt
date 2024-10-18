package ui.model

import cache.DataCache
import data.Path
import data.Sector

class PathsScreenModel : DataScreenModel<Sector, Path>(
    parentCache = DataCache.Sectors,
    childrenCache = DataCache.Paths
)
