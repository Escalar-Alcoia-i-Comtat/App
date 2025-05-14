package org.escalaralcoiaicomtat.app.database

import org.escalaralcoiaicomtat.app.data.Blocking

interface BlockingInterface : EntityInterface<Blocking> {
    /**
     * Gets all the [Blocking]s from its parent path.
     * May throw [UnsupportedOperationException] if the item does not have a parent.
     */
    suspend fun getByPathId(parentId: Long): List<Blocking>
}
