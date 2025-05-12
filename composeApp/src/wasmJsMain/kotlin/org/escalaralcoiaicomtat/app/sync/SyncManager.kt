package org.escalaralcoiaicomtat.app.sync

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.utils.IO
import org.escalaralcoiaicomtat.app.utils.runBlocking
import kotlin.js.Promise

actual object SyncManager {
    actual fun schedule() {

    }

    actual fun run(cause: DataSync.Cause, syncId: Pair<DataTypes<*>, Int>?) {
        Promise<JsAny?> { onSuccess, onFailure ->
            runBlocking {
                try {
                    DataSync.start(cause, syncId)
                    onSuccess(null)
                } catch (e: JsException) {
                    Napier.e(e) { "There was an error while running synchronization." }
                    onFailure(e.thrownValue!!)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            DataSync.start(cause, syncId)
        }
    }
}
