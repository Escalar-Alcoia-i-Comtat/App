import cache.StorageProvider
import cache.storageProvider
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import database.setRoomDatabaseBuilder
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import push.PushNotifications

fun debugBuild() {
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    // Initialize the database
    setRoomDatabaseBuilder()

    // Configure Push Notifications
    PushNotifications.initialize(
        NotificationPlatformConfiguration.Ios(
            showPushNotification = false,
            askNotificationPermissionOnStart = false,
        )
    )
}
