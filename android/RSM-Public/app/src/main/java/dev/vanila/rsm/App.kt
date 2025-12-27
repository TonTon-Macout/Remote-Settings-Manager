package dev.vanila.rsm
import NetworkAnalyzer
import android.app.Application

/**

 */
class App : Application() {
    lateinit var networkAnalyzer: NetworkAnalyzer private set

    companion object {
        const val DEFAULT_NETWORK_FINGERPRINT = "ssid=default;bssid_prefix=06:00:06:00:06"
        const val ALL_NETWORKS_FINGERPRINT = "all_networks"
        const val NULL_NETWORKS_FINGERPRINT = "-----"
        const val CELLULAR_NETWORKS_FINGERPRINT = "ssid=cellular;bssid_prefix=07:00:07:00:07"



    }

    override fun onCreate() {
        super.onCreate()

        SettingsManager.init(applicationContext)
        networkAnalyzer = NetworkAnalyzer(applicationContext)
    }
}