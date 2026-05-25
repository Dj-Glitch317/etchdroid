package eu.depau.etchdroid

import android.app.Application
import android.os.Build
import android.os.StrictMode
import eu.depau.etchdroid.plugins.telemetry.Telemetry

class EtchDroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Telemetry.init(this)

        // Enable strict-mode in debug builds to catch issues early
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
        }
    }
}
