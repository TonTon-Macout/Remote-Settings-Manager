package dev.vanila.rsm

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.ScanResult
import android.os.Build
import android.util.Log

import androidx.core.content.edit

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.vanila.rsm.FingerprintGenerator.extractBssidPrefix
import java.util.UUID



/**
 * Хранит отпечаток и статус доверия для конкретной сети.
 * @param id            Уникальный ID для управления записью.
 * @param fingerprint   Сам отпечаток, сгенерированный на основе стабильных параметров.
 * @param ssid          Имя для отображения (SSID).
 * @param name    пользовательское Имя сети
 * @param isTrusted     Доверяет ли пользователь этой сети. true - доверяет, false - не доверяет.
 * @param subnet        Подсеть сети (например, 192.168.1.0/24), если она определена.
 * @param mainBssidPrefix   Префикс основного BSSID (первые 5 октетов).
 * @param additionalBssids Дополнительные префиксы BSSID (первые 5 октетов).
 * @param lastDevice  объект Device последнего устройства
 */
data class KnownNetwork(
    var id: String = UUID.randomUUID().toString(),
    var fingerprint: String,                // Основной fingerprint (с основным BSSID)
    var ssid: String,
    var name: String,
    var isTrusted: Boolean,
    var subnet: String? = null,
    var mainBssidPrefix: String,            // Префикс основного BSSID (первые 5)
    var additionalBssids: MutableList<String> = mutableListOf(),  // Дополнительные префиксы BSSID
    var lastDevice: Device? = null
)


// --- Генератор отпечатков ---
object FingerprintGenerator {

    /**
     * Генерирует отпечаток сети на основе SSID, BSSID и подсети.
     * @param ssid Имя сети (SSID).
     * @param bssid MAC-адрес точки доступа (BSSID).
     * @param subnet Подсеть сети (например, "192.168.1.0/24").
     * @return Строка-отпечаток.
     */
    fun generate(ssid: String, bssid: String, subnet: String?): String {
        val components = mutableListOf<String>()
        val bssidPrefix = bssid.split(':').take(5).joinToString(":")

        val cellularBssid = extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)
        Log.v("FingerprintGenerator", "generate: ssid - $ssid | bssid - $bssid | subnet - $subnet || cellularBssid - $cellularBssid" )

        if(bssid == cellularBssid){
            components.add("ssid=$ssid")
            components.add("bssid_prefix=$bssidPrefix")
            //return components.joinToString(separator = ";")
            val newFingerprint = components.joinToString(separator = ";")
            Log.v("FingerprintGenerator", "generate: генерация отпечатка для сотовой сети: newFingerprint - $newFingerprint")

            return newFingerprint
        }


        components.add("ssid=$ssid")
        components.add("bssid_prefix=$bssidPrefix")
        subnet.let { components.add("subnet=$it") }

        //return components.joinToString(separator = ";")
        val newFingerprint = components.joinToString(separator = ";")
        Log.v("FingerprintGenerator", "generate: генерация отпечатка для wifi сети: newFingerprint - $newFingerprint")

