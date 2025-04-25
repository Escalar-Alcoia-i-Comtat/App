package org.escalaralcoiaicomtat.app.ui.model

import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.database.DatabaseInterface

class ZonesScreenModel : DataScreenModel<Area, Zone>(
    childrenListAccessor = { parentId -> DatabaseInterface.zones().getByParentId(parentId) },
    parentListAccessor = { id -> DatabaseInterface.areas().get(id) }
)
