package eu.depau.etchdroid.utils.ktexts

import android.app.Notification
import android.app.Service
import android.content.pm.ServiceInfo
import android.os.Build

fun Service.startForegroundSpecialUse(id: Int, notification: Notification) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        startForeground(id, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // API 29-33: use regular foreground with no type restriction
        startForeground(id, notification)
    } else {
        @Suppress("DEPRECATION")
        startForeground(id, notification)
    }
}
