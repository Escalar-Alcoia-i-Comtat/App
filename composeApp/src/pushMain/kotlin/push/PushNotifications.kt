package push

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking
import network.Backend

object PushNotifications {
    private const val TOPIC_CREATED = "created"
    private const val TOPIC_UPDATED = "updated"
    private const val TOPIC_DELETED = "deleted"

    private const val DATA_TYPE = "type"
    private const val DATA_ID = "id"

    private const val TYPE_AREA = "AREA"
    private const val TYPE_ZONE = "ZONE"
    private const val TYPE_SECTOR = "SECTOR"
    private const val TYPE_PATH = "PATH"

    fun initialize(configuration: NotificationPlatformConfiguration) {
        NotifierManager.initialize(configuration)
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                Napier.i { "Got new push token: $token" }
            }

            override fun onPayloadData(data: PayloadData) {
                val type = data[DATA_TYPE] as? String ?: return
                val idStr = data[DATA_ID] as? String ?: return
                val id = idStr.toIntOrNull() ?: return
                when (type) {
                    TYPE_AREA -> Backend.area(id)
                }
            }
        })
        NotifierManager.getPushNotifier().apply {
            runBlocking {
                subscribeToTopic(TOPIC_CREATED)
                subscribeToTopic(TOPIC_UPDATED)
                subscribeToTopic(TOPIC_DELETED)
            }
        }
    }
}
