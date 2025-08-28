package org.escalaralcoiaicomtat.app.network

import android.telephony.TelephonyManager
import org.escalaralcoiaicomtat.android.applicationContext

actual object PlatformCarrier {
    actual fun getCarrier(): String {
        val tm = applicationContext.getSystemService(TelephonyManager::class.java)
        return tm.networkOperatorName
    }
}
