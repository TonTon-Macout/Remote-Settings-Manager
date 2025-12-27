import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Parcel
import android.util.Log
import androidx.annotation.RequiresApi
import dev.vanila.rsm.App
import dev.vanila.rsm.NetworkManager
import dev.vanila.rsm.SettingsManager
import dev.vanila.rsm.showLocationDialog
import java.net.Inet4Address
import java.net.Inet6Address

class NetworkAnalyzer(private val context: Context) {

    val TAG = "NetworkAnalyzer"

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    data class NetworkInfo(
        val isConnected: Boolean,
        val hasInternet: Boolean,
        val activeNetworks: List<NetworkType>,
        val vpnStatus: VPNStatus,
        val networkDetails: List<NetworkDetail>,
        val ipv4Networks: List<SubnetInfo>,
        val ipv6Networks: List<SubnetInfo>,
        val timestamp: Long = System.currentTimeMillis(),

        val ssid: String?,      // Имя сети
        val bssid: String?       // MAC-адрес точки доступа, "02:00:00:00:00:00" - это вернет если андроид не нифика не дал


    )

    data class NetworkDetail(
        val network: Network?,
        val type: NetworkType,
        val isVPN: Boolean,
        val isMetered: Boolean,
        val hasInternet: Boolean,
        val linkProperties: LinkProperties?,
        val capabilities: NetworkCapabilities?,

        val ssid: String?,      // Имя сети
        val bssid: String?       // MAC-адрес точки доступа, "02:00:00:00:00:00" - это вернет если андроид не нифика не дал
    )

    data class SubnetInfo(
        val networkAddress: String,
        val prefixLength: Int,
        val totalAddresses: Long,
        val firstAddress: String,
        val lastAddress: String,
        val ipVersion: String
    )

    enum class NetworkType {
        WIFI, CELLULAR, VPN, ETHERNET, BLUETOOTH, UNKNOWN
    }

    enum class VPNStatus {
        ACTIVE, INACTIVE, MIXED
    }