        return newFingerprint
    }
    /**
     * Генерирует отпечаток сети на основе объекта ScanResult.
     * @param scanResult Результат сканирования сети.
     * @return Строка-отпечаток.
     */
    fun generate(context: Context, scanResult: ScanResult): String {
        val components = mutableListOf<String>()
        val cellularPrefixBssid = extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)
        Log.v("FingerprintGenerator", "generate from scanResult: scanResult.BSSID - ${scanResult.BSSID} | cellularBssid - $cellularPrefixBssid")
        val currentPrefixBssid = getBssidPrefix(scanResult)
        val currentSsid = getSsid(scanResult)

        if(currentPrefixBssid == cellularPrefixBssid){

            components.add(currentSsid)
            components.add("bssid_prefix=${currentPrefixBssid}")

            val newFingerprint = components.joinToString(separator = ";")
            Log.v("FingerprintGenerator", "generate from scanResult: генерация отпечатка для сотовой сети: newFingerprint - $newFingerprint")

            return newFingerprint
        }


        components.add(currentSsid)
        components.add("bssid_prefix=${currentPrefixBssid}")
        getNetworkSubnet(context)?.let { components.add(it) }

        val newFingerprint = components.joinToString(separator = ";")
        Log.v("FingerprintGenerator", "generate from scanResult: генерация отпечатка для wifi сети: newFingerprint - $newFingerprint")

        return newFingerprint
    }

    private fun getSsid(scanResult: ScanResult): String = "ssid=${scanResult.getSsidString()}"

    private fun ScanResult.getSsidString(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && this.wifiSsid != null) {
            // для Android 13+
            this.wifiSsid.toString().removeSurrounding("\"")
        } else {
            //  для старых версий
            @Suppress("DEPRECATION")
            this.SSID
        }
    }

    /**
     * Получить мак префикс из объекта сканрезульт
     * @param scanResult объект сканРезульт
     * @return String bssidPrefix - первые 5 октетов
     */
    fun getBssidPrefix(scanResult: ScanResult): String {
        val bssidPrefix = scanResult.BSSID.split(':').take(5).joinToString(":")
        Log.v("FingerprintGenerator", "getBssidPrefix: из BSSID '${scanResult.BSSID}' получен префикс: '$bssidPrefix'")
        return bssidPrefix
    }
    /**
     * Получить мак префикс из мак адреса (bssid) - обрезать мак адрес
     * @param bssid мак адрес (bssid)
     * @return String bssidPrefix - первые 5 октетов
     */
    fun getBssidPrefix(bssid: String?): String? {
        if (bssid == null) return null

        val bssidPrefix = bssid.split(':').take(5).joinToString(":")
        Log.v("FingerprintGenerator", "getBssidPrefix: из BSSID '${bssid}' получен префикс: '$bssidPrefix'")
        return bssidPrefix
    }

    // извлечь BSSID prefix из fingerprint строки
    fun extractBssidPrefix(fingerprint: String): String? {
        val bssidPrefix = fingerprint.split(";")
            .map { it.trim() } // Добавим обрезку пробелов для надежности
            .find { it.startsWith("bssid_prefix=") }
            ?.removePrefix("bssid_prefix=")

        Log.v("FingerprintGenerator", "extractBssidPrefix: из отпечатка '$fingerprint' извлечен BSSID-префикс: '$bssidPrefix'")

        return bssidPrefix
    }

    // извлечь SSID из fingerprint
    fun extractSsid(fingerprint: String): String? {
        val ssid = fingerprint.split(";")
            .find { it.startsWith("ssid=") }
            ?.removePrefix("ssid=")

        Log.v("FingerprintGenerator", "extractSsid: из отпечатка '$fingerprint' извлечен SSID: '$ssid'")

        return ssid
    }

    // извлечь subnet из fingerprint
    fun extractSubnet(fingerprint: String): String? {
        return fingerprint.split(";")
            .find { it.startsWith("subnet=") }
            ?.removePrefix("subnet=")
    }

    private fun getCapabilities(scanResult: ScanResult): String = "caps=${scanResult.capabilities}"
    fun getWifiStandard(scanResult: ScanResult): String {
        val standard =
            scanResult.wifiStandard.toString()
        return "std=$standard"
    }
    fun getNetworkSubnet(context: Context): String? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
        if (connectivityManager == null) {
            Log.e("FingerprintGenerator", "ConnectivityManager is null")
            return null
        }

        // Получаем активную сеть
        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork == null) {
            Log.e("FingerprintGenerator", "Active network is null")
            return null
        }

        // Получаем свойства сети, включая IP-адреса
        val linkProperties = connectivityManager.getLinkProperties(activeNetwork)
        if (linkProperties == null) {
            Log.e("FingerprintGenerator", "LinkProperties is null")
            return null
        }

        // Ищем первый IPv4 адрес
        for (linkAddress in linkProperties.linkAddresses) {
            val ipAddress = linkAddress.address
            // Убеждаемся, что это IPv4
            if (ipAddress is java.net.Inet4Address) {
                try {
                    val prefixLength = linkAddress.prefixLength
                    val ipBytes = ipAddress.address

                    // Создаем маску подсети
                    val mask = -1 shl (32 - prefixLength)

                    // Применяем маску к IP-адресу, чтобы получить адрес сети
                    val networkIpBytes = byteArrayOf(
                        (ipBytes[0].toInt() and (mask shr 24)).toByte(),
                        (ipBytes[1].toInt() and (mask shr 16)).toByte(),
                        (ipBytes[2].toInt() and (mask shr 8)).toByte(),
                        (ipBytes[3].toInt() and mask).toByte()
                    )

                    val subnetAddress = java.net.InetAddress.getByAddress(networkIpBytes)

                    return "subnet=${subnetAddress.hostAddress}/$prefixLength"

                } catch (e: Exception) {
                    Log.e("FingerprintGenerator", "Error calculating subnet", e)
                    return null
                }
            }
        }

        Log.e("FingerprintGenerator", "No IPv4 address found")
        return null
    }

}



object NetworkManager {

