package org.escalaralcoiaicomtat.app

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.database.setRoomDatabaseBuilder
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
