package org.escalaralcoiaicomtat.app.ui.model

import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.database.DatabaseInterface

class PathsScreenModel : DataScreenModel<Sector, Path>(
    childrenListAccessor = { parentId -> DatabaseInterface.paths().getByParentId(parentId) },
    parentListAccessor = { id -> DatabaseInterface.sectors().get(id) }
)
