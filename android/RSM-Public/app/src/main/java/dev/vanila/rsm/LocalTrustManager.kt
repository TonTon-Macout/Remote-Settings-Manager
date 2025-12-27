package dev.vanila.rsm

import NetworkAnalyzer
import android.content.Context
import android.net.wifi.ScanResult
import android.util.Log
import dev.vanila.rsm.NetworkManager.isSplitNetworkEnabled
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object LocalTrustManager {
    private fun getNetworkAnalyzer(context: Context): NetworkAnalyzer {
        // Приводим applicationContext к типу App, чтобы получить доступ к его свойствам
        return (context.applicationContext as App).networkAnalyzer
    }


    private val unsafeTrustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
    }

    private val unsafeFactory = SSLContext.getInstance("TLS").apply {
        init(null, arrayOf<TrustManager>(unsafeTrustManager), null)
    }.socketFactory

    private val unsafeVerifier = javax.net.ssl.HostnameVerifier { _, _ -> true }

    // Паттерны для определения локальных IP-адресов
    private val LOCAL_PATTERNS = listOf(
        Regex("^10\\..*"),
        Regex("^192\\.168\\..*"),
        Regex("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*"),
        Regex("^127\\..*"),
        Regex("^169\\.254\\..*"),
        Regex("^fc[0-9a-fA-F]{2}:.*"),
        Regex("^fd[0-9a-fA-F]{2}:.*"),
        Regex("^::1$"),
        Regex("^fe80::.*")
    )

    @Deprecated(
        message = "не очень надежная версия",
        replaceWith = ReplaceWith("isLocal(hostOrIp)")
    )
    private fun isLocalOLD(hostOrIp: String): Boolean {
        return LOCAL_PATTERNS.any { it.matches(hostOrIp) }
    }


    /**
     * Определяет, является ли хост/IP действительно локальным.
     * Поддерживает:
     * - Приватные IPv4 (10.*, 192.168.*, 172.16-31.*)
     * - Loopback (127.*, ::1)
     * - IPv6 link-local (fe80::)
     * - mDNS-имена (.local)
     * - localhost
     *
     * Не считается локальным:
     * - Публичные IP
     * - Обычные домены (.com, .ru и т.д.)
     */
    private fun isLocal(hostOrIp: String): Boolean {
        val trimmed = hostOrIp.trim().lowercase()

        // 1. Явные локальные имена
        if (trimmed == "localhost" || trimmed.endsWith(".local")) {
            return true
        }

        // 2. Проверка как IP-адрес
        try {
            val inet = InetAddress.getByName(trimmed)
            return inet.isSiteLocalAddress ||
                    inet.isLoopbackAddress ||
                    inet.isLinkLocalAddress  // дополнительно для IPv6
        } catch (e: UnknownHostException) {
            // Не удалось распарсить как IP — точно не локальный IP
            return false
        } catch (e: Exception) {
            // Любая другая ошибка — на всякий случай считаем нелокальным
            return false
        }

        // По умолчанию — нелокальный (на всякий случай)
        // return false
    }


    fun applyToConnectionIfTrusted(conn: HttpURLConnection, context: Context) {
        if (conn !is HttpsURLConnection) return

        // 1. Если разделение сетей отключено, ничего не делаем.
        if (!SettingsManager.isSplitNetworkEnabled) {
            Log.v("LocalTrustManager", "Разделение сетей отключено, доверие не применяется.")
            return
        }

        val networkAnalyzer = getNetworkAnalyzer(context)

        val (ssid, bssid, subnet) = networkAnalyzer.getNetworkInfo()
        val fingerprint =
            if(ssid != null && bssid != null && subnet != null)   FingerprintGenerator.generate(ssid, bssid, subnet)
            else {
                App.ALL_NETWORKS_FINGERPRINT
            }
        val knownNetwork = NetworkManager.findNetworkByFingerprint(context, fingerprint)
        Log.v("LocalTrustManager", "=== Проверка доверя сети ===\n" +
                "      fingerprint: $fingerprint\n" +
                "      knownNetwork: $knownNetwork\n")


        //  URL локальный И сеть найдена и помечена как доверенная.
        val isUrlLocal = isLocal(conn.url.host)
        val isNetworkTrusted = knownNetwork?.isTrusted == true

        Log.v("LocalTrustManager", "      URL локальный? $isUrlLocal\n      Сеть доверенная? $isNetworkTrusted")

        // Применяем "небезопасные" настройки, ТОЛЬКО ЕСЛИ ОБА УСЛОВИЯ ВЫПОЛНЕНЫ
        if (isUrlLocal && isNetworkTrusted) {
            Log.e("LocalTrustManager", "!!! ДОВЕРЯЕМ СЕТИ ${knownNetwork?.name} ДЛЯ URL : ${conn.url} !!!")
            conn.sslSocketFactory = unsafeFactory
            conn.hostnameVerifier = unsafeVerifier
        }
        else {
            Log.v("LocalTrustManager", "^^^ НЕ ДОВЕРЯЕМ СЕТИ ДЛЯ URL : ${conn.url} ^^^")
        }
    }
    fun applyToConnectionIfTrusted(conn: HttpURLConnection, context: Context, currentScanResult: ScanResult?) {
        if (conn !is HttpsURLConnection) return

        // 1. Если разделение сетей отключено, ничего не делаем.
        if (SettingsManager.isSplitNetworkEnabled) {
            Log.v("LocalTrustManager", "Разделение сетей отключено, доверие не применяется.")
            return
        }

        // 2. Получаем полный ScanResult текущей сети.
        // Приводим context к DeviceScanActivity, чтобы вызвать его публичный метод.
        if (currentScanResult == null) {
            Log.v("LocalTrustManager", "Не удалось получить информацию о текущей сети.")
            return
        }

        // 3. Используем НОВЫЙ метод findNetwork, чтобы найти сеть по отпечатку.
        val knownNetwork = NetworkManager.findNetwork(context, currentScanResult)

        // 4. Проверяем два условия: URL локальный И сеть найдена и помечена как доверенная.
        val isUrlLocal = isLocal(conn.url.host)
        val isNetworkTrusted = knownNetwork?.isTrusted == true

        Log.v("LocalTrustManager", "Проверка доверия: URL локальный? $isUrlLocal, Сеть доверенная? $isNetworkTrusted")

        // Применяем "небезопасные" настройки, ТОЛЬКО ЕСЛИ ОБА УСЛОВИЯ ВЫПОЛНЕНЫ
        if (isUrlLocal && isNetworkTrusted) {
            Log.v("LocalTrustManager", "ПРИМЕНЯЕМ ДОВЕРИЕ для URL: ${conn.url}")
            conn.sslSocketFactory = unsafeFactory
            conn.hostnameVerifier = unsafeVerifier
        }
    }





}