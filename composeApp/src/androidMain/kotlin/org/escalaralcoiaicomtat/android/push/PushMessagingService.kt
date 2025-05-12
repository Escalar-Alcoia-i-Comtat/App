package org.escalaralcoiaicomtat.android.push

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.sync.DataSync

class PushMessagingService: FirebaseMessagingService() {
    companion object {
        private const val TOPIC_CREATED = "created"
        private const val TOPIC_UPDATED = "updated"
        private const val TOPIC_DELETED = "deleted"

        private const val DATA_TYPE = "type"
        private const val DATA_ID = "id"

        private const val TYPE_AREA = "AREA"
        private const val TYPE_ZONE = "ZONE"
        private const val TYPE_SECTOR = "SECTOR"
        private const val TYPE_PATH = "PATH"
    }

    override fun onCreate() {
        super.onCreate()

        val messaging = Firebase.messaging
        messaging
            .subscribeToTopic(TOPIC_CREATED)
            .addOnFailureListener { Napier.e(it) { "Could not subscribe to topic: $TOPIC_CREATED" } }
            .continueWithTask { messaging.subscribeToTopic(TOPIC_UPDATED) }
            .addOnFailureListener { Napier.e(it) { "Could not subscribe to topic: $TOPIC_CREATED" } }
            .continueWithTask { messaging.subscribeToTopic(TOPIC_DELETED) }
            .addOnFailureListener { Napier.e(it) { "Could not subscribe to topic: $TOPIC_CREATED" } }
            .addOnSuccessListener { Napier.i { "Subscribed to topics." } }
    }

    override fun onNewToken(token: String) {
        Napier.i { "Got new push token: $token" }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val type = data[DATA_TYPE] ?: return
        val idStr = data[DATA_ID] ?: return
        val id = idStr.toIntOrNull() ?: return
        val dataType = when (type) {
            TYPE_AREA -> DataTypes.Area
            TYPE_ZONE -> DataTypes.Zone
            TYPE_SECTOR -> DataTypes.Sector
            TYPE_PATH -> DataTypes.Path
            else -> return Napier.e { "Received a push notification with an unknown type: $type" }
        }
        CoroutineScope(Dispatchers.IO).launch {
            Napier.i { "Scheduling sync for $dataType..." }
            DataSync.start(DataSync.Cause.Push, dataType to id)
        }
    }
}