    /**
     * Возвращает детали текущего Wi-Fi соединения, если оно активно.
     * @return NetworkDetail для Wi-Fi сети или null, если Wi-Fi не подключен.
     */
    fun getWifiDetails(): NetworkDetail? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getCompleteNetworkInfo().networkDetails.find { it.type == NetworkType.WIFI }
        } else {
            null
        }
    }

    fun getCompleteNetworkInfo(): NetworkInfo {
        val allNetworks = connectivityManager.allNetworks
        val networkDetails = mutableListOf<NetworkDetail>()
        val activeNetworks = mutableListOf<NetworkType>()
        var hasInternet = false
        var vpnCount = 0
        var totalNetworks = 0

        var ssid: String? = null
        var bssid: String? = null

        val ipv4Networks = mutableListOf<SubnetInfo>()
        val ipv6Networks = mutableListOf<SubnetInfo>()

        for (network in allNetworks) {
            val caps = connectivityManager.getNetworkCapabilities(network)
            val linkProperties = connectivityManager.getLinkProperties(network)

            if (caps != null) {
                val networkType = determineNetworkType(caps)

                // Если это Wi-Fi сеть, пытаемся извлечь дополнительную информацию
                if (networkType == NetworkType.WIFI) {
                    // transportInfo содержит специфичные для транспорта данные (для Wi-Fi это WifiInfo)
                    val wifiInfo = caps.transportInfo as? WifiInfo
                    if (wifiInfo != null) {
                        // SSID может содержать кавычки по краям, их нужно убрать
                        ssid = wifiInfo.ssid.removeSurrounding("\"")
                        bssid = wifiInfo.bssid
                    }
                }

                val isVPN = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                val isMetered = !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                val hasInternetAccess =
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)


                if (hasInternetAccess) hasInternet = true
                if (isVPN) vpnCount++

                val detail = NetworkDetail(
                    network = network,
                    type = networkType,
                    isVPN = isVPN,
                    isMetered = isMetered,
                    hasInternet = hasInternetAccess,
                    linkProperties = linkProperties,
                    capabilities = caps,
                    ssid = ssid,
                    bssid = bssid
                )

                networkDetails.add(detail)
                activeNetworks.add(networkType)
                totalNetworks++

                analyzeLinkProperties(linkProperties, ipv4Networks, ipv6Networks)
            }
        }

        //val vpnStatus = when {
        //    vpnCount == totalNetworks && totalNetworks > 0 -> VPNStatus.ACTIVE
        //    vpnCount > 0 -> VPNStatus.MIXED
        //    else -> VPNStatus.INACTIVE
        //}
        val vpnStatus =
            if (networkDetails.any { it.isVPN }) VPNStatus.ACTIVE
            else VPNStatus.INACTIVE


        val isConnected = totalNetworks > 0

        return NetworkInfo(
            isConnected = isConnected,
            hasInternet = hasInternet,
            activeNetworks = activeNetworks.distinct(),
            vpnStatus = vpnStatus,
            networkDetails = networkDetails,
            ipv4Networks = ipv4Networks,
            ipv6Networks = ipv6Networks,
            ssid = ssid,
            bssid = bssid
        )
    }

    /**
     * Синхронно получает BSSID (MAC-адрес) текущей Wi-Fi точки доступа.
     * Использует комбинацию WifiManager.connectionInfo и кэша scanResults для надежности.
     *
     * @return BSSID в виде строки (например, "0a:1b:2c:3d:4e:5f") или null, если получить не удалось.
     */
    @SuppressLint("MissingPermission")
    fun getBssid(): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager


        ///////////////////
        // Ищем активное Wi-Fi соединение
        val wifiNetwork = connectivityManager.allNetworks.find { network ->
            val caps = connectivityManager.getNetworkCapabilities(network)
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        }
        if (wifiNetwork != null) {
            val caps = connectivityManager.getNetworkCapabilities(wifiNetwork)
            // transportInfo содержит специфичные для транспорта данные (для Wi-Fi это WifiInfo)
            val wifiInfo = caps?.transportInfo as? WifiInfo
            val bssid = wifiInfo?.bssid
            // Отфильтровываем плейсхолдер, который система может возвращать
            if (bssid != null && bssid != "02:00:00:00:00:00") {
                Log.v(TAG, "connectivityManager, getBssid: нашли реальный макадрес: $bssid ")
                return bssid
            }
        }

        /////////////////////
        @Suppress("DEPRECATION")
        val bssidFromInfo = wifiManager.connectionInfo?.bssid
        if (bssidFromInfo != null && bssidFromInfo != "02:00:00:00:00:00") {
            Log.v(TAG, "wifiManager, getBssid: нашли реальный макадрес: $bssidFromInfo ")
            return bssidFromInfo
        } else {
            Log.e(TAG, "getBssid: неудалось найти мак адрес точки")
            return null
        }

    }

    /**
     * Синхронно получает SSID (имя) текущей Wi-Fi сети.
     * Использует комбинацию WifiManager.connectionInfo и кэша scanResults для надежности.
     *
     * @return Имя сети (например, "MyHomeWiFi") или null, если получить не удалось.
     */
    @SuppressLint("MissingPermission")
    fun getSsid(): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Способ 1: Через connectionInfo.
        @Suppress("DEPRECATION")
        val ssidFromInfo = wifiManager.connectionInfo?.ssid?.removeSurrounding("\"")
        if (ssidFromInfo != null && ssidFromInfo != "<unknown ssid>") {
            return ssidFromInfo
        }

        // Способ 2: Если первый не сработал, ищем в кэше scanResults.
        @Suppress("DEPRECATION")
        val bssid = wifiManager.connectionInfo?.bssid
        if (bssid != null) {
            val scanResult = wifiManager.scanResults?.find { it.BSSID == bssid }
            if (scanResult != null) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    scanResult.wifiSsid?.toString()?.removeSurrounding("\"")
                } else {
                    @Suppress("DEPRECATION")
                    scanResult.SSID
                }
            }
        }

        return ssidFromInfo
    }

    /**
     * Синхронно получает SSID и BSSID текущей Wi-Fi сети в виде пары.
     * Использует ту же надежную комбинацию `connectionInfo` и кэша `scanResults`.
     *
     * @return Pair<SSID?, BSSID?>. Например, Pair("MyHomeWiFi", "0a:1b:2c:3d:4e:5f").
     *         Значения могут быть null, если информация недоступна.
     */
    @SuppressLint("MissingPermission")
    fun getWifiSsidAndBssid(): Pair<String?, String?> {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Получаем данные из connectionInfo
        @Suppress("DEPRECATION")
        val connectionInfo = wifiManager.connectionInfo

        val ssid = connectionInfo?.ssid?.removeSurrounding("\"")
        val bssid = connectionInfo?.bssid

        // Если оба значения уже корректны, возвращаем их сразу
        if (ssid != null && ssid != "<unknown ssid>" && bssid != null && bssid != "02:00:00:00:00:00") {
            Log.v("NetworkAnalyzer", "getWifiSsidAndBssid: ssid: $ssid, bssid: $bssid")
            return Pair(ssid, bssid)
        } else Log.v(
            "NetworkAnalyzer",
            "wifiManager.connectionInfo вернул заглушку, пробуем из кеша сканРезульт"
        )

        return Pair(getSsid(), getBssid())
    }


    /** получает текущие bssid и ssid
     *
     * если сотовая сеть вернет
     * ssid = "cellular"
     * bssid = "07:00:07:00:07:00"
     *
     * если не удалось получить вернет null
     **/
    fun getNetworkInfo(): Triple<String?, String?, String?>{

        val logTag = "getNetworkInfo"
        var (ssid, bssid) = getWifiSsidAndBssid()

        if (ssid == null || bssid == null || ssid == "<unknown ssid>" || bssid == "02:00:00:00:00:00") {
            Log.e(logTag, "НЕУДАЧА: NetworkAnalyzer не смог получить корректные SSID/BSSID. SSID='$ssid', BSSID='$bssid'")

            if(isDefaultNetworkCellular()){
                ssid = "cellular"
                bssid = "07:00:07:00:07:00"

            }
            else {
                return Triple(null, null, null)
            }


        }
        val subnet = getCurrentSubnet()
        return Triple(ssid, bssid, subnet)
    }









    fun getSimpleNetworkStatus(): String {
        val info = getCompleteNetworkInfo()
        return buildString {
            append("Сеть: ${if (info.isConnected) "Подключено" else "Нет подключения"}\n")
            append(" | Интернет: ${if (info.hasInternet) "Доступен" else "Не доступен"}\n")
            append(" | VPN: ${info.vpnStatus}\n")
            append(" | Типы: ${info.activeNetworks.joinToString()}")

        }
    }

    fun isConnected(): Boolean {

        val allNetworks = connectivityManager.allNetworks
        return allNetworks.any { network ->
            connectivityManager.getNetworkCapabilities(network) != null

        }
    }

    fun isVPNActive(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val info = getCompleteNetworkInfo()
            info.vpnStatus == VPNStatus.ACTIVE || info.vpnStatus == VPNStatus.MIXED
        } else {
            false
        }
    }

    fun isWifiConnected(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val info = getCompleteNetworkInfo()
            info.activeNetworks.contains(NetworkType.WIFI)
        } else {
            false
        }
    }

    /** просто есть ли сотовая сеть
    * если нужна активна ли сотовая сеть вызывать
     *
    * isDefaultNetworkCellular
    **/
    fun isCellularConnected(): Boolean {
        val info = getCompleteNetworkInfo()
        return info.activeNetworks.contains(NetworkType.CELLULAR)
    }


    fun getActiveNetworkTypes(): List<NetworkType> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getCompleteNetworkInfo().activeNetworks
        } else {
            emptyList()
        }
    }

    /**
     * Получает детальную информацию об активной сети, используемой системой "по умолчанию".
     * Именно эту сеть будет использовать браузер или любое другое приложение для выхода в интернет.
     *
     * @return Объект `NetworkDetail` с информацией об активной сети или `null`, если активной сети нет.
     */
    @SuppressLint("MissingPermission")
    fun getActiveDefaultNetworkInfo(): NetworkDetail? {

        // 1. Получаем "сеть по умолчанию" от системы.
        val defaultNetwork = connectivityManager.activeNetwork ?: return null

        // 2. Получаем "возможности" этой сети, чтобы узнать ее тип (Wi-Fi, Cellular и т.д.).
        val caps = connectivityManager.getNetworkCapabilities(defaultNetwork) ?: return null

        // 3. Получаем "свойства соединения" (IP-адреса, DNS и т.д.).
        val linkProperties = connectivityManager.getLinkProperties(defaultNetwork)

        // 4. Определяем тип сети.
        val networkType = determineNetworkType(caps)

        // --- Вот недостающие вычисления ---

        // 5. Определяем, является ли сеть VPN.
        val isVPN = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

        // 6. Определяем, является ли сеть лимитной (metered).
        val isMetered = !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)

        // 7. Проверяем наличие реального доступа в интернет.
        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        // 8. Если это Wi-Fi, получаем SSID и BSSID.
        var ssid: String? = null
        var bssid: String? = null
        if (networkType == NetworkType.WIFI) {
            val wifiInfo = caps.transportInfo as? WifiInfo
            if (wifiInfo != null) {
                ssid = wifiInfo.ssid.removeSurrounding("\"")
                bssid = wifiInfo.bssid
            }
        }

        // 9. Формируем и возвращаем детальный объект со всеми полями.
        return NetworkDetail(
            type = networkType,
            hasInternet = hasInternet,
            network = defaultNetwork,
            capabilities = caps,
            linkProperties = linkProperties,

            isVPN = isVPN,
            isMetered = isMetered,
            ssid = ssid,
            bssid = bssid
        )
    }

    /**
     * Проверяет, является ли активное соединение по умолчанию сотовой сетью
     * с подтвержденным доступом в интернет.
     *
     * @return `true`, если активна сотовая сеть с интернетом, иначе `false`.
     */
    fun isDefaultNetworkCellularWithInternet(): Boolean {
        // 1. Получаем информацию только о той сети, которую система использует по умолчанию.
        val defaultNetwork = getActiveDefaultNetworkInfo()

        // 2. Если сети по умолчанию нет (телефон не в сети), возвращаем false.
        if (defaultNetwork == null) {
            return false
        }

        // 3. Возвращаем результат проверки двух условий:
        //    - Тип сети — CELLULAR?
        //    - И есть ли подтвержденный доступ в интернет?
        return defaultNetwork.type == NetworkType.CELLULAR && defaultNetwork.hasInternet
    }

    /**
     * Проверяет, является ли активное соединение по умолчанию сотовой сетью
     *
     * @return `true`, если активна сотовая сеть, иначе `false`.
     */
    fun isDefaultNetworkCellular(): Boolean {
        // 1. Получаем информацию только о той сети, которую система использует по умолчанию.
        val defaultNetwork = getActiveDefaultNetworkInfo()

        // 2. Если сети по умолчанию нет (телефон не в сети), возвращаем false.
        if (defaultNetwork == null) {
            return false
        }

        // 3. Возвращаем результат проверки двух условий:
        //    - Тип сети — CELLULAR?
        //    - И есть ли подтвержденный доступ в интернет?
        return defaultNetwork.type == NetworkType.CELLULAR
    }


    /**
     * Получает список подсетей. Может фильтровать их по типу сети.
     *
     * @param ofType Если указан тип (например, NetworkType.WIFI), вернет подсети только для этого типа сети.
     *               Если null, вернет все найденные подсети (старое поведение).
     * @return Список объектов SubnetInfo.
     */
    @SuppressLint("MissingPermission")
    fun getSubnetInfo(ofType: NetworkType? = null): List<SubnetInfo> {
        val info = getCompleteNetworkInfo()

        if (ofType == null) {
            // Если фильтр не задан, возвращаем все, как и раньше.
            return info.ipv4Networks + info.ipv6Networks
        }

        // Если фильтр задан, ищем детали для этого типа сети
        val targetNetworkDetails = info.networkDetails.filter { it.type == ofType }

        // Собираем все адреса из найденных деталей
        val linkAddresses =
            targetNetworkDetails.mapNotNull { it.linkProperties?.linkAddresses }.flatten()

        val ipv4Subnets = mutableListOf<SubnetInfo>()
        val ipv6Subnets = mutableListOf<SubnetInfo>()

        linkAddresses.forEach { linkAddress ->
            val address = linkAddress.address
            val prefixLength = linkAddress.prefixLength

            when (address) {
                is Inet4Address -> {
                    val subnetInfo = calculateIPv4SubnetInfo(address, prefixLength)
                    if (ipv4Subnets.none { it.networkAddress == subnetInfo.networkAddress }) {
                        ipv4Subnets.add(subnetInfo)
                    }
                }

                is Inet6Address -> {
                    val subnetInfo = calculateIPv6SubnetInfo(address, prefixLength)
                    if (ipv6Subnets.none { it.networkAddress == subnetInfo.networkAddress }) {
                        ipv6Subnets.add(subnetInfo)
                    }
                }
            }
        }

        return ipv4Subnets + ipv6Subnets
    }


    /**
     * Получает подсеть текущего активного подключения в виде строки, отдавая приоритет Ethernet.
     *
     * @return Строка подсети (например, "192.168.1.0/24") для наиболее приоритетного подключения
     *         или `null`, если активных сетей с IPv4-адресом не найдено.
     */
    fun getCurrentSubnetOLD(): String? {

        // 1. Получаем полную информацию о всех активных сетях.
        val networkInfo = getCompleteNetworkInfo()
        // 2. Ищем подсеть, отдавая приоритет Ethernet, затем Wi-Fi.
        val targetSubnet = networkInfo.networkDetails
            .sortedBy {
                // Сортируем: Ethernet -> Wi-Fi -> остальные.
                when (it.type) {
                    NetworkType.ETHERNET -> 0
                    NetworkType.WIFI -> 1
                    else -> 2
                }
            }
            .firstNotNullOfOrNull { networkDetail ->
                // Для каждого подключения ищем первый валидный IPv4 адрес.
                networkDetail.linkProperties?.linkAddresses?.firstNotNullOfOrNull { linkAddress ->
                    val address = linkAddress.address
                    if (address is Inet4Address) {
                        // Если найден IPv4, вычисляем и возвращаем информацию о подсети.
                        calculateIPv4SubnetInfo(address, linkAddress.prefixLength)
                    } else {
                        null // Игнорируем IPv6 адреса в этой логике
                    }
                }
            }

        return targetSubnet?.networkAddress
    }


    /**
     * Получает подсеть текущей АКТИВНОЙ сети, используемой по умолчанию, в виде строки.
     * Именно эту сеть будет использовать браузер или любое другое приложение для выхода в интернет.
     *
     * @return Строка подсети (например, "192.168.1.0/24") для сети по умолчанию
     *         или `null`, если активной сети с IPv4-адресом не найдено.
     */
    fun getCurrentSubnet(): String? {
        val logTag = "GetCurrentSubnet"

        // 1. Получаем детальную информацию именно о той сети, которую система использует по умолчанию.
        val defaultNetworkDetail = getActiveDefaultNetworkInfo()
        if (defaultNetworkDetail == null) {
            Log.e(logTag, "Не удалось получить информацию об активной сети по умолчанию. getActiveDefaultNetworkInfo() вернул null.")
            return null
        }
        Log.v(logTag, "Активная сеть по умолчанию найдена: ${defaultNetworkDetail.type}, Интернет: ${defaultNetworkDetail.hasInternet}")

        // 2. Извлекаем "свойства соединения" (linkProperties) для этой сети.
        val linkProperties = defaultNetworkDetail.linkProperties
        if (linkProperties == null) {
            Log.e(logTag, "Не удалось получить LinkProperties для активной сети. Возможно, сеть еще не полностью сконфигурирована.")
            return null
        }

        // 3. Ищем первый попавшийся IPv4-адрес в этой конкретной сети.
        Log.v(logTag, "Ищем IPv4 адрес среди ${linkProperties.linkAddresses.size} доступных адресов...")
        val targetSubnetInfo = linkProperties.linkAddresses.firstNotNullOfOrNull { linkAddress ->
            val address = linkAddress.address
            // Убеждаемся, что это именно IPv4 адрес
            if (address is Inet4Address) {
                Log.v(logTag, "Найден IPv4 адрес: ${address.hostAddress} с префиксом /${linkAddress.prefixLength}. Вычисляем подсеть.")
                // Если найден IPv4, вычисляем и возвращаем информацию о подсети.
                calculateIPv4SubnetInfo(address, linkAddress.prefixLength)
            } else {
                null // Игнорируем IPv6 и другие типы адресов
            }
        }

        if (targetSubnetInfo == null) {
            Log.e(logTag, "В активной сети по умолчанию не найдено ни одного IPv4 адреса.")
            return null
        }

        // 4. Возвращаем строковое представление подсети (например, "192.168.1.0/24").
        Log.v(logTag, "Результат: подсеть активной сети - '${targetSubnetInfo.networkAddress}'")
        return targetSubnetInfo.networkAddress
    }


    /**
     * Получает подсеть текущего активного подключения, отдавая приоритет Ethernet.
     *
     * @param  netInfo нужно передать getSubnetInfo() - getCurrentSubnet(getCompleteNetworkInfo())
     * @return Объект `SubnetInfo` для наиболее приоритетного подключения (Ethernet, затем Wi-Fi)
     *         или `null`, если активных сетей с IPv4-адресом не найдено.
     */
    fun getCurrentSubnet(netInfo: NetworkInfo): SubnetInfo? {

        // 1. Получаем полную информацию о всех активных сетях.
        val networkInfo = netInfo

        // 2. Ищем подсеть, отдавая приоритет Ethernet, затем Wi-Fi.
        val targetSubnet = networkInfo.networkDetails
            .sortedBy {
                // Сортируем: Ethernet -> Wi-Fi -> остальные.
                when (it.type) {
                    NetworkType.ETHERNET -> 0
                    NetworkType.WIFI -> 1
                    else -> 2
                }
            }
            .firstNotNullOfOrNull { networkDetail ->
                // Для каждого подключения ищем первый валидный IPv4 адрес.
                networkDetail.linkProperties?.linkAddresses?.firstNotNullOfOrNull { linkAddress ->
                    val address = linkAddress.address
                    if (address is Inet4Address) {
                        // Если найден IPv4, вычисляем и возвращаем информацию о подсети.
                        calculateIPv4SubnetInfo(address, linkAddress.prefixLength)
                    } else {
                        null // Игнорируем IPv6 адреса в этой логике
                    }
                }
            }

        return targetSubnet
    }


    fun startNetworkMonitoring(callback: (NetworkInfo) -> Unit): NetworkCallbackWrapper? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NetworkCallbackWrapper(connectivityManager, callback)
        } else {
            null
        }
    }

    private fun determineNetworkType(caps: NetworkCapabilities): NetworkType {
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NetworkType.VPN
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> NetworkType.BLUETOOTH
            else -> NetworkType.UNKNOWN
        }
    }

    private fun analyzeLinkProperties(
        linkProperties: LinkProperties?,
        ipv4Networks: MutableList<SubnetInfo>,
        ipv6Networks: MutableList<SubnetInfo>
    ) {
        linkProperties?.linkAddresses?.forEach { linkAddress ->
            val address = linkAddress.address
            val prefixLength = linkAddress.prefixLength

            when (address) {
                is Inet4Address -> {
                    val subnetInfo = calculateIPv4SubnetInfo(address, prefixLength)
                    if (ipv4Networks.none { it.networkAddress == subnetInfo.networkAddress }) {
                        ipv4Networks.add(subnetInfo)
                    }
                }

                is Inet6Address -> {
                    val subnetInfo = calculateIPv6SubnetInfo(address, prefixLength)
                    if (ipv6Networks.none { it.networkAddress == subnetInfo.networkAddress }) {
                        ipv6Networks.add(subnetInfo)
                    }
                }
            }
        }
    }

    fun calculateIPv4SubnetInfo(address: Inet4Address, prefixLength: Int): SubnetInfo {
        val ip = address.address
        val mask = calculateIPv4Mask(prefixLength)

        val networkInt = (bytesToInt(ip) and bytesToInt(mask))
        val broadcastInt = networkInt or (bytesToInt(mask).inv() and 0xFFFFFFFFL)

        val networkStr = intToIPv4(networkInt.toInt())
        val firstAddress = intToIPv4((networkInt + 1).toInt())
        val lastAddress = intToIPv4((broadcastInt - 1).toInt())

        val totalAddresses = maxOf(1, broadcastInt - networkInt - 1)

        return SubnetInfo(
            networkAddress = "$networkStr/$prefixLength",
            prefixLength = prefixLength,
            totalAddresses = totalAddresses,
            firstAddress = firstAddress,
            lastAddress = lastAddress,
            ipVersion = "IPv4"
        )
    }

    private fun calculateIPv6SubnetInfo(address: Inet6Address, prefixLength: Int): SubnetInfo {
        // val totalAddresses = 1L shl (128 - prefixLength)
        val totalAddresses = -1L // или null / BigInteger
        val firstAddress = "${address.hostAddress?.substringBefore("%")}/$prefixLength"

        return SubnetInfo(
            networkAddress = firstAddress,
            prefixLength = prefixLength,
            totalAddresses = totalAddresses,
            firstAddress = "N/A (IPv6)",
            lastAddress = "N/A (IPv6)",
            ipVersion = "IPv6"
        )
    }

    private fun calculateIPv4Mask(prefixLength: Int): ByteArray {
        val mask = (-1 shl (32 - prefixLength)).toInt()
        return byteArrayOf(
            (mask ushr 24 and 0xFF).toByte(),
            (mask ushr 16 and 0xFF).toByte(),
            (mask ushr 8 and 0xFF).toByte(),
            (mask and 0xFF).toByte()
        )
    }

    private fun bytesToInt(bytes: ByteArray): Long {
        return ((bytes[0].toInt() and 0xFF) shl 24 or
                ((bytes[1].toInt() and 0xFF) shl 16) or
                ((bytes[2].toInt() and 0xFF) shl 8) or
                (bytes[3].toInt() and 0xFF)).toLong() and 0xFFFFFFFFL
    }

    private fun intToIPv4(ip: Int): String {
        return "${ip ushr 24 and 0xFF}.${ip ushr 16 and 0xFF}.${ip ushr 8 and 0xFF}.${ip and 0xFF}"
    }


    inner class NetworkCallbackWrapper(
        private val connectivityManager: ConnectivityManager,
        private val callback: (NetworkInfo) -> Unit
    ) : ConnectivityManager.NetworkCallback() {

        private var isRegistered = false

        fun startMonitoring() {
            if (!isRegistered) {
                val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                connectivityManager.registerNetworkCallback(request, this)
                isRegistered = true
            }
        }

        fun stopMonitoring() {
            if (isRegistered) {
                connectivityManager.unregisterNetworkCallback(this)
                isRegistered = false
            }
        }

        override fun onAvailable(network: Network) {
            callback(getCompleteNetworkInfo())
        }

        override fun onLost(network: Network) {
            callback(getCompleteNetworkInfo())
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            callback(getCompleteNetworkInfo())
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            callback(getCompleteNetworkInfo())
        }
    }


}