    private const val PREFS_NAME = "known_networks_prefs"
    private const val NETWORKS_LIST_KEY = "known_networks"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun loadNetworks(context: Context): MutableList<KnownNetwork> {
        val json = getPrefs(context).getString(NETWORKS_LIST_KEY, null)
        if (json.isNullOrBlank()) return mutableListOf()
        val type = object : TypeToken<MutableList<KnownNetwork>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveNetworks(context: Context, networks: List<KnownNetwork>) {
        val json = gson.toJson(networks)
        getPrefs(context).edit { putString(NETWORKS_LIST_KEY, json) }
    }




    //===============================//

    @Deprecated(
        "Настройки теперь управляются через SettingsManager",
        ReplaceWith("SettingsManager.isSplitNetworkEnabled")
    )
    fun isSplitNetworkEnabled(context: Context): Boolean {
        return SettingsManager.isSplitNetworkEnabled
    }

    @Deprecated(
        "Настройки теперь управляются через SettingsManager",
        ReplaceWith("SettingsManager.isSplitNetworkEnabled = isEnabled")
    )
    fun setSplitNetworkEnabled(context: Context, isEnabled: Boolean) {
        SettingsManager.isSplitNetworkEnabled = isEnabled
    }

    @Deprecated(
        "Настройки теперь управляются через SettingsManager",
        ReplaceWith("SettingsManager.isSplitNetworkManualEnabled")
    )
    fun isSplitNetworkManual(context: Context): Boolean {
        return  SettingsManager.isSplitNetworkManualEnabled
    }

    @Deprecated(
        "Настройки теперь управляются через SettingsManager",
        ReplaceWith("SettingsManager.isSplitNetworkManualEnabled = isEnabled")
    )
    fun setSplitNetworkManual(context: Context, isEnabled: Boolean) {
        SettingsManager.isSplitNetworkManualEnabled = isEnabled
    }

    @Deprecated(
        "Настройки теперь управляются через SettingsManager",
        ReplaceWith("SettingsManager.lastNetwork")
    )
    fun getLastNetworkId(context: Context): String? {
        return SettingsManager.lastNetwork
    }

    @Deprecated(
        "Настройки теперь управляются через SettingsManager",
        ReplaceWith("SettingsManager.lastNetwork = id")
    )
    fun setLastNetworkId(context: Context, id: String) {
        SettingsManager.lastNetwork = id
    }

    //===============================//

    /**
     * Возвращает полный список всех известных сетей.
     * @param context Контекст приложения, необходимый для доступа к SharedPreferences.
     * @return Список [List] объектов [KnownNetwork].
     */
    fun getKnownNetworks(context: Context): List<KnownNetwork> {
        return loadNetworks(context)
    }

    /**
     * Идентифицирует текущую сеть на основе "сырых" данных от системы и ищет ее в списке известных сетей.
     * Эта функция генерирует отпечаток из ScanResult и затем выполняет поиск по нему.
     *
     * @param context Контекст приложения.
     * @param currentApScanResult Объект [ScanResult], представляющий текущую точку доступа.
     * @return Объект [KnownNetwork], если сеть найдена в базе, иначе null.
     */
    fun findNetwork(context: Context, currentApScanResult: ScanResult?): KnownNetwork? {
        if (currentApScanResult == null) return null

        val currentFingerprint = FingerprintGenerator.generate(context, currentApScanResult)
        val currentBssidPrefix = FingerprintGenerator.getBssidPrefix(currentApScanResult)
        val currentSsid = currentApScanResult.getSsidString()
        val currentSubnet = FingerprintGenerator.getNetworkSubnet(context)

        val knownNetworks = loadNetworks(context)

        // ищем совпадение по полному fingerprint
        knownNetworks.find { it.fingerprint == currentFingerprint }?.let { return it }

        // Ищем по основному или дополнительному BSSID + совпадению SSID и subnet
        for (network in knownNetworks) {
            val networkSsid = FingerprintGenerator.extractSsid(network.fingerprint)
            val networkSubnet = network.subnet

            if (networkSsid == currentSsid && networkSubnet == currentSubnet?.removePrefix("subnet=")) {
                // Совпадает SSID и подсеть — проверяем BSSID
                if (network.mainBssidPrefix == currentBssidPrefix ||
                    network.additionalBssids.contains(currentBssidPrefix)) {
                    return network
                }
            }
        }


        val cellularCandidat = findNetworkByBssidPrefix(context, currentBssidPrefix)
        val cellularBssidPrefix = FingerprintGenerator.extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)
        if(cellularCandidat != null && cellularCandidat.mainBssidPrefix == cellularBssidPrefix){
            return cellularCandidat
        }

        return null
    }
    private fun ScanResult.getSsidString(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && this.wifiSsid != null) {
            // для Android 13+
            this.wifiSsid.toString().removeSurrounding("\"")
        } else {
            //  для старых версий
            @Suppress("DEPRECATION")
            this.SSID
        }
    }
    /**
     * Находит известную сеть по её отпечатку.
     * @param context Контекст приложения.
     * @param fingerprint Отпечаток для поиска.
     * @return Объект KnownNetwork или null, если сеть не найдена.
     *
     * Так же ищет по дополнительным точкам,
     * если additionalBssids точки есть и ssid и subnet сети совпадают с отпечатком то вернет сеть иначе null
     */
    fun findNetworkByFingerprint(context: Context, fingerprint: String): KnownNetwork? {

        val networks = loadNetworks(context)

        val bssidPrefix = FingerprintGenerator.extractBssidPrefix(fingerprint)
        val subnet = FingerprintGenerator.extractSubnet(fingerprint)
        val ssid = FingerprintGenerator.extractSsid(fingerprint)

        val cellularBssidPrefix = FingerprintGenerator.extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)
        val cellularCandidat = bssidPrefix?.let { findNetworkByBssidPrefix(context, it) }

        if(cellularCandidat != null && cellularCandidat.mainBssidPrefix == cellularBssidPrefix){
            return cellularCandidat
        }

        var network = networks.find { it.fingerprint == fingerprint }

        if(network == null) {
            network = networks.find {
                it.mainBssidPrefix == bssidPrefix || it.additionalBssids.contains(bssidPrefix)
            }
            if (subnet != network?.subnet && ssid != network?.ssid){
                network = null
            }
        }

        return network
    }

    /**
     * Находит сеть по префиксу BSSID (первые 5 октетов).
     * как основной, так и дополнительные BSSID.
     *
     * ищет только по BSSID, не учитывая ни ssid ни subnet
     *
     * @param context Контекст приложения.
     * @param bssidPrefix Префикс BSSID для поиска (например, "0a:1b:2c:3d:4e").
     * @return Объект KnownNetwork или null, если сеть не найдена.
     */
    fun findNetworkByBssidPrefix(context: Context, bssidPrefix: String?): KnownNetwork? {
        if (bssidPrefix.isNullOrBlank()) return null

        val networks = loadNetworks(context)
        return networks.find {
            it.mainBssidPrefix == bssidPrefix || it.additionalBssids.contains(bssidPrefix)
        }
    }

    /**
     * Находит ID сети по её отпечатку.
     * @param context Контекст приложения.
     * @param fingerprint Отпечаток для поиска.
     * @return Уникальный ID сети (String) или null, если сеть с таким отпечатком не найдена.
     */
    fun getIdByFingerprint(context: Context, fingerprint: String): String? {
        val networks = loadNetworks(context)
        return networks.find { it.fingerprint == fingerprint }?.id
    }

    /**
     * Находит отпечаток сети по её ID.
     * @param context Контекст приложения.
     * @param id Уникальный ID для поиска.
     * @return Отпечаток сети (String) или null, если сеть с таким ID не найдена.
     */
    fun getFingerprintById(context: Context, id: String): String? {
        val networks = loadNetworks(context)
        return networks.find { it.id == id }?.id
    }

    /**
     * Находит известную сеть по её уникальному ID.
     * @param context Контекст приложения.
     * @param id Уникальный ID для поиска.
     * @return Объект KnownNetwork или null, если сеть не найдена.
     */
    fun findNetworkById(context: Context, id: String): KnownNetwork? {
        val networks = loadNetworks(context)
        return networks.find { it.id == id }
    }

    //===============================//


    /**
     * Добавляет новый BSSID как дополнительный в существующую сеть.
     * @return true если BSSID был добавлен
     */
    fun addBssidToNetwork(context: Context, networkId: String, newBssidPrefix: String): Boolean {
        val networks = loadNetworks(context).toMutableList()
        val network = networks.find { it.id == networkId } ?: return false

        if (network.mainBssidPrefix != newBssidPrefix &&
            !network.additionalBssids.contains(newBssidPrefix)) {

            network.additionalBssids.add(newBssidPrefix)
            saveNetworks(context, networks)  // ← ЭТО САМОЕ ГЛАВНОЕ!
            Log.v("NetworkManager", "Добавлен доп. BSSID $newBssidPrefix в сеть '${network.name}'")
            return true
        }
        return false
    }

    /**
     * Добавляет новую сеть вручную, без сканирования.
     * Отпечаток генерируется на основе имени, подсети и случайного BSSID.
     *
     * @param context Контекст приложения.
     * @param SSID Имя(SSID) для новой сети, если не указать nameNetwork, то имя и ssid будут одни и те же
     * @param isTrusted Доверяет ли пользователь этой сети.
     * @param subnet Подсеть в формате "192.168.1.0/24" (может быть null).
     * @param bssidPrefix префикс Мак адреса (первые 5 октетов) "06:00:06:00:06" (или null для рандомной генерации).
     * @param name Пользовательское ИМЯ для новой сети, если null то == @param name(SSID)
     * @return Созданный объект [KnownNetwork].
     */
    fun addManualNetwork(context: Context, ssid: String, isTrusted: Boolean, subnet: String?, bssidPrefix: String? = null, name: String? = null): KnownNetwork {

        // 1. Генерируем "фейковый" BSSID, чтобы отпечаток был уникальным
        //val randomBssid = (1..6).joinToString(":") {
        //    (0..255).random().toString(16).padStart(2, '0')
        //}
        val randomBssid = "99:" + (1..5).joinToString(":") {
            (0..255).random().toString(16).padStart(2, '0')
        }

        // Извлекаем префикс (первые 5 октетов) для mainBssidPrefix
        val mainBssidPrefix: String
        if (bssidPrefix != null) mainBssidPrefix = bssidPrefix
        else                     mainBssidPrefix = randomBssid.split(':').take(5).joinToString(":")

        // Формируем компоненты для отпечатка вручную
        val fingerprintComponents = mutableListOf<String>()
        fingerprintComponents.add("ssid=$ssid") //
        fingerprintComponents.add("bssid_prefix=$mainBssidPrefix") // префикс

        // Добавляем подсеть, если она была указана
        if (!subnet.isNullOrBlank()) {
            fingerprintComponents.add("subnet=$subnet")
        }

        // Соединяем компоненты в уникальный отпечаток
        val manualFingerprint = fingerprintComponents.joinToString(separator = ";")

        val nameNet = name ?: ssid
        // Создаем новый объект KnownNetwork
        val newManualNetwork = KnownNetwork(
            fingerprint = manualFingerprint,
            ssid = ssid,
            name = nameNet,
            isTrusted = isTrusted,
            subnet = subnet,
            mainBssidPrefix = mainBssidPrefix,
            // additionalBssids остаётся пустым по умолчанию (mutableListOf())
            lastDevice = null
        )

        // Сохраняем обновленный список сетей
        val networks = loadNetworks(context)
        networks.add(newManualNetwork)
        saveNetworks(context, networks)

        Log.v("NetworkManager", "Сеть добавлена вручную: ${newManualNetwork.name}, fingerprint: ${newManualNetwork.fingerprint}")

        // Возвращаем созданный объект
        return newManualNetwork
    }

    @Deprecated("ScanResult впал в немилость")
    /**
     * Сохраняет решение пользователя о сети.
     * Если сеть новая, создает запись. Если уже существует, обновляет ее статус доверия.
     * @param trust true если пользователь доверяет сети, false если нет.
     */
    fun processUserDecision(context: Context, currentApScanResult: ScanResult?, trust: Boolean) {
        if (currentApScanResult == null) return

        val networks = loadNetworks(context)
        val currentFingerprint = FingerprintGenerator.generate(context, currentApScanResult)
        val currentBssidPrefix = FingerprintGenerator.getBssidPrefix(currentApScanResult)

        val existingNetwork = networks.find { it.fingerprint == currentFingerprint }

        if (existingNetwork != null) {
            // Сеть уже известна, просто обновляем ее статус
            existingNetwork.isTrusted = trust
        } else {
            val subnet = if (isSplitNetworkEnabled(context)) {
                currentFingerprint.split(';').find { it.startsWith("subnet=") }?.removePrefix("subnet=")
            } else {
                null
            }
            val cellularBssid = extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)
            if(cellularBssid == currentBssidPrefix) {

                // Сеть новая, создаем новую запись
                val newNetwork = KnownNetwork(
                    fingerprint = currentFingerprint,
                    ssid = currentApScanResult.getSsidString(),
                    name = "Сотовая",
                    isTrusted = false,
                    subnet = null,
                    mainBssidPrefix = currentBssidPrefix,
                    // additionalBssids
                    lastDevice = null
                )
                networks.add(newNetwork)
                Log.v("NetworkManager", "processUserDecision: Сеть новая, но это сотовая сеть: fingerprint - $currentFingerprint | ssid - ${newNetwork.ssid} | subnet - ${newNetwork.subnet} | name - ${newNetwork.name}")
            }
            else {

                // Сеть новая, создаем новую запись
                val newNetwork = KnownNetwork(
                    fingerprint = currentFingerprint,
                    ssid = currentApScanResult.getSsidString(),
                    name = currentApScanResult.getSsidString(),
                    isTrusted = trust,
                    subnet = subnet,
                    mainBssidPrefix = currentBssidPrefix,
                    // additionalBssids
                    lastDevice = null
                )
                networks.add(newNetwork)

                Log.v("NetworkManager", "processUserDecision: Сеть новая: fingerprint - $currentFingerprint | ssid - ${newNetwork.ssid} | subnet - ${newNetwork.subnet} | isTrusted - ${newNetwork.isTrusted}")
            }

        }
        saveNetworks(context, networks)
    }



    /**
     * Сохраняет решение пользователя о сети на основе переданных данных (SSID, BSSID, подсеть).
     * Если сеть новая, создает запись. Если уже существует, обновляет ее статус доверия.
     * @param context Контекст приложения.
     * @param ssid Имя сети (SSID).
     * @param bssid MAC-адрес точки доступа (BSSID).
     * @param subnet Подсеть в формате "192.168.1.0/24". Может быть null.
     * @param trust true если пользователь доверяет сети, false если нет.
     */
    fun processUserDecision(context: Context, ssid: String, bssid: String, subnet: String?, trust: Boolean, name: String?=null)  {
        val networks = loadNetworks(context)

        // Генерируем отпечаток и префикс на основе переданных данных
        val currentFingerprint = FingerprintGenerator.generate(ssid, bssid, subnet)
        val currentBssidPrefix = FingerprintGenerator.getBssidPrefix(bssid)

        if (currentBssidPrefix == null) {
            Log.e("processUserDecision", "!!! Ошибка: currentBssidPrefix == null")
            return
        }

        val existingNetwork = networks.find { it.fingerprint == currentFingerprint }

        if (existingNetwork != null) {
            // Сеть уже известна, просто обновляем ее статус
            existingNetwork.isTrusted = trust
            Log.v("NetworkManager", "processUserDecisionByData: Сеть '${existingNetwork.name}' уже известна, статус доверия обновлен на $trust")
        } else {
            // Сеть новая, создаем новую запись
            val cellularBssid = extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)
            val newName = name ?: ssid

            if (cellularBssid == currentBssidPrefix) {
                // Это сотовая сеть
                val newNetwork = KnownNetwork(
                    fingerprint = currentFingerprint,
                    ssid = ssid,
                    name = newName,
                    isTrusted = false, // Сотовые сети по умолчанию не доверенные
                    subnet = null,
                    mainBssidPrefix = currentBssidPrefix,
                    lastDevice = null
                )
                networks.add(newNetwork)
                Log.v("NetworkManager", "processUserDecisionByData: Новая сотовая сеть: fingerprint - $currentFingerprint | ssid - ${newNetwork.ssid}")
            } else {
                // Это обычная Wi-Fi сеть
                val newNetwork = KnownNetwork(
                    fingerprint = currentFingerprint,
                    ssid = ssid,
                    name = newName,
                    isTrusted = trust,
                    subnet = subnet,
                    mainBssidPrefix = currentBssidPrefix,
                    lastDevice = null
                )
                networks.add(newNetwork)
                Log.v("NetworkManager", "processUserDecisionByData: Новая сеть: fingerprint - $currentFingerprint | ssid - ${newNetwork.ssid} | subnet - ${newNetwork.subnet} | isTrusted - ${newNetwork.isTrusted}")
            }
        }
        saveNetworks(context, networks)

    }







    @Deprecated("ScanResult впал в немилость")
    /**
     * Сохраняет решение пользователя и ВОЗВРАЩАЕТ объект сети.
     * Если сеть новая, создает и возвращает ее. Если уже существует, обновляет и возвращает ее.
     * @param trust true если пользователь доверяет сети, false если нет.
     * @return Объект KnownNetwork, который был создан или обновлен.
     */
    fun processUserDecisionAndGetResult(context: Context, currentApScanResult: ScanResult?, trust: Boolean): KnownNetwork? {
        if (currentApScanResult == null) return null

        val networks = loadNetworks(context)
        val currentFingerprint = FingerprintGenerator.generate(context, currentApScanResult)

        val existingNetwork = findNetworkByFingerprint(context, currentFingerprint)

        val resultNetwork: KnownNetwork

        if (existingNetwork != null) {
            // Сеть уже известна, просто обновляем ее статус и назначаем как результат
            existingNetwork.isTrusted = trust
            resultNetwork = existingNetwork
        } else {
            // Сеть новая, создаем новую запись
            val currentBssidPrefix = FingerprintGenerator.getBssidPrefix(currentApScanResult)

            val cellularBssidPrefix = extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)

            val subnet = if (isSplitNetworkEnabled(context) && currentBssidPrefix != cellularBssidPrefix) {
                currentFingerprint.split(';').find { it.startsWith("subnet=") }?.removePrefix("subnet=")
            } else {
                null
            }

            val newNetwork = KnownNetwork(
                fingerprint = currentFingerprint,
                ssid = currentApScanResult.getSsidString(),
                name = currentApScanResult.getSsidString(),
                isTrusted = trust,
                subnet = subnet,
                mainBssidPrefix = currentBssidPrefix,
                lastDevice = null
            )
            networks.add(newNetwork)
            resultNetwork = newNetwork // Назначаем новую сеть как результат
        }

        saveNetworks(context, networks) // Сохраняем изменения в любом случае
        return resultNetwork // Возвращаем результат
    }




    /**
     * Обновляет статус доверия для известной сети по ее ID.
     * @param context Контекст приложения.
     * @param networkId ID сети, для которой нужно обновить статус.
     * @param trust Новое значение статуса доверия (true или false).
     * @return true, если статус был изменен; false, если статус уже был таким и ничего не поменялось; null, если сеть не была найдена.
     */
    fun updateNetworkTrustStatus(context: Context, networkId: String, trust: Boolean): Boolean? {
        // 1. Загружаем текущий список сетей
        val networks = loadNetworks(context)

        // 2. Находим нужную сеть по ее уникальному ID
        val networkToUpdate = networks.find { it.id == networkId }

        // 3. Проверяем, найдена ли сеть
        if (networkToUpdate == null) {
            // Если сеть не найдена, возвращаем null
            Log.w("NetworkManager", "updateNetworkTrustStatus: Сеть с ID '$networkId' не найдена.")
            return null
        }

        // 4. Проверяем, нужно ли менять статус
        if (networkToUpdate.isTrusted == trust) {
            // Если текущий статус уже такой же, как новый, ничего не делаем и возвращаем false
            Log.v("NetworkManager", "updateNetworkTrustStatus: Статус сети '${networkToUpdate.name}' уже был isTrusted=$trust. Изменений нет.")
            return false
        }

        // 5. Если статус отличается, обновляем его
        networkToUpdate.isTrusted = trust
        Log.i("NetworkManager", "updateNetworkTrustStatus: Статус сети '${networkToUpdate.name}' изменен на isTrusted=$trust.")

        // 6. Сохраняем обновленный список сетей
        saveNetworks(context, networks)

        // 7. Возвращаем true, подтверждая, что изменение было внесено
        return true
    }






    /**
     * Полностью обновляет объект сети в хранилище.
     * @param context Контекст.
     * @param updatedNetwork Объект сети с новыми данными.
     */
    fun updateWholeNetwork(context: Context, updatedNetwork: KnownNetwork) {
        val networks = loadNetworks(context).toMutableList()
        val index = networks.indexOfFirst { it.id == updatedNetwork.id }
        if (index != -1) {
            networks[index] = updatedNetwork
            saveNetworks(context, networks)
        }
    }

    /**
     * Объединяет одну сеть (source) с другой (target).
     * BSSID префиксы из source добавляются в additionalBssids в target.
     * Сеть source удаляется.
     * @param context Контекст.
     * @param targetNetworkId ID сети, В которую объединяем.
     * @param sourceNetworkId ID сети, КОТОРУЮ объединяем (она будет удалена).
     * @return true в случае успеха, false если что-то пошло не так.
     */
    fun mergeNetworks(context: Context, targetNetworkId: String, sourceNetworkId: String): Boolean {
        val networks = loadNetworks(context).toMutableList()
        val targetNetwork = networks.find { it.id == targetNetworkId }
        val sourceNetwork = networks.find { it.id == sourceNetworkId }

        if (targetNetwork == null || sourceNetwork == null) {
            Log.e("NetworkManager", "mergeNetworks: Одна из сетей не найдена.")
            return false
        }

        // Собираем все BSSID из исходной сети в один список
        val bssidsToMerge = mutableListOf(sourceNetwork.mainBssidPrefix)
        bssidsToMerge.addAll(sourceNetwork.additionalBssids)

        // Добавляем их в целевую сеть, избегая дубликатов
        targetNetwork.additionalBssids.addAll(bssidsToMerge)
        // Финальная очистка от дублей
        targetNetwork.additionalBssids = targetNetwork.additionalBssids.toSet().toMutableList()

        // Удаляем исходную сеть из общего списка
        networks.remove(sourceNetwork)

        // Сохраняем обновленный список сетей
        saveNetworks(context, networks)
        Log.i("NetworkManager", "Сеть '${sourceNetwork.name}' успешно объединена с '${targetNetwork.name}'.")
        return true
    }







    /**
     * Обновляет пользовательское имя для известной сети.
     * @param context Контекст приложения.
     * @param networkId ID сети, которую нужно обновить.
     * @param newName Новое пользовательское имя.
     * @return true, если сеть была найдена и обновлена, иначе false.
     */
    fun updateNetworkName(context: Context, networkId: String, newName: String): Boolean {
        // Загружаем текущий список сетей
        val networks = loadNetworks(context)

        // Находим нужную сеть по ее уникальному ID
        val networkToUpdate = networks.find { it.id == networkId }

        if (networkToUpdate != null) {
            // Если нашли, меняем имя
            networkToUpdate.name = newName

            // Сохраняем обновленный список обратно в SharedPreferences
            saveNetworks(context, networks)

            // Возвращаем true, подтверждая успех операции
            return true
        }

        // Если сеть с таким ID не найдена, возвращаем false
        return false
    }



    /**
     * Обновляет запись о последнем использованном устройстве для сети, найденной по ее ID.
     * @param context Контекст приложения.
     * @param networkId Уникальный ID сети, для которой нужно обновить устройство.
     * @param device Объект [Device], который нужно сохранить как последний. Может быть null, чтобы очистить поле.
     * @return true, если сеть была найдена и обновлена, иначе false.
     */
    fun updateLastDeviceById(context: Context, networkId: String, device: Device?): Boolean {
        // Загружаем текущий список сетей
        val networks = loadNetworks(context)

        // Находим нужную сеть по ее уникальному ID
        val networkToUpdate = networks.find { it.id == networkId }

        if (networkToUpdate != null) {
            // Если нашли, обновляем поле lastDevice
            networkToUpdate.lastDevice = device
            Log.v("NetworkManager", "Обновлено последнее устройство для сети '${networkToUpdate.name}' (ID: $networkId)")

            // Сохраняем обновленный список обратно в SharedPreferences
            saveNetworks(context, networks)

            // Возвращаем true, подтверждая успех операции
            return true
        }

        Log.e("NetworkManager", "Не удалось обновить устройство: сеть с ID '$networkId' не найдена.")
        // Если сеть с таким ID не найдена, возвращаем false
        return false
    }
    /**
     * Получает последнее использованное устройство для сети, найденной по ее ID.
     * @param context Контекст приложения.
     * @param networkId Уникальный ID сети для поиска.
     * @return Объект [Device], если он был сохранен для этой сети, иначе null.
     */
    fun getLastDeviceById(context: Context, networkId: String): Device? {
        // Находим нужную сеть по ее уникальному ID, используя уже существующую функцию
        val network = findNetworkById(context, networkId)

        // Возвращаем поле lastDevice найденной сети.
        // Если сеть не найдена (network == null), результат будет null.
        return network?.lastDevice
    }


    /**
     * Обновляет запись о последнем использованном устройстве для сети, найденной по ее отпечатку.
     * @param context Контекст приложения.
     * @param fingerprint Отпечаток сети, для которой нужно обновить устройство.
     * @param device Объект [Device], который нужно сохранить как последний. Может быть null, чтобы очистить поле.
     * @return true, если сеть была найдена и обновлена, иначе false.
     */
    fun updateLastDeviceByFingerprint(context: Context, fingerprint: String, device: Device?): Boolean {
        val networks = loadNetworks(context)
        val networkToUpdate = networks.find { it.fingerprint == fingerprint }


        if (networkToUpdate != null) {
            // Если нашли, обновляем поле lastDevice
            networkToUpdate.lastDevice = device
            Log.v("NetworkManager", "Обновлено последнее устройство для сети '${networkToUpdate.name}' (Fingerprint: $fingerprint)")

            // Сохраняем обновленный список обратно в SharedPreferences
            saveNetworks(context, networks)

            // Возвращаем true, подтверждая успех операции
            return true
        }

        Log.e("NetworkManager", "Не удалось обновить устройство: сеть с отпечатком '$fingerprint' не найдена.")
        // Если сеть с таким отпечатком не найдена, возвращаем false
        return false
    }
    /**
     * Получает последнее использованное устройство для сети, найденной по ее отпечатку.
     * @param context Контекст приложения.
     * @param fingerprint Отпечаток сети для поиска.
     * @return Объект [Device], если он был сохранен для этой сети, иначе null.
     */
    fun getLastDeviceByFingerprint(context: Context, fingerprint: String): Device? {
        val network = findNetworkByFingerprint(context, fingerprint)
        return network?.lastDevice
    }









    /**
     * Удаляет указанную сеть из списка известных сетей.
     * @param context Контекст приложения для доступа к хранилищу.
     * @param network Объект [KnownNetwork], который необходимо удалить. Удаление происходит по ID сети.
     */
    fun removeNetwork(context: Context, network: KnownNetwork) {
        val networks = loadNetworks(context)
        // Удаляем все сети, у которых ID совпадает с ID переданной сети.
        // `removeAll` возвращает true, если список был изменен.
        if (networks.removeAll { it.id == network.id }) {
            // Сохраняем измененный список только если что-то было удалено.
            saveNetworks(context, networks)
        }
    }

    /**
     * Полностью очищает хранилище известных сетей.
     * @param context Контекст приложения для доступа к хранилищу.
     */
    fun clearAll(context: Context) {
        getPrefs(context).edit { clear() }
    }




}