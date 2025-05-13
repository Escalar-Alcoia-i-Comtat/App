package org.escalaralcoiaicomtat.app.sync

import org.escalaralcoiaicomtat.app.data.DataTypes

expect object SyncManager {
    fun schedule()

    fun run(cause: SyncProcess.Cause, syncId: Pair<DataTypes<*>, Int>? = null)
}
