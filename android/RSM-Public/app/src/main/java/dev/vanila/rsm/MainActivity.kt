package dev.vanila.rsm

import NetworkAnalyzer

import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAsDropDown

import android.view.animation.AccelerateDecelerateInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent

import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


import androidx.constraintlayout.widget.ConstraintLayout
import android.view.animation.OvershootInterpolator
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.caverock.androidsvg.SVG
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.content.withStyledAttributes
import androidx.core.text.HtmlCompat

import androidx.lifecycle.lifecycleScope
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Message
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.HttpAuthHandler
import android.webkit.JavascriptInterface
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog

import android.location.LocationManager
import android.net.http.SslError


import android.provider.Settings
import android.text.Html
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import android.webkit.SslErrorHandler
import android.widget.AdapterView


import android.widget.CheckBox
import androidx.activity.result.IntentSenderRequest



import androidx.core.animation.AnimatorSet


import androidx.core.net.toUri

import android.os.Parcel
import android.text.Spanned
import android.util.TypedValue
import android.view.Gravity
import androidx.activity.viewModels
import androidx.annotation.AttrRes
import androidx.core.graphics.alpha

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.lihang.ShadowLayout
import com.lihang.SmartLoadingView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.text.contains
import kotlin.text.removePrefix
import androidx.core.graphics.drawable.toDrawable
import dev.vanila.rsm.SettingsManager


import eightbitlab.com.blurview.BlurTarget
import eightbitlab.com.blurview.BlurView
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume








class MainActivity : AppCompatActivity() {


    private val viewModel: MainViewModel by viewModels()

    // Ключи для настроек
    companion object {
        private const val DEFAULT_ICON_COLOR = "#FF4081"
        private const val THEME_DEFAULT = "default"

    }

    private var snow_man = false
    private var snow_man_animation = false

    private var easter_egg = false
    private var btn_back = true
    private var backPressedOnce = false
    private val backPressedHandler = Handler(Looper.getMainLooper())


    private var address_input = false // адресная строка тру показываекм фелс не показываем
    private var isUrlBarExpanded = false // Флаг для отслеживания состояния поля URL

    private var check_version = true

    private var open_last_device = true
    private var solid_color_icon = false
    private var icon_color: String? = DEFAULT_ICON_COLOR
    private var label = true
    private var icon_in_label = false
    private var currentWebViewFavicon: Bitmap? = null

    private var theme = THEME_DEFAULT

    private var eggClickCount = 0




    private var isDragging = false
    private var initialX = 0f
    private var initialY = 0f
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private val swipeThreshold = 100 // минимальное расстояние для определения свайпа
    private val edgeMargin = 20 // отступ от края в dp


    private lateinit var webView: WebView
    private var isZoomed = 1.0f
    private var zoomFactor = 1.0f

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private lateinit var fileChooserLauncher: ActivityResultLauncher<Intent>

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var isShowingPlaceholder = false
    private var isLoadingFromSwipe = false
    private lateinit var webProgress: ProgressBar


    private lateinit var placeholderDeviceUnavailable: ConstraintLayout
    private lateinit var placeholderNoDevices: ConstraintLayout
    private lateinit var placeholderNoDevicesWelcome: ConstraintLayout

    private lateinit var smartLoadingView: SmartLoadingView
    private lateinit var shadowLayout: ShadowLayout

    private lateinit var smartLoadingViewWelcome: SmartLoadingView
    private lateinit var shadowLayoutWelcome: ShadowLayout

    private lateinit var smartLoadingViewRefresh: SmartLoadingView
    private lateinit var shadowLayoutRefresh: ShadowLayout


    private lateinit var devicesList: ListView
    //private lateinit var btnDevices: TextView
    //private lateinit var btnSettings: TextView
    private lateinit var slidingLayout: SlidingUpPanelLayout

    private var topColor = "#225C6E".toColorInt()
    private var isLight = false

    //private var devices: MutableList<Device> = mutableListOf()
    private lateinit var deviceManager: DeviceManager
    private val allDevices = mutableListOf<Device>()

    //private var firstRunPlaceholder = false
    private var pendingUpdateVersion: String? = null


    private lateinit var networkAnalyzer: NetworkAnalyzer
    //private var networkMonitor: NetworkAnalyzer.NetworkCallbackWrapper? = null



    private var splitNetwork = false
    private var splitNetworkManual = false

    private var onPermissionsGrantedAction: (() -> Unit)? = null
    private var isLocationDialogShowing = false
    private var isMergeNetworkDialogShowing = false
    private var isTrustNetworkDialogShowing = false
    private var isPermissionDialogShowing = false

    private var currentNetworkFingerprint: String? = null //

    private val knownNetworksList = mutableListOf<KnownNetwork>()

    private var httpsError: Boolean = false



    // private var autofillRequested = false
    //  private var autofillAttempts = 0
    // private val MAX_AUTOFILL_ATTEMPTS = 8
    private fun displayNetworkInfo(info: NetworkAnalyzer.NetworkInfo) {
        Log.e("Network", "=== ИНФОРМАЦИЯ О СЕТИ ===")
        println("Подключение: ${if (info.isConnected) "Да" else "Нет"}")
        println("Интернет: ${if (info.hasInternet) "Доступен" else "Не доступен"}")
        println("VPN: ${info.vpnStatus}")
        println("Типы сетей: ${info.activeNetworks.joinToString()}")

        println("\nДетали сетей:")
        info.networkDetails.forEach { detail ->
            println(" - ${detail.type}: VPN=${detail.isVPN}, Интернет=${detail.hasInternet}")
        }

        println("\nПодсети IPv4:")
        info.ipv4Networks.forEach { subnet ->
            println(" - ${subnet.networkAddress} (${subnet.totalAddresses} адресов)")
        }

        println("SSID: ${info.ssid}")
        println("BSID: ${info.bssid}")

    }

    private lateinit var viewManager: ViewManager

    private var oneStart = false
    private var oneStartSnowAnim = true









    /**
     * Читает устройства из старого файла "discovered_devices.json" и возвращает их списком.
     * @return Список устройств для миграции или null, если старый файл не найден.
     */
    private fun readOldDevicesForMigration(): List<Device>? {
        val oldDevicesFileName = "discovered_devices.json"
        val oldFile = File(filesDir, oldDevicesFileName)

        //существует ли старый файл
        if (!oldFile.exists()) {
            Log.i("Migration", "Старый файл '$oldDevicesFileName' не найден. Миграция не требуется.")
            return null // Возвращаем null, если файла нет
        }

        Log.i("Migration", "Обнаружен старый файл устройств. Чтение данных для миграции...")
        val devicesToMigrate = mutableListOf<Device>()
        try {
            // загрузка устройства из старого файла
            val jsonStr = oldFile.readText()
            val jsonArray = org.json.JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray.getJSONObject(i)
                devicesToMigrate.add(
                    Device(
                        name = jsonObj.getString("name"),
                        url = jsonObj.getString("url"),
                        faviconPath = jsonObj.optString("favicon", null).takeIf { it != "null" },
                        isBuiltinIcon = jsonObj.optBoolean("isBuiltin", false),
                        // имя сети и айди пумолчанию
                        networkName = "default",
                        networkId = "defaultid"
                    )
                )
            }
            Log.i("Migration", "Прочитано ${devicesToMigrate.size} устройств из старого формата.")
            return devicesToMigrate
        } catch (e: Exception) {
            Log.e("Migration", "Ошибка во время чтения старого файла устройств", e)
            return null // ошибка возвращаем null, прервать миграцию
        }
    }

    private fun handleFirstRun() {

        if (SettingsManager.isFirstRunMigrate()) {
            Log.i("MainActivity", "Обнаружен первый запуск новой версии приложения. Запускаем процесс миграции и очистки.")

            // --- МИГРАЦИЯ ---

            // Пытаемся прочитать старые устройства в память
            val devicesToMigrate = readOldDevicesForMigration()

            //  ПОЛНОСТЬЮ ОЧИЩАЕМ ВСЕ ДАННЫЕ ПРИЛОЖЕНИЯ
            clearAllApplicationData()
            Log.i("Migration", "Все старые данные приложения (файлы, настройки) были удалены.")

            //  Если есть устройства для миграции, сохраняем
            if (devicesToMigrate != null && devicesToMigrate.isNotEmpty()) {
                Log.i("Migration", "Сохранение ${devicesToMigrate.size} мигрированных устройств в новом формате...")

                val deviceManager = DeviceManager(this)
                deviceManager.saveDevices(devicesToMigrate, App.DEFAULT_NETWORK_FINGERPRINT)
                Log.i("Migration", "Мигрированные устройства успешно сохранены.")
            } else {
                Log.i("Migration", "Нет устройств для миграции, хранилище чистое.")
            }

            Log.i("MainActivity", "Миграция завершена.")

        } else {
           // Log.i("MainActivity", "запуск")
        }
    }

    /**
     * ПОЛНОСТЬЮ очищает все данные приложения: SharedPreferences, файлы и кэш.
     */
    private fun clearAllApplicationData() {
        Log.e("MainActivity", "!!! ВНИМАНИЕ: Начата полная очистка данных приложения !!!")

        // 1. ПОЛНАЯ ОЧИСТКА ВСЕХ SharedPreferences
        try {
            // Получаем корневую папку данных приложения
            val dataDir = File(applicationInfo.dataDir)
            // Ищем папку "shared_prefs"
            val prefsDir = File(dataDir, "shared_prefs")

            if (prefsDir.exists() && prefsDir.isDirectory) {
                // Рекурсивно удаляем папку "shared_prefs" и ВСЁ её содержимое
                if (prefsDir.deleteRecursively()) {
                    Log.d("clearAllApplicationData", "Папка SharedPreferences ('shared_prefs') успешно удалена.")
                } else {
                    Log.e("clearAllApplicationData", "Не удалось удалить папку SharedPreferences.")
                }
            } else {
                Log.d("clearAllApplicationData", "Папка SharedPreferences не найдена, очистка не требуется.")
            }
        } catch (e: Exception) {
            Log.e("clearAllApplicationData", "Ошибка при очистке SharedPreferences", e)
        }

        // 2. Очистка всех файлов в приватной директории (включая devices_*.json и иконки)
        try {
            val filesDir = filesDir // Это стандартная папка /data/data/ваше.приложение/files
            // Рекурсивно удаляем всё содержимое папки /files
            if (filesDir.exists() && filesDir.isDirectory) {
                val files = filesDir.listFiles()
                files?.forEach { file ->
                    if(file.deleteRecursively()) {
                        Log.d("clearAllApplicationData", "Удаление файла/папки: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("clearAllApplicationData", "Ошибка при очистке файлов приложения", e)
        }

        // 3. Очистка кэша
        try {
            if (cacheDir?.deleteRecursively() == true) { // Удаляем папку кэша и всё её содержимое
                Log.d("clearAllApplicationData", "Кэш приложения очищен.")
            }
        } catch (e: Exception) {
            Log.e("clearAllApplicationData", "Ошибка при очистке кэша", e)
        }

        Log.e("MainActivity", "!!! Полная очистка данных приложения завершена !!!")
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel

        super.onCreate(savedInstanceState)
        Log.e("onCreate", "========== СТАРТУУУЕЕЕМ ==========")
        oneStart = true

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        ThemeManager.applyTheme(this)
        window.insetsController?.hide(WindowInsets.Type.navigationBars())
        window.insetsController?.let { controller ->
            WindowInsets.Type.navigationBars()
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        /////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        
        
        deviceManager = DeviceManager(this)
        viewManager = ViewManager()
        networkAnalyzer = NetworkAnalyzer(this)

        fileChooserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                // Если выбран один файл, data.data будет не null.
                // Если выбрано несколько, они будут в data.clipData.
                val uris = data?.data?.let { arrayOf(it) } ?:
                data?.clipData?.let { clipData ->
                    Array(clipData.itemCount) { i -> clipData.getItemAt(i).uri }
                }

                filePathCallback?.onReceiveValue(uris)
            } else {
                // Пользователь отменил выбор файла
                filePathCallback?.onReceiveValue(null)
            }
            filePathCallback = null // Очищаем колбэк
        }

        handleFirstRun()
        loadSettings()

        setupView()
        setupWebView()
        setupBackPressed()
        setupIntentExtra()


        setNetwork(getCurrentNetworkFingerprint())
        loadDevices(currentNetworkFingerprint)
        openLastOpenedDevice()

      //  viewManager.lableVisibility()
      //  viewManager.updateAddressAreaVisability()
        //viewManager.updateUrlFieldState(false, false)




        val networkInfo = networkAnalyzer.getCompleteNetworkInfo()
        displayNetworkInfo(networkInfo)

        val connectInfo = networkAnalyzer.getSimpleNetworkStatus()
        Log.d("onCreate: ", connectInfo)

        // Получаем детали только для Wi-Fi
        //val wifiDetails = networkAnalyzer.getWifiDetails()

        val netInfo = networkAnalyzer.getWifiSsidAndBssid()
        Log.e("WIFI_CHECK", "Получено из getWifiSsidAndBssid(): SSID = '${netInfo.first}', BSSID = '${netInfo.second}'\n")

        logSavedDevices()


















        if (check_version)
            lifecycleScope.launch {
                UpdateChecker.check(this@MainActivity)
            }


        Log.e("MainActivity", "^^^^^^^^^^ OnCreate ^^^^^^^^^^")
    }// OnCreate


    /**
     * Применить все настройки сети
     */
    private fun applyNetwork(network: KnownNetwork?){


        if (network != null) {
            Log.e("applyNetwork", "applyNetwork вызван с network == ${network.name} fingerprint ${network.fingerprint}")
            setNetwork(network.fingerprint)
            loadDevices(network.fingerprint)
            viewManager.updateNetworkIcon(network)
            viewManager.updateNetworksForSpinner(network)




        }
        else {
            Log.e("applyNetwork", "applyNetwork вызван с network == null, устанавливаем выбор на все сети")
            setNetwork(App.ALL_NETWORKS_FINGERPRINT)
            loadDevices(App.ALL_NETWORKS_FINGERPRINT)
            viewManager.updateNetworkIcon(null)
            viewManager.updateNetworksForSpinner(null)
            openUrl("about:blank")

        }


    }


    /**
     * Установить fingerprint как текущий (currentNetworkFingerprint)
     *
     *  сохранит отпечаток и id
     *
     * @param fingerprint - fingerprint сети
     *
     *
     */
    private fun setNetwork(fingerprint: String?){
        Log.d("setNetwork", "Устанавливаем fingerprint сети: $fingerprint")

        // сохранить ID
        val networkId = fingerprint?.let { NetworkManager.getIdByFingerprint(this, it) }
        saveLastNetworkId(networkId)
        saveLastNetworkFingerprint(fingerprint)

        currentNetworkFingerprint = fingerprint

    }




    /**
     * создать сеть по умолчанию
     *
     * создаст только если разделение выключено и дефолтной сети нет
     */
    private fun createDefaultNetwork(){
        //разделение отключено
        if(splitNetwork) return
        var knownNetworks = NetworkManager.getKnownNetworks(this)
        var defaultNetwork = knownNetworks.find { it.fingerprint == App.DEFAULT_NETWORK_FINGERPRINT}

        if(knownNetworks.isEmpty() || defaultNetwork == null ) {
            NetworkManager.addManualNetwork(
                this,
                "default",
                isTrusted = false,
                null,
                "06:00:06:00:06"
            )
            knownNetworks = NetworkManager.getKnownNetworks(this)
            defaultNetwork = knownNetworks.find { it.fingerprint == App.DEFAULT_NETWORK_FINGERPRINT }
            Log.e("DefaultNetwork",
                "Создана дефолтная сеть: " +
                        " name ${defaultNetwork?.name}" +
                        " fingerprint ${defaultNetwork?.fingerprint}")
        }


        knownNetworks = NetworkManager.getKnownNetworks(this)
        defaultNetwork = knownNetworks.find { it.fingerprint == App.DEFAULT_NETWORK_FINGERPRINT }

        if (defaultNetwork != null)
            Log.d("DefaultNetwork",
                "Дефолтная сеть в наличии: " +
                    " name ${defaultNetwork.name}" +
                    " ssid ${defaultNetwork.ssid}" +
                    " fingerprint ${defaultNetwork.fingerprint}")

        else Log.e("DefaultNetwork", "Не удалось создать или прочтитать дефолтную сеть")
    }
    // получить Fingerprint текущей сети
    private fun getCurrentNetworkFingerprint(): String? {
        splitNetwork = SettingsManager.isSplitNetworkEnabled
        splitNetworkManual = SettingsManager.isSplitNetworkManualEnabled
        val logTag = "GET_NETWORK_FINGERPRINT"
        if (!splitNetwork) {
            Log.d(logTag, "Разделение выключено")
            createDefaultNetwork()


            return App.DEFAULT_NETWORK_FINGERPRINT
        }


        if (splitNetworkManual) {

            val lastFingerprint = SettingsManager.lastNetworkFingerprint
            if (lastFingerprint != null) {
                Log.d(logTag, "Разделение включено, но вручную, вернули последнюю сеть: $lastFingerprint")
                return lastFingerprint
            }
            Log.e(logTag, "Отпечаток не найден, пробуем ID")

            // если отпечатка нет пробуем по id
            val lastNetId = SettingsManager.lastNetwork
            val fingerprint =
                if (lastNetId != null) NetworkManager.getFingerprintById(this, lastNetId)
                else App.ALL_NETWORKS_FINGERPRINT

            Log.d(logTag, "Разделение включено, но вручную, вернули последнюю сеть по ID: $fingerprint")
            return  fingerprint
        }
        else {
           //detectNewNetwork()

            val (ssid, bssid) = networkAnalyzer.getWifiSsidAndBssid()
            val subnet = networkAnalyzer.getCurrentSubnet()


            val fingerprint =
                if(ssid != null && bssid != null && subnet != null)
                        FingerprintGenerator.generate(ssid, bssid, subnet)

                else {

                    if (networkAnalyzer.isDefaultNetworkCellular()) {
                        val bssidCellularPrefix = FingerprintGenerator.extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)

                        val cellularNetwork = NetworkManager.findNetworkByBssidPrefix(this, bssidCellularPrefix?: "null")
                        if (cellularNetwork != null) {
                            Log.d(logTag, "НАЙДЕНА СОТОВАЯ СЕТЬ!\n" +
                                    "fingerprint: ${cellularNetwork.fingerprint}\n" +
                                    "name: ${cellularNetwork.name}\n" +
                                    "ssid: ${cellularNetwork.ssid}\n" +
                                    "bssid: ${cellularNetwork.mainBssidPrefix}\n" +
                                    "subnet ${cellularNetwork.subnet}"
                            )

                            return App.CELLULAR_NETWORKS_FINGERPRINT
                        }
                        else {
                            Log.e(logTag, "НАЙДЕНА СОТОВАЯ СЕТЬ! Но в базе ее нет." )
                        }

                    }
                    else {
                        Log.w(logTag, "ЭТО НЕ СТОТОВАЯ СЕТЬ! " )
                    }


                    Log.e(logTag, "getCurrentNetworkFingerprint: не удалось автоматически получить сеть, вернули App.ALL_NETWORKS_FINGERPRINT: ${App.ALL_NETWORKS_FINGERPRINT}" )
                    return App.ALL_NETWORKS_FINGERPRINT
                }



            Log.d(logTag, "getCurrentNetworkFingerprint: автоматически получили fingerprint: $fingerprint")
            return fingerprint


        }

    }

    private fun saveLastNetworkId(networkId: String?) {
        if(networkId == null) {
            Log.e("saveLastNetworkId", "networkId == null")
            return
        }
        else if(networkId == App.ALL_NETWORKS_FINGERPRINT) {
            Log.e("saveLastNetworkId", "networkId == App.ALL_NETWORKS_FINGERPRINT")
            return
        }

        SettingsManager.lastNetwork = networkId
        Log.d("", "Сохранен ID последней сети: $networkId")
    }
    private fun saveLastNetworkFingerprint(fingerprint: String?){
        if(fingerprint == App.ALL_NETWORKS_FINGERPRINT){
            Log.d("saveLastNetworkFingerprint", "Передали fingerprint ALL_NETWORKS_FINGERPRINT, ничего не сохраняем: $fingerprint")
            return
        }
        SettingsManager.lastNetworkFingerprint = fingerprint
        Log.d("saveLastNetworkFingerprint", "Сохранен fingerprint последней сети: $fingerprint")
    }
    private fun openPanel(){
        slidingLayout.panelState = PanelState.EXPANDED
        startHeaderPlay()
        viewManager.setLabelTheme(light = true)
    }
    private fun closePanel(){
        if(SettingsManager.openPanel) {
            SettingsManager.openPanel = false
            openPanel()

        }
        slidingLayout.panelState = PanelState.COLLAPSED
        Log.d("closePanel", "Закрываем панель")
        viewManager.getTopColorFromWebView()
    }

    /**
     * Открыть устройство
     *
     * • вызывает openUrl(device.url)
     *
     * • сохраняет последнее устройство
     *
     * • обновляет ярлык
     *
     */
    private fun openDevice(device: Device){
        Log.d("openDevice", "Открываем устройство: ${device.name}, url ${device.url}, networkName ${device.networkName}")
        httpsError = false
        webView.loadUrl("about:blank")

        openUrl(device.url)
        saveLastOpenedDevice(device)
        currentNetworkFingerprint?.let { deviceManager.saveDevice(device, it) }

        viewManager.updateLabel(device)
    }

    /**
     * Открывает URL.
     *
     * - Загружает URL в WebView.
     * - Устанавливает текст в адресную строку.
     * - Показывает/скрывает индикатор загрузки.
     *
     * @param url URL для открытия.
     * @param progress Если `true`, покажет индикатор загрузки.
     */
    private fun openUrl(url: String, progress: Boolean = true){
        Log.d("openUrl", "=== Открываем урл ===  $url")
        httpsError = false
        webView.loadUrl(url)
        viewManager.setUrlText(url)
        if(progress) webProgress.visibility = View.VISIBLE

    }

    /**
     * если надо покажет если надо скроет плейсхолдер нет устройств
     *
     * проверяет первый ли старт
     */
    private fun updateNoDevicePlaceholder(){
        val first = SettingsManager.isFirstRun()
        val allDeviceCount = deviceManager.getTotalDeviceCount()
        val deviceCount = getDevicesCount()

        Log.d(
            "Placeholder",
            "first: $first  deviceCount: $deviceCount, allDeviceCount: $allDeviceCount"
        )

        if (first && allDeviceCount == 0) {

            setPlaceholders(noDevicesWelcome = true)


            Log.e("Placeholder", "Тадам, первый запуск, устройсв нет")

            Handler(Looper.getMainLooper()).postDelayed({
                firstStartPlay()
            }, 1000)
            return
        }
        else if(first){
            Log.e("Placeholder", "первый запуск, но устройства есть")

        }


        if (!splitNetwork)
            if(allDeviceCount == 0) {
                setPlaceholders(noDevices = true)

                Handler(Looper.getMainLooper()).postDelayed({
                    noDevicePlay()
                }, 1000)
                isShowingPlaceholder = true

                Log.d(
                    "Placeholder",
                    "splitNetwork: $splitNetwork  deviceCount: $deviceCount, allDeviceCount: $allDeviceCount"
                )

                return
            }
            else {
                closeAllPlaceholders()
                Log.d(
                    "Placeholder",
                    "splitNetwork: $splitNetwork  deviceCount: $deviceCount, allDeviceCount: $allDeviceCount"
                )
                return
            }

        if (splitNetwork){
            if(allDeviceCount == 0) {
                setPlaceholders(noDevices = true)

                Handler(Looper.getMainLooper()).postDelayed({
                    noDevicePlay()
                }, 1000)

                Log.d(
                    "Placeholder",
                    "splitNetwork: $splitNetwork  deviceCount: $deviceCount, allDeviceCount: $allDeviceCount"
                )
                return
            }
            else {
                closeAllPlaceholders()
                Log.d(
                    "Placeholder",
                    "splitNetwork: $splitNetwork  deviceCount: $deviceCount, allDeviceCount: $allDeviceCount"
                )
                return
            }

        }
    }






    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }







    private fun setupBackPressed() {

        var lastGoBackUrl: String? = null

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("BackPressed", "label: $label, btn_back: $btn_back, panelState: ${slidingLayout.panelState}")

                // Проверяем, может ли WebView вообще вернуться назад
                if (webView.canGoBack()) {
                    val history = webView.copyBackForwardList()
                    val previousUrl = history.getItemAtIndex(history.currentIndex - 1).url

                    Log.d("BackPressed_WebView", "WebView может вернуться. Текущий URL: ${webView.url}, кандидат на возврат: $previousUrl")

                    // Если мы пытаемся вернуться назад, а текущий URL - это тот,
                    // на который мы уже пытались вернуться в прошлый раз, какая то шляпа
                    if (webView.url == lastGoBackUrl) {
                        Log.w("BackPressed_WebView", "Обнаружено застревание WebView! Игнорируем goBack() и обрабатываем нативно.")

                    } else {
                        // Если застревания нет, выполняем стандартный возврат
                        Log.d("BackPressed_WebView", "Выполняю webView.goBack()")
                        lastGoBackUrl = webView.url // Запоминаем текущий URL перед возвратом
                        webView.goBack()

                        val goingBackToUrl = history.getItemAtIndex(history.currentIndex - 1).url
                        val device = findDeviceByUrl(goingBackToUrl)

                        viewManager.updateLabel(device)
                        return // Выходим
                    }
                }


                //  если WebView не может вернуться назад или случилась какая-то шляпа
                Log.d("BackPressed", "Выполняется нативная обработка 'Назад'")
                lastGoBackUrl = null // Сбрасываем флаг застревания

                if (label && !btn_back) {
                    if (slidingLayout.panelState == PanelState.EXPANDED || slidingLayout.panelState == PanelState.ANCHORED) {
                        slidingLayout.panelState = PanelState.COLLAPSED
                        backPressedOnce = false
                    } else {
                        if (backPressedOnce) {
                            // Если нажали второй раз в течение 2 секунд, сворачиваем приложение
                            moveTaskToBack(true)
                            return
                        }
                        backPressedOnce = true

                        Toast.makeText(this@MainActivity, "Нажмите еще раз, чтобы выйти", Toast.LENGTH_SHORT).show()

                        backPressedHandler.postDelayed({ backPressedOnce = false }, 2000)
                    }
                } else {
                    if (slidingLayout.panelState == PanelState.COLLAPSED || slidingLayout.panelState == PanelState.ANCHORED) {
                        slidingLayout.panelState = PanelState.EXPANDED
                        startHeaderPlay()
                        viewManager.setLabelTheme(light = true)
                    } else {
                        if (backPressedOnce) {
                            // Если нажали второй раз в течение 2 секунд, сворачиваем приложение
                            moveTaskToBack(true)
                            return
                        }
                        backPressedOnce = true

                        Toast.makeText(this@MainActivity, "Нажмите еще раз, чтобы выйти", Toast.LENGTH_SHORT).show()

                        backPressedHandler.postDelayed({ backPressedOnce = false }, 2000)
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
    private fun findDeviceByUrl(url: String): Device? {
        return allDevices.find { it.url.equals(url, ignoreCase = true) }
    }


    private fun setupIntentExtra() {
        val currentIntent = this.intent ?: return

        // надо пересоздаться
        if (currentIntent.getBooleanExtra("FORCE_RECREATE", false)) {
            currentIntent.removeExtra("FORCE_RECREATE")
            recreate()
        }

        // надо открыть устройство
        if (currentIntent.hasExtra("SELECTED_DEVICE_NAME") && currentIntent.hasExtra("SELECTED_DEVICE_URL")) {
            val deviceName = currentIntent.getStringExtra("SELECTED_DEVICE_NAME") ?: ""
            val deviceUrl = currentIntent.getStringExtra("SELECTED_DEVICE_URL") ?: ""

            if (deviceName.isNotEmpty() && deviceUrl.isNotEmpty()) {
                //addressField.text = deviceName
                //labelText.text = deviceName


                val device = Device(deviceName, deviceUrl, null, false, null,null,false)


                //placeholderDeviceUnavailable.visibility = View.GONE
                placeholderNoDevices.visibility = View.GONE
                placeholderNoDevicesWelcome.visibility = View.GONE
                httpsError = false
                webView.loadUrl(deviceUrl)
               
                //addressInput.setText(deviceUrl)



                viewManager.updateLabel(device)

                slidingLayout.panelState = PanelState.COLLAPSED

                Toast.makeText(this, deviceName, Toast.LENGTH_SHORT).show()

                //val device = Device(deviceName, deviceUrl, null, false, false)
                saveLastOpenedDevice(device)

                // Закрываем панель при выборе устройства
                // slidingLayout.panelState = com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED
                //mainLayout.setBackgroundColor(topColor)

                // Очищаем extras
                currentIntent.removeExtra("SELECTED_DEVICE_NAME")
                currentIntent.removeExtra("SELECTED_DEVICE_URL")


            }
            // такого быть не должно
            else {
                closeAllPlaceholders()
                httpsError = false
                webView.loadUrl("about:blank")
                viewManager.updateLabel(null)
                //addressInput.setText("about:blank")

                viewManager.updateLabel()

            }
        }

        if (currentIntent.hasExtra("URL_TO_LOAD")) {
            val urlToLoad = currentIntent.getStringExtra("URL_TO_LOAD")
            Log.d("URL_TO_LOAD", "$urlToLoad")

            if (!urlToLoad.isNullOrEmpty()) {
                openUrl(urlToLoad)
               // webView.loadUrl(urlToLoad)
                //addressInput.setText(urlToLoad)
                viewManager.updateLabel(null, "Github")
                saveLastOpenedLink(urlToLoad, "Github")


            }

            currentIntent.removeExtra("URL_TO_LOAD")
        }

    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent) // обновляем intent
        setupIntentExtra()
    }



    private fun setupView() {
        Log.d("setupView", "=== setupView ===")

        webView = findViewById(R.id.web_view)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        webProgress = findViewById(R.id.web_progress)

        placeholderDeviceUnavailable = findViewById(R.id.placeholder_device_unavailable)
        placeholderNoDevices = findViewById(R.id.placeholder_no_devices)
        placeholderNoDevicesWelcome = findViewById(R.id.placeholder_no_devices_welcome)

        smartLoadingView = findViewById(R.id.btn_add_device)
        shadowLayout = findViewById(R.id.btn_add_device_cont)

        smartLoadingViewWelcome = findViewById(R.id.btn_add_devices_welcome)
        shadowLayoutWelcome =     findViewById(R.id.btn_add_device_conts_welcome)

        smartLoadingViewRefresh = findViewById(R.id.btn_reload)
        shadowLayoutRefresh = findViewById(R.id.btn_reload_cont)

        devicesList = findViewById(R.id.devices_list)

        slidingLayout = findViewById(R.id.sliding_layout)

    }

    private fun setupWebView() {
        Log.d("setupWebView", "=== setupWebView ===")



        // ===  Swipe to Refresh ===
        swipeRefresh.setOnRefreshListener {
            isLoadingFromSwipe = true
            webView.reload()
        }
        swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        swipeRefresh.setSize(SwipeRefreshLayout.DEFAULT)
        swipeRefresh.setProgressViewOffset(false, 100, 200)


        // === НАСТРОЙКА WEBVIEW ===
        webView.settings.apply {

            // Разрешает выполнение JavaScript на страницах.
            javaScriptEnabled = true

            // Разрешает использование DOM Storage API (localStorage/sessionStorage). Необходимо для сайтов,
            // которые сохраняют данные на стороне клиента (например, настройки, состояние сессии).
            domStorageEnabled = true

            // Разрешает использование Web SQL Database API. Хотя это устаревшая технология (предпочтительнее IndexedDB),
            // некоторые старые сайты все еще могут ее использовать.
            databaseEnabled = true

            // --- Доступ к файлам и контенту ---

            // Разрешает WebView доступ к файлам на устройстве через `file:///` URL.
            // загружать локальные HTML-файлы.
            allowFileAccess = true

            // Разрешает WebView доступ к данным через Content-провайдеры, установленные на устройстве.
            allowContentAccess = true

            // --- Кэширование и сетевые запросы ---

            // Устанавливает режим кэширования. LOAD_DEFAULT использует кэш, если он не устарел, иначе загружает из сети.
            // Это стандартное поведение браузера.
            cacheMode = WebSettings.LOAD_DEFAULT

            // Определяет, как обрабатывать "смешанный контент" (когда на HTTPS-странице есть HTTP-ресурсы, например, картинки).
            // MIXED_CONTENT_ALWAYS_ALLOW разрешает загрузку такого контента, что может быть небезопасно,
            // но обеспечивает отображение всех элементов на странице.
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            // --- Идентификация и совместимость ---

            // Устанавливает строку User-Agent, которую WebView будет отправлять серверам.
            // Это позволяет "маскироваться" под полноценный мобильный браузер (в данном случае, Chrome на Pixel 3),
            // чтобы сайты отдавали корректную мобильную верстку.
            userAgentString =
                "Mozilla/5.0 (Linux; Android 10; Pixel 3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

            // ОПАСНАЯ НАСТРОЙКА: Разрешает JavaScript, загруженному из URL `file:///`,
            // выполнять запросы к любым другим источникам (включая `http/https`).
            // Это может быть необходимо для специфических локальных приложений, но создает риски безопасности (XSS-атаки).
            //allowUniversalAccessFromFileURLs = true

            // --- Пользовательский опыт и формы ---

            // Разрешает WebView сохранять данные, введенные пользователем в формы.
            // (Примечание: на новых версиях Android эта настройка может не работать без `importantForAutofill`).
           // saveFormData = true

            // Устаревшая настройка для сохранения паролей. На современных версиях Android
            // автозаполнение паролей управляется системным сервисом автозаполнения.
            //savePassword = true

            // --- Управление отображением (Viewport) ---

            // Разрешает WebView использовать "широкий" viewport, то есть масштабировать HTML-контент так,
            // чтобы он соответствовал ширине экрана, а не отображался в пикселях 1:1.
            useWideViewPort = true

            // Когда useWideViewPort включен, эта настройка заставляет WebView уменьшать масштаб контента
            // так, чтобы вся страница поместилась на экране по ширине.
            loadWithOverviewMode = true
        }


        webView.apply {
            // Делает WebView фокусируемым, чтобы пользователь мог взаимодействовать с элементами на странице
            // с помощью клавиатуры или D-пада.
            isFocusable = true
            isFocusableInTouchMode = true

            // Явно указывает, что View может обрабатывать клики и долгие нажатия.
            isClickable = true
            isLongClickable = true

            // Указывает системе автозаполнения Android, что это поле важно и его нужно обрабатывать.
            // Необходимо для работы автозаполнения логинов, паролей, адресов и т.д.
            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
        }

        // --- Дополнительные настройки ---

        // Устаревшая настройка для поддержки плагинов (например, Flash). На современных версиях Android не имеет эффекта.
       // webView.settings.pluginState = WebSettings.PluginState.ON

        // Включает встроенные элементы управления масштабированием (жесты pinch-to-zoom).
        // Скрывает стандартные кнопки зума (+/-), которые появляются на экране при масштабировании.
        // Оставляет только возможность масштабирования жестами.
        webView.settings.builtInZoomControls = true  // или false, если не хотите кнопки +/-
        webView.settings.displayZoomControls = false
        // Разрешает WebView сохранять свое состояние (историю навигации) при смене конфигурации.
        webView.isSaveFromParentEnabled = true
        webView.isSaveEnabled = true

        // Разрешает отладку содержимого WebView через Chrome DevTools (`chrome://inspect`).
        // ВАЖНО: В релизной версии приложения эту настройку следует отключать (`false`).
        WebView.setWebContentsDebuggingEnabled(true)


        webView.webChromeClient = object : WebChromeClient() {

            // --- УПРАВЛЕНИЕ ПРОГРЕССОМ И ЗАГОЛОВКОМ ---
            /**
             * Вызывается при изменении прогресса загрузки страницы.
             * @param newProgress Прогресс в процентах (0-100).
             */
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // super.onProgressChanged(view, newProgress)

                // if (newProgress < 100) {
                //     webProgress.progress = newProgress
                //     webProgress.visibility = View.VISIBLE
                // } else {
                //     webProgress.visibility = View.GONE
                // }
            }

            /**
             * Вызывается, когда браузер получает заголовок для текущей страницы.
             * @param title Строка с заголовком страницы.
             */
            override fun onReceivedTitle(view: WebView?, title: String?) {
                // super.onReceivedTitle(view, title)

                // if (!title.isNullOrEmpty()) {
                //     labelContainer.text = title
                // }
            }

            /**
             * Вызывается, когда браузер получает иконку (favicon) для текущей страницы.
             * @param icon Bitmap с иконкой сайта.
             */
            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                // super.onReceivedIcon(view, icon)

                 if (icon != null) {
                   //  labelIcon.setImageBitmap(icon)
                     currentWebViewFavicon = icon
                 }
            }

            /**
             * Вызывается, когда страница запрашивает иконку для Apple-устройств ("apple-touch-icon").
             * @param url URL иконки.
             * @param precomposed `true`, если это `apple-touch-icon-precomposed`.
             */
            override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
                // super.onReceivedTouchIconUrl(view, url, precomposed)

            }

            // --- УПРАВЛЕНИЕ ДИАЛОГАМИ JAVASCRIPT ---

            /**
             * Вызывается при вызове `alert()` в JavaScript.
             * @return `true`, если вы обработали диалог, иначе `false`.
             */
            override fun onJsAlert(view: WebView?,url: String?,message: String?,result: JsResult?): Boolean {
                 //super.onJsAlert(view, url, message, result)
//
                 //AlertDialog.Builder(this@MainActivity)
                 //    .setTitle("Alert")
                 //    .setMessage(message)
                 //    .setPositiveButton("OK") { _, _ -> result?.confirm() }
                 //    .setOnCancelListener { result?.cancel() }
                 //    .show()
                 //return true
                return super.onJsAlert(view, url, message, result) // Стандартная обработка
            }

            /**
             * Вызывается при вызове `confirm()` в JavaScript.
             * @return `true`, если вы обработали диалог.
             */
            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                // super.onJsConfirm(view, url, message, result)

                // AlertDialog.Builder(this@MainActivity)
                //     .setTitle("Confirm")
                //     .setMessage(message)
                //     .setPositiveButton("OK") { _, _ -> result?.confirm() }
                //     .setNegativeButton("Cancel") { _, _ -> result?.cancel() }
                //     .setOnCancelListener { result?.cancel() }
                //     .show()
                // return true
                return super.onJsConfirm(view, url, message, result)
            }

            /**
             * Вызывается при вызове `prompt()` в JavaScript.
             * @return `true`, если вы обработали диалог.
             */
            override fun onJsPrompt(view: WebView?,url: String?,message: String?,defaultValue: String?,result: JsPromptResult?): Boolean {
                // super.onJsPrompt(view, url, message, defaultValue, result)
                // val input = EditText(this@MainActivity)
                // input.setText(defaultValue)
                // AlertDialog.Builder(this@MainActivity)
                //     .setTitle("Prompt")
                //     .setMessage(message)
                //     .setView(input)
                //     .setPositiveButton("OK") { _, _ -> result?.confirm(input.text.toString()) }
                //     .setNegativeButton("Cancel") { _, _ -> result?.cancel() }
                //     .setOnCancelListener { result?.cancel() }
                //     .show()
                // return true
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }

            /**
             * Вызывается перед выгрузкой страницы (`onbeforeunload`).
             * @return `true`, если вы обработали диалог.
             */
            override fun onJsBeforeUnload(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                return super.onJsBeforeUnload(view, url, message, result)
            }

            // --- УПРАВЛЕНИЕ ПОЛНОЭКРАННЫМ РЕЖИМОМ (ДЛЯ ВИДЕО) ---

            /**
             * Вызывается, когда веб-контент хочет показать элемент в полноэкранном режиме (например, видео).
             * @param view Элемент для отображения.
             * @param callback Колбэк для управления выходом из полноэкранного режима.
             */
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                // super.onShowCustomView(view, callback)
                // Скрыть основной интерфейс и показать `view` на весь экран.
            }

            /**
             * Вызывается, когда веб-контент выходит из полноэкранного режима.
             */
            override fun onHideCustomView() {
                // super.onHideCustomView()
                // Показать основной интерфейс обратно.
            }

            // --- УПРАВЛЕНИЕ ОКНАМИ ---

            /**
             * Вызывается, когда JavaScript пытается открыть новое окно (`window.open()`).
             * @return `true`, если вы создали новый WebView для обработки запроса.
             */
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                // super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
                // Можно создать новый WebView в диалоге или новом Activity
                // и передать его через `resultMsg`.
                return false
            }

            /**
             * Вызывается, когда JavaScript пытается закрыть окно (`window.close()`).
             */
            override fun onCloseWindow(window: WebView?) {
                // super.onCloseWindow(window)
                // Закрыть Activity или диалог, в котором находится этот WebView.
            }

            // --- УПРАВЛЕНИЕ РАЗРЕШЕНИЯМИ ---

            /**
             * Вызывается, когда страница запрашивает разрешение на доступ к ресурсам (камера, микрофон и т.д.).
             */
            override fun onPermissionRequest(request: PermissionRequest?) {
                 super.onPermissionRequest(request)
                // проигнорирует все запросы разрешений
            }

            /**
             * Вызывается при отмене запроса разрешений.
             */
            override fun onPermissionRequestCanceled(request: PermissionRequest?) {
                // super.onPermissionRequestCanceled(request)

            }

            /**
             * Вызывается, когда страница запрашивает доступ к геолокации.
             */
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                // super.onGeolocationPermissionsShowPrompt(origin, callback)

            }

            /**
             * Вызывается, когда страница больше не нуждается в доступе к геолокации.
             */
            override fun onGeolocationPermissionsHidePrompt() {
                // super.onGeolocationPermissionsHidePrompt()
                // Скрыть UI, связанный с геолокацией.
            }

            // --- ПРОЧЕЕ ---

            /**
             * Позволяет приложению предоставить изображение-постер для видео до его загрузки.
             * @return Bitmap, который будет использоваться как постер.
             */
            override fun getDefaultVideoPoster(): Bitmap? {
                return super.getDefaultVideoPoster()
            }

            /**
             * Позволяет приложению предоставить View, которое будет отображаться во время загрузки видео.
             * @return View для отображения во время загрузки.
             */
            override fun getVideoLoadingProgressView(): View? {
                return super.getVideoLoadingProgressView()
                // val progress = ProgressBar(this@MainActivity)
                // return progress
            }

            /**
             * Получение пути для выбора файлов (при клике на `<input type="file">`).
             * @return `true`, если вы обработали запрос выбора файла.
             */
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@MainActivity.filePathCallback = filePathCallback

                // Создаем Intent для выбора файлов
                val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*" // Базовый тип, если сайт ничего не указал
                }

                // FileChooserParams.createIntent() хорошо справляется с созданием базового Intent,
                // он учитывает и MIME-типы, и возможность множественного выбора.

                try {
                    fileChooserLauncher.launch(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this@MainActivity, "Не найдено приложение для выбора файлов", Toast.LENGTH_SHORT).show()
                    // Важно вернуть false, если запустить не удалось
                    this@MainActivity.filePathCallback?.onReceiveValue(null)
                    this@MainActivity.filePathCallback = null
                    return false
                }

                return true
            }

            /**
             * Вызывается, когда WebView запрашивает фокус.
             */
            override fun onRequestFocus(view: WebView?) {
                // super.onRequestFocus(view)
            }

            /**
             * Вызывается при выводе сообщения в консоль JavaScript (`console.log`, `console.error` и т.д.).
             */
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.d("WebViewConsole", "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                return super.onConsoleMessage(consoleMessage)
            }
        }

        webView.webViewClient = object : WebViewClient() {

            private var hasError: Boolean = false

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(
                    "WebView onPageStarted",
                    "Загрузка страницы: ${url}\n" +
                            "hasError: $hasError"
                )
                hasError = false


                //if (!isLoadingFromSwipe)
                webProgress.visibility = View.VISIBLE
                // hideAnimatedErrorText()
                // swipeRefresh.isRefreshing = true


            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(
                    "WebView onPageFinished",
                    "Финиш страницы: ${url}\n" +
                            "hasError: $hasError"
                )

                webProgress.visibility = View.GONE




                smartLoadingViewRefresh.finishLoading()
                smartLoadingViewWelcome.finishLoading()
                smartLoadingView.finishLoading()

                val strokeWidthDp = 4
                val strokeWidthPx = strokeWidthDp * resources.displayMetrics.density

                Handler(Looper.getMainLooper()).postDelayed({

                    shadowLayoutRefresh.setStrokeWidth(strokeWidthPx)
                    shadowLayoutRefresh.setShadowHidden(false)

                    shadowLayout.setStrokeWidth(strokeWidthPx)
                    shadowLayout.setShadowHidden(false)

                    shadowLayoutWelcome.setShadowHidden(false)
                    shadowLayoutWelcome.setStrokeWidth(strokeWidthPx)


                }, 600)


                if (hasError) {
                    //if (easter_egg && snow_man) {
                    //    val imageUnavailable = findViewById<ImageView>(R.id.image_unavailable)
                    //    imageUnavailable.setImageResource(R.drawable.anim_no_connection_egg)
                    //}
                    //// Если была ошибка, показываем плейсхолдер с сообщением
                    setPlaceholders(unavailable = true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        playUnavailable()
                    }, 1000)

                } else {
                    // Если ошибок не было, показываем загруженный WebView
                    closeAllPlaceholders()
                    eggClickCount = 0

                    //if(webView.url == "about:blank") viewManager.updateLabel(null)


                    val errorText = findViewById<TextView>(R.id.error_text)
                    val errorTxt = "Что-то пошло не так\n"
                    errorText.text = errorTxt
                    errorText.visibility = View.GONE

                }


                isLoadingFromSwipe = false
                swipeRefresh.isRefreshing = false


                Log.d(
                    "onPageFinished",
                    "Обновление цвета заголовка"
                )
                viewManager.getTopColorFromWebView()



                view?.evaluateJavascript(
                    """
                    (function() {
                        if (document.body.dataset.swipeHandlersAdded === 'true') {
                            return;
                        }
                
                        // начало касания
                        document.body.addEventListener('touchstart', function(e) {
                            //  ПРОВЕРКА ПРОКРУТКИ ВСЕЙ СТРАНИЦЫ 
                            const mainScrollElement = document.scrollingElement || document.documentElement;
                            if (mainScrollElement.scrollTop > 0) {
                                // вся страница прокручена отключаем свайп выходим
                                if (window.swipeControl) {
                                    window.swipeControl.setEnabled(false);
                                }
                                return;
                            }
                
                            // только если вся страница наверху.
                            let target = e.target;
                            let canSwipe = true;
                
                            while (target && target !== document.body) {
                                const style = window.getComputedStyle(target);
                                const hasScroll = target.scrollHeight > target.clientHeight;
                                const isScrollable = style.overflowY === 'auto' || style.overflowY === 'scroll';
                                
                                if (isScrollable && hasScroll && target.scrollTop > 0) {
                                    canSwipe = false;
                                    break;
                                }
                                target = target.parentNode;
                            }
                            
                            // Применяем
                            if (window.swipeControl) {
                                window.swipeControl.setEnabled(canSwipe);
                            }
                
                        }, { passive: true });
                
                        // окончание касания 
                        document.body.addEventListener('touchend', function(e) {
                            if (window.swipeControl) {
                                window.swipeControl.setEnabled(true);
                            }
                        }, { passive: true });
                        
                        // флаг
                        document.body.dataset.swipeHandlersAdded = 'true';
                    })();
                    """, null
                )



                viewManager.setUrlText(url?: "")
                if (url != null && !httpsError) {
                    when {
                        url.startsWith("https://") -> {
                            // Соединение безопасное (HTTPS)
                            Log.d("WebViewConnection", "Страница загружена по безопасному протоколу HTTPS: $url")
                             viewManager.updateConnectionIcon("https")
                        }
                        url.startsWith("http://") -> {
                            // Соединение небезопасное (HTTP)
                            Log.w("WebViewConnection", "ВНИМАНИЕ: Страница загружена по небезопасному протоколу HTTP: $url")
                            viewManager.updateConnectionIcon("http")
                        }
                        else -> {
                            // Другие случаи, например, 'about:blank', 'file://', и т.д.
                            Log.d("WebViewConnection", "Страница не является веб-страницей (протокол: $url)")
                            viewManager.updateConnectionIcon("https") //
                        }
                    }
                }
                else if(httpsError){
                    viewManager.updateConnectionIcon("аааашибка")
                }
                else {
                    viewManager.updateConnectionIcon("http")
                }


                zoomFactor = isZoomed

            }

            override fun onReceivedHttpAuthRequest(
                view: WebView?,
                handler: HttpAuthHandler,
                host: String?,
                realm: String?
            ) {
                //  View для диалога
                val dialogView = layoutInflater.inflate(R.layout.dialog_auth, null)
                val loginInput = dialogView.findViewById<EditText>(R.id.login_input)
                val passwordInput = dialogView.findViewById<EditText>(R.id.password_input)

                val dialog = AlertDialog.Builder(this@MainActivity, R.style.AppTheme_RoundedAlertDialogErr)
                    .setTitle("Требуется авторизация")
                    .setMessage("Для доступа к $host")
                    .setView(dialogView)
                    .setPositiveButton("Войти") { _, _ ->
                        val login = loginInput.text.toString()
                        val password = passwordInput.text.toString()
                        // Передаем учетные данные обратно в WebView
                        handler.proceed(login, password)
                    }
                    .setNegativeButton("Отмена") { _, _ ->
                        // Пользователь отменил ввод
                        handler.cancel()
                        // показать плейсхолдер?
                    }
                    .setOnCancelListener {
                        // Срабатывает при нажатии вне диалога или на кнопку "Назад"
                        handler.cancel()
                        // playUnavailable(true)
                    }
                    .create()

                dialog.show()
            }


            override fun onReceivedError(
                view: WebView?, request: WebResourceRequest?, error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)




                if (request?.isForMainFrame == true) {
                    hasError = true

                    webProgress.visibility = View.GONE

                    smartLoadingViewRefresh.finishLoading()
                    smartLoadingViewWelcome.finishLoading()
                    smartLoadingView.finishLoading()
                    val strokeWidthDp = 4
                    val strokeWidthPx = strokeWidthDp * resources.displayMetrics.density
                    android.os.Handler(Looper.getMainLooper()).postDelayed({

                        shadowLayoutRefresh.setStrokeWidth(strokeWidthPx)
                        shadowLayoutRefresh.setShadowHidden(false)

                        shadowLayout.setStrokeWidth(strokeWidthPx)
                        shadowLayout.setShadowHidden(false)

                        shadowLayoutWelcome.setShadowHidden(false)
                        shadowLayoutWelcome.setStrokeWidth(strokeWidthPx)


                    }, 600)


                    if (easter_egg && snow_man) {
                        val imageUnavailable = findViewById<ImageView>(R.id.image_unavailable)
                        imageUnavailable.setImageResource(R.drawable.anim_no_connection_egg)
                    }

                    if (placeholderDeviceUnavailable.isVisible) {
                        showAnimatedErrorText(error?.description.toString(), true)
                    }
                    // показываем плейсхолдер
                    setPlaceholders(unavailable = true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        playUnavailable()
                    }, 1000)


                    val userFriendlyError = getErrorDescription(error?.errorCode)
                    Log.d("WebViewError", userFriendlyError)

                    Log.e(
                        "WebViewError onReceivedError",
                        "Ошибка загрузки страницы: ${request.url}\n" +
                                "Код ошибки: ${error?.errorCode}\n" +
                                "Описание: ${error?.description}"
                    )


                }



                isLoadingFromSwipe = false
                swipeRefresh.isRefreshing = false


                //if (request?.isForMainFrame == true) {
                //    //isShowingPlaceholder = true
//
                //    if (easter_egg && snow_man) {
                //        val imageUnavailable = findViewById<ImageView>(R.id.image_unavailable)
                //        imageUnavailable.setImageResource(R.drawable.anim_no_connection_egg)
                //    }
                 //  setPlaceholders(unavailable = true)
                //    //webView.visibility = View.GONE
                //}

            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                // --- Формирование информативного сообщения для пользователя ---
                Log.e("SSL_DEBUG", "Пришла ошибка: ${error.primaryError}. Константа SSL_EXPIRED: ${SslError.SSL_EXPIRED}")

                // тип SSL ошибки
                val errorDescription = when (val primaryError = error.primaryError) { //
                    SslError.SSL_NOTYETVALID -> "Сертификат еще не действителен."
                    SslError.SSL_EXPIRED -> "Срок действия сертификата истек."
                    SslError.SSL_IDMISMATCH -> "Имя сайта не совпадает с сертификатом."
                    SslError.SSL_UNTRUSTED -> "Сертификат не является доверенным."
                    SslError.SSL_DATE_INVALID -> "Дата сертификата недействительна." // Общая ошибка, связанная с датой
                    SslError.SSL_INVALID -> "Общая/неизвестная ошибка сертификата, код: $primaryError" // Самая общая ошибка


                    else -> "Непредвиденная ошибка сертификата, код: $primaryError"
                }

                // Создаем HTML-форматированный текст для сообщения.
                // Это позволяет использовать жирный шрифт и переносы строк для лучшей читаемости.
                val dialogMessageHtml = """
                     <br>
                     <font color='#D32F2F'><b>&#9888; Внимание: небезопасное подключение</b></font>
                     <br><br>
                     &#8505;&#65039; <b>Причина:</b> $errorDescription
                     <br><br>
                     Вы действительно хотите продолжить?
                     <br><br>
                     <i>&#128737; Это может быть безопасно для известных Вам локальных устройств (например, роутера), но не рекомендуется для публичных сайтов.</i>
                 """.trimIndent()


                val builder = AlertDialog.Builder(view.context, R.style.AppTheme_RoundedAlertDialogErr)

                // Устанавливаем заголовок и отформатированное сообщение
                builder.setTitle("Предупреждение безопасности")
                builder.setMessage(Html.fromHtml(dialogMessageHtml, Html.FROM_HTML_MODE_LEGACY))


                //    Вызов handler.proceed() разрешает WebView проигнорировать ошибку и загрузить страницу.
                builder.setPositiveButton("Продолжить") { _, _ ->
                    viewManager.updateConnectionIcon("https_err")
                    httpsError = true
                    handler.proceed()
                }

                // Отмена"
                //    Вызов handler.cancel() прерывает загрузку.
                builder.setNegativeButton("Отмена") { _, _ ->
                    httpsError = false
                    handler.cancel()
                }

                // Показываем готовый диалог пользователю
                builder.show()
            }


            override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
                // newScale > 1.0 — страница увеличена (зум > 100%)
                // newScale == 1.0 — нормальный масштаб (зум убран)
                swipeRefresh.isEnabled = newScale <= 1.0f
                isZoomed = newScale

                Log.d("WebViewScaleChanged", "Зум страницы:\n" +
                                                         "    swipeRefresh.isEnabled: ${swipeRefresh.isEnabled}\n" +
                                                         "    isZoomed: $isZoomed\n" +
                                                         "    zoomFactor: $zoomFactor" )


            }

        }



    // подключаем жаваскрипт в финиш
        webView.addJavascriptInterface(SwipeRefreshJavaScriptInterface(), "swipeControl")
        webView.clearCache(true)
        webView.clearHistory()
        webView.requestFocus()




    }



    /**
     * мост между JavaScript и Kotlin.
     * JavaScript сможет вызывать его методы.
     */
    inner class SwipeRefreshJavaScriptInterface {
        @JavascriptInterface
        fun setEnabled(enabled: Boolean) {
            // в главном потоке
            runOnUiThread {
               if (isZoomed <= zoomFactor) swipeRefresh.isEnabled = enabled
            }
        }
    }

    fun checkPermissionAndShowUpdate(newVersion: String) {
        //  только для API 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            pendingUpdateVersion = newVersion

            when {
                // Разрешение есть
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    displayTheNotification(newVersion)
                    pendingUpdateVersion = null
                }

                // когда нужно объяснение
                else -> {

                    AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialog)
                        .setTitle("Новая версия RSM!")
                        .setMessage("Нужно разрешение для уведомлений!\n\nПри старте приложение будет проверять нет ли новой версии на гитхаб, и если есть покажет уведомление.\n" +
                                    "Можно изменить в настройках")
                        .setPositiveButton("Хорошо, сейчас разрешу") { _, _ ->

                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                        }
                        //.setNegativeButton("Нет, спасибо", null)
                        .setNeutralButton("Не проверять обновления") { _, _ ->
                            disableUpdateChecks()

                        }
                        .setOnDismissListener {

                        }
                        .show()
                }
            }
        } else {
            // API 32  разрешение не требуется,
            displayTheNotification(newVersion)
        }
    }

    private fun disableUpdateChecks() {

        check_version = false
        SettingsManager.isCheckNewVersionEnabled = false


        Toast.makeText(this, "Проверка обновлений отключена", Toast.LENGTH_SHORT).show()

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            firstStartPlay()
        }, 1000)
    }

    private fun displayTheNotification(newVersion: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "update_channel", "Обновления приложения", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Уведомления о новых версиях RSM"
            setShowBadge(true)
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, "update_channel")
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("🎉 Новая версия RSM!")
            .setContentText("Доступна версия $newVersion")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Обновление RSM до версии $newVersion уже доступно!\n\nНажмите для скачивания")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(createDownloadPendingIntent(newVersion))
            .build()

        notificationManager.notify(1001, notification)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Пользователь дал разрешение. Проверяем, есть ли версия, которую нужно показать.
                pendingUpdateVersion?.let { version ->
                    displayTheNotification(version)
                    // Очищаем переменную после использования
                    pendingUpdateVersion = null
                }
            } else {
                // Пользователь отказал в разрешении.
                Toast.makeText(
                    this,
                    "Уведомления выключены. Отключите проверку версии в настройках!",
                    Toast.LENGTH_LONG
                ).show()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                firstStartPlay()
            }, 1000)
        }

    // Ссылка
    private fun createDownloadPendingIntent(version: String): PendingIntent {
        val downloadUrl =
            "https://github.com/TonTon-Macout/Remote-Settings-Manager/releases/latest/"
        val intent = Intent(Intent.ACTION_VIEW, downloadUrl.toUri())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun openScanButton(view: View) {
        Log.d("openScanButton", "открываем окно сканирование устройств")
        shadowLayout.setShadowHidden(true)
        shadowLayout.setStrokeWidth(0f)
        smartLoadingView.startLoading()

        shadowLayoutWelcome.setShadowHidden(true)
        shadowLayoutWelcome.setStrokeWidth(0f)
        smartLoadingViewWelcome.startLoading()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, DeviceScanActivity::class.java)

            // анимация перехода из кнопки
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainActivity,
                view,
                "button_transition" // уникальное имя перехода
            ).toBundle()

            startActivity(intent, options)
            smartLoadingView.finishLoading()
            smartLoadingViewWelcome.finishLoading()

        }, 800)
    }

    fun openReloadButton(view: View) {
        //webProgress.visibility = View.VISIBLE
        val errorText = findViewById<TextView>(R.id.error_text)
        val errorTxt = ""

        errorText.text = errorTxt
        hideAnimatedErrorText()
        //errorText.visibility = View.GONE


        shadowLayoutRefresh.setShadowHidden(true)
        shadowLayoutRefresh.setStrokeWidth(0f)
        smartLoadingViewRefresh.startLoading()

        Handler(Looper.getMainLooper()).postDelayed({
            openUrl(viewManager.getUrlFronAddressField(), false)

           // webView.loadUrl(viewManager.getUrlFronAddressField())
            
        }, 1000)


    }

    private fun playUnavailable(force: Boolean = false) {

        if (!force)
            if (!snow_man || !snow_man_animation) return

        val imageUnavailable = findViewById<ImageView>(R.id.image_unavailable)

        //  Получаем Drawable
        val drawable = imageUnavailable.drawable

        //  Проверяем, что он  анимированный
        if (drawable is AnimatedImageDrawable) {

            //  Если анимация уже идет остановить
            if (drawable.isRunning) {
                drawable.stop()
            }

            //  Количество повторов и запускаем анимацию
            drawable.repeatCount = 0
            drawable.start()
        } else {
            Log.e("MainActivity", "Drawable не является анимированным")
        }

    }

    fun egg(view: View) {
        if (snow_man || !snow_man_animation) playUnavailable(true)
        eggClickCount++

        // тост за текущее количество нажатий
        //if (eggClickCount > 6) Toast.makeText(this, "$eggClickCount", Toast.LENGTH_SHORT).show()

        if (eggClickCount > 3) {
            val attrs = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
            withStyledAttributes(null, attrs) {
                val foregroundDrawable = getDrawable(0)
                val imageUnavailable = findViewById<ImageView>(R.id.image_unavailable)
                imageUnavailable.foreground = foregroundDrawable
            }
        } else {
            val imageUnavailable = findViewById<ImageView>(R.id.image_unavailable)
            imageUnavailable.foreground = null
        }




        if (eggClickCount == 10) {
            var eggText = ""
            var egg = false

            //  val packageManager = packageManager
            //  val packageName = packageName
            //  val snowAlias = ComponentName(packageName, ".MainActivitySnow")
            //  val snowAliasState = packageManager.getComponentEnabledSetting(snowAlias)
            //  val isSnowAliasDisabled = snowAliasState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
            //          snowAliasState == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT


            val imageUnavailable = findViewById<ImageView>(R.id.image_unavailable)

            if (!snow_man) {
                imageUnavailable.animate()
                    .alpha(0f) // делаем картинку прозрачной
                    .setDuration(400) // Длительность затухания
                    .withEndAction {
                        // Когда затухание завершено:
                        imageUnavailable.setImageResource(R.drawable.anim_no_connection_egg) // Меняем ресурс картинки

                        val drawable = imageUnavailable.drawable
                        if (drawable is AnimatedImageDrawable) {
                            // Если да - запускаем анимацию
                            drawable.repeatCount = 0
                            drawable.start()
                        }
                        // Плавно проявляем новую
                        imageUnavailable.animate()
                            .alpha(1f)
                            .setDuration(2000)
                            .start()
                    }.start()
            } else {

                val drawable = imageUnavailable.drawable

                //  Проверяем, что он  анимированный
                if (drawable is AnimatedImageDrawable) {

                    //  Если анимация уже идет остановить
                    if (drawable.isRunning)  drawable.stop()


                    //  Количество повторов
                    drawable.repeatCount = 0
                    drawable.start()
                } else {
                    Log.e("MainActivity", "imageUnavailable не является анимированным")
                }
            }


            //активироовна и используется
            if (snow_man && easter_egg) {
                eggText = "<b>Вы уже открыли уникальную тему!</b><br><br>Больше ничего нет."
                ThemeManager.setTheme(this, "yellow")
                ThemeManager.applyTheme(this)
                //recreate()
            }
            // активирована не используется
            else if (!snow_man && easter_egg) {
                eggText =
                    "<b>И снова привет!</b><br>Сейчас применим уникальную тему снова.<br>Приложение закроется, запусти его заново!<br>3..2..1..."
                egg = true
                SettingsManager.isSnowmanEnabled = true
                snow_man = true
            }
            // не активирована и не используется
            else {
                eggText =
                    "<b>Поздравляю!!!</b><br>Вы открыли уникальную тему!<br>Сейчас приложение закроется, чтобы применить изменение.<br>Запусти его заново!" +
                            "<br>3..2..1...\n"
                egg = true
            }

            SettingsManager.isSnowmanEnabled = true
            snow_man = true
            easter_egg = true

            showAnimatedErrorText(eggText, false)
            eggClickCount = 0

            // меняем алиас и закрывамся
            if (egg) Handler(Looper.getMainLooper()).postDelayed({

                SettingsManager.isSnowmanEnabled = true
                snow_man = true
                SettingsManager.isEasterEggEnabled = true
                easter_egg = true


                ThemeManager.setTheme(this, "yellow")
                ThemeManager.applyTheme(this)

                updateAppIcon()

            }, 12000)


        }
    }


    private fun getErrorDescription(errorCode: Int?): String {
        return when (errorCode) {
            // Ошибки сети
            WebViewClient.ERROR_HOST_LOOKUP -> "Не удалось найти сервер (ошибка DNS)."
            WebViewClient.ERROR_CONNECT -> "Не удалось подключиться к серверу. Проверьте адрес и сеть."
            WebViewClient.ERROR_TIMEOUT -> "Время ожидания ответа от сервера истекло."
            WebViewClient.ERROR_AUTHENTICATION -> "Ошибка аутентификации на сервере."
            WebViewClient.ERROR_PROXY_AUTHENTICATION -> "Ошибка аутентификации на прокси-сервере."
            WebViewClient.ERROR_REDIRECT_LOOP -> "Сайт постоянно перенаправляет запрос (зацикливание)."
            WebViewClient.ERROR_IO -> "Произошла ошибка при передаче данных (разрыв соединения)."

            // Ошибки SSL (безопасного соединения)
            WebViewClient.ERROR_FAILED_SSL_HANDSHAKE -> "Ошибка безопасного SSL-соединения."
            WebViewClient.ERROR_BAD_URL -> "Некорректный или неверно сформированный адрес (URL)."
            //WebViewClient.ERROR_SSL_CERT_INVALID -> "Сертификат безопасности сайта недействителен."
            //WebViewClient.ERROR_SSL_DATE_INVALID -> "Дата на устройстве неверна, что мешает установить безопасное соединение."
            //WebViewClient.ERROR_SSL_IDMISMATCH -> "Имя сайта не совпадает с его сертификатом безопасности."
            // WebViewClient.ERROR_SSL_UNTRUSTED -> "Сертификат безопасности сайта не является доверенным."

            // Ошибки доступа
            WebViewClient.ERROR_FILE_NOT_FOUND -> "Необходимый локальный файл не найден."
            WebViewClient.ERROR_FILE -> "Общая ошибка при доступе к файлу."
            WebViewClient.ERROR_UNSUPPORTED_SCHEME -> "Данный тип ссылки не поддерживается в приложении."

            // Общая ошибка
            WebViewClient.ERROR_UNKNOWN -> "Произошла неизвестная ошибка."
            else -> "Произошла неизвестная ошибка." // Сообщение по умолчанию
        }
    }

    private fun showAnimatedErrorText(errorMessage: String, errors: Boolean = true) {
        val errorTextView = findViewById<TextView>(R.id.error_text)


        var finalText = if (errors) {
            "<b>Ошибка</b>:<br><br>$errorMessage"
        } else {
            "$errorMessage\n\n"
        }
        finalText = finalText.replace("net::", "")
        val formattedText = HtmlCompat.fromHtml(finalText, HtmlCompat.FROM_HTML_MODE_LEGACY)

        errorTextView.apply {
            text = formattedText

            // Начальное состояние: прозрачный и уменьшенный
            alpha = 0f
            scaleX = 0.7f
            scaleY = 0.7f
            visibility = View.VISIBLE

            // Запускаем анимацию
            animate()
                .setStartDelay(600)
                .alpha(1f)       // до полной непрозрачности
                .scaleX(1f)      // до нормального размера по горизонтали
                .scaleY(1f)      // до нормального размера по вертикали
                .setDuration(1400) // Длительность
                .setInterpolator(OvershootInterpolator(2.0f)) // пружинящий эффект
                .setListener(null)
                .start()
        }
    }
    private fun hideAnimatedErrorText() {
        val errorTextView = findViewById<TextView>(R.id.error_text)
        if (errorTextView.isVisible) {
            errorTextView.animate()
                .alpha(0f) // до полной прозрачности
                .setDuration(800)
                .withEndAction {
                    // После анимации делаем View невидимым
                    errorTextView.visibility = View.INVISIBLE
                }
                .start()
        }
    }


    fun addDevice(view: View) {

        if(currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT ||
            currentNetworkFingerprint == null ||
            currentNetworkFingerprint == App.NULL_NETWORKS_FINGERPRINT){
            Toast.makeText(this@MainActivity, "Не удалось добавить устройство!\nНеизвестная сеть.", Toast.LENGTH_LONG).show()
            return
        }
        //showAddDeviceDialog()


        var urlFromWebView: String? = viewManager.getUrlFronAddressField()
        if(urlFromWebView == null || urlFromWebView.isEmpty() || urlFromWebView == "about:blank") urlFromWebView = webView.url

        showAddDeviceDialog(
            context = this,
            currentNetworkFingerprint = currentNetworkFingerprint,
            allDevices = allDevices, // Пример
            currentUrl = urlFromWebView, // <-- ПЕРЕДАЕМ URL
            onDeviceCreated = { newlyCreatedDevice ->
                // Этот код выполнится, когда пользователь нажмет "Добавить"
                Log.d("MainActivity", "Диалог вернул новое устройство: ${newlyCreatedDevice.name}")


                addManualDevice(newlyCreatedDevice.name, newlyCreatedDevice.url, newlyCreatedDevice.networkName?:"---", newlyCreatedDevice.networkId?:"---")
                webProgress.visibility = View.VISIBLE
                openDevice(newlyCreatedDevice)
            }
        )

    }

    fun refreshDevice(view: View) {

        openUrl(viewManager.getUrlFronAddressField())
        // Закрываем клавиатуру
        hideKeyboard(view)
        closePanel()

    }


    private fun isDeviceExists(url: String): Boolean {
        Log.d("isDeviceExistsForBtn", "Проверка существования для URL: '$url'")

        return try {
            // Регулярное выражение для поиска порта (:8080) сразу после хоста.
            // Оно ищет последовательность :<цифры>, за которой следует / или конец строки.
            val portRegex = Regex(":\\d+(?=[/?#]|$)")

            // 1. Очищаем URL, который мы ищем, только от порта и конечного слэша
            val urlToCheck = url.replace(portRegex, "").removeSuffix("/")
            Log.d("isDeviceExistsForBtn", "URL для проверки: '$urlToCheck'")

            // 2. Ищем совпадение в списке
            val result = allDevices.any { device ->
                // 3. Точно так же очищаем URL каждого устройства в списке
                val deviceUrl = device.url.replace(portRegex, "").removeSuffix("/")

                Log.d("isDeviceExistsForBtn", "Сравниваем с устройством: '${device.name}' | Обработанный URL: '$deviceUrl'")

                // 4. Сравниваем URL, игнорируя регистр (на случай, если где-то http, а где-то HTTP)
                val match = deviceUrl.equals(urlToCheck, ignoreCase = true)
                if (match) {
                    Log.d("isDeviceExistsForBtn", "Найдено совпадение!")
                }
                match
            }

            Log.d("isDeviceExistsForBtn", "Результат проверки: $result")
            result

        } catch (e: Exception) {
            Log.e("isDeviceExistsForBtn", "Критическая ошибка при проверке: '$url'", e)
            false // В случае любой ошибки считаем, что устройства нет.
        }
    }






    private fun addManualDevice(name: String, url: String, networkName: String, networkId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {




                val host = url.removePrefix("http://").removePrefix("https://").substringBefore("/")
                val faviconPath = downloadFavicon(host)

                val device = Device(name, url, faviconPath, false, networkName, networkId, false)

                withContext(Dispatchers.Main) {

                     if (addDeviceToList(device)) {
                        Toast.makeText(
                            this@MainActivity,
                            "Устройство добавлено",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("MainActivity", "Device added: ${device.name}, url ${device.url}, networkName ${device.networkName}")

                        // Обновляем список устройств
                        loadDevices(currentNetworkFingerprint)

                        // Закрываем панель
                        slidingLayout.panelState = PanelState.COLLAPSED
                    } else {
                        Toast.makeText(this@MainActivity, "Устройство уже есть", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Ошибка добавления устройства",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addDeviceToList(device: Device): Boolean {
        if (allDevices.none { it.url == device.url }) {
            allDevices.add(0, device)
            deviceManager.saveDevices(allDevices, currentNetworkFingerprint ?: App.DEFAULT_NETWORK_FINGERPRINT)
           // deviceManager.saveDevice(device, currentNetworkFingerprint ?: App.DEFAULT_NETWORK_FINGERPRINT)

            //saveDevices()
            return true
        }
        return false
    }

    private suspend fun downloadFavicon(host: String, port: Int? = null): String? =
        withContext(Dispatchers.IO) {
            val baseUrl = if (port != null) "http://$host:$port" else "http://$host"
            Log.d("FaviconDebug", "=== Поиск фавикона для $baseUrl ===")

            val directPaths = listOf(
                "/favicon.ico",
                "/favicon.png",
                "/apple-touch-icon.png",
                "/apple-touch-icon-precomposed.png"
            )

            for (path in directPaths) {
                val urlStr = baseUrl + path
                Log.d("FaviconDebug", "Проверяем путь: $urlStr")
                val result =
                    tryDownloadAndSaveFavicon(urlStr, host, path.substringAfterLast("/"), port)
                if (result != null) {
                    Log.d("FaviconDebug", "Фавикон найден: $path")
                    return@withContext result
                }
            }

            try {
                val url = URL(baseUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                conn.instanceFollowRedirects = true
                conn.connect()

                if (conn.responseCode == 200) {
                    val html = conn.inputStream.bufferedReader().use { it.readText() }
                    Log.d("FaviconDebug", "HTML загружен: ${html.length} символов")
                    val doc = Jsoup.parse(html, baseUrl)

                    val relSelectors = listOf(
                        "link[rel~=(?i)^(icon|shortcut icon|apple-touch-icon|apple-touch-icon-precomposed)\$]",
                        "link[href*=.ico], link[href*=.png], link[href*=.svg], link[href*=.jpg], link[href*=.jpeg]"
                    )

                    for (selector in relSelectors) {
                        val links = doc.select(selector)
                        Log.d("FaviconDebug", "Найдено ${links.size} по: $selector")
                        for (link in links) {
                            val href = link.attr("abs:href")
                            if (href.isNotEmpty()) {
                                Log.d("FaviconDebug", "Проверяем: $href")
                                val result = tryDownloadAndSaveFavicon(
                                    href,
                                    host,
                                    href.substringAfterLast("/").takeIf { it.isNotEmpty() }
                                        ?: "favicon",
                                    port)
                                if (result != null) {
                                    Log.d("FaviconDebug", "Фавикон загружен: $href")
                                    return@withContext result
                                }
                            }
                        }
                    }
                } else {
                    Log.d("FaviconDebug", "HTML не загружен: HTTP ${conn.responseCode}")
                }
            } catch (e: Exception) {
                Log.e("FaviconDebug", "Ошибка HTML: ${e.message}")
            }

            Log.d("FaviconDebug", "Фавикон НЕ найден для $baseUrl")
            return@withContext null
        }

    private fun tryDownloadAndSaveFavicon(
        urlStr: String,
        host: String,
        filename: String,
        port: Int? = null
    ): String? {
        var inputStream: InputStream? = null
        try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.instanceFollowRedirects = true
            conn.setRequestProperty("Accept", "image/*,*/*;q=0.8")
            conn.connect()

            if (conn.responseCode == 200) {
                val contentType = conn.contentType?.lowercase() ?: ""
                if (contentType.contains("text/html")) {
                    Log.d("FaviconDebug", "Пропуск: HTML вместо изображения ($contentType)")
                    return null
                }

                inputStream = conn.inputStream
                val bytes = inputStream.readBytes()
                if (bytes.isEmpty() || bytes.size > 500_000) {
                    Log.d("FaviconDebug", "Пропуск: пусто или слишком большой файл (${bytes.size})")
                    return null
                }

                Log.d("FaviconDebug", "Скачано: $urlStr | $contentType | ${bytes.size} байт")

                val ext = when {
                    contentType.contains("svg") -> ".svg"
                    contentType.contains("png") -> ".png"
                    contentType.contains("jpeg") || contentType.contains("jpg") -> ".jpg"
                    contentType.contains("ico") -> ".ico"
                    else -> ".bin"
                }

                val cleanName = filename.replace(Regex("[^a-zA-Z0-9._-]"), "_") + ext
                val fileIdentifier = if (port != null) "${host}_$port" else host
                val file = File(filesDir, "favicon_${fileIdentifier}_$cleanName")

                FileOutputStream(file).use { it.write(bytes) }

                // SVG конвертация (если нужно)
                if (ext == ".svg") {
                    val pngFile = File(
                        filesDir,
                        "favicon_${fileIdentifier}_${cleanName.substringBeforeLast(".")}.png"
                    )
                    if (renderSvgToPng(file, pngFile)) {
                        file.delete()
                        return pngFile.absolutePath
                    } else {
                        Log.w("FaviconDebug", "SVG не отрендерен, сохранён как .svg")
                        return file.absolutePath
                    }
                }

                return file.absolutePath
            } else {
                Log.d("FaviconDebug", "HTTP ${conn.responseCode}: $urlStr")
            }
        } catch (e: Exception) {
            Log.d("FaviconDebug", "Ошибка загрузки $urlStr: ${e.message}")
        } finally {
            inputStream?.close()
        }
        return null
    }

    private fun renderSvgToPng(svgFile: File, pngFile: File): Boolean {
        return try {
            val svg = SVG.getFromInputStream(FileInputStream(svgFile))
            val size = 64f
            svg.documentWidth = size
            svg.documentHeight = size

            val bitmap = createBitmap(size.toInt(), size.toInt())
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            svg.renderToCanvas(canvas)

            FileOutputStream(pngFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            Log.d("FaviconDebug", "SVG отрендерен в PNG")
            true
        } catch (e: Exception) {
            Log.e("FaviconDebug", "Ошибка рендеринга SVG: ${e.message}")
            false
        }
    }


    private fun updateBlueBackgroundAlpha(bckgrnd: Boolean) {
        val corner = findViewById<com.lihang.ShadowLayout>(R.id.devices_list_cont)
        val displayMetrics = resources.displayMetrics
        val cornerRadiusInPx = (32 * displayMetrics.density).toInt() // Конечный радиус в пикселях

        // начальное и конечное значения
        val startRadius = if (bckgrnd) 0 else cornerRadiusInPx
        val endRadius = if (bckgrnd) cornerRadiusInPx else 0

        val cornerAnimator = ValueAnimator.ofInt(startRadius, endRadius)
        cornerAnimator.duration = 400 // Длительность анимации в миллисекундах (можете настроить)
        cornerAnimator.interpolator = AccelerateDecelerateInterpolator() // Плавное начало и конец

        // Добавляем слушатель, который будет вызываться на каждом кадре анимации
        cornerAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            // Устанавливаем текущее (анимированное) значение радиуса для верхних углов
            corner.setSpecialCorner(animatedValue, animatedValue, 0, 0)
        }

        // Запускаем
        cornerAnimator.start()

    }




    /**
     * Загружает устройства для указанной сети
     * @param networkFingerprint Отпечаток сети, для которой нужно загрузить устройства.
     *                           Если null, загружаются все устройства
     */
    private fun loadDevices(networkFingerprint: String?) {
        Log.d("loadDevices", "Загружаем устройства для fingerprint: '$networkFingerprint'")
        try {

            allDevices.clear()


            val loadedDevices =
                when (networkFingerprint) {
                    App.ALL_NETWORKS_FINGERPRINT -> {
                        deviceManager.loadAllDevicesWithDuplicates()
                    }
                    App.DEFAULT_NETWORK_FINGERPRINT -> {
                        deviceManager.loadDevices(networkFingerprint)
                    }
                    App.CELLULAR_NETWORKS_FINGERPRINT -> {
                        deviceManager.loadDevices(networkFingerprint)
                    }
                    null -> {
                        Log.e("loadDevices", "Загружено ${allDevices.size} устройств для fingerprint: null")
                        (devicesList.adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()
                        return
                    }
                    else -> {
                        deviceManager.loadDevices(networkFingerprint)

                    }
                }

            Log.d("loadDevices", "Загружено ${loadedDevices.size} устройств для fingerprint: '${networkFingerprint ?: "all"}'")


            allDevices.addAll(loadedDevices)
            (devicesList.adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()



        } catch (e: Exception) {
            Log.e("loadDevices", "Ошибка загрузки устройств", e)
            Toast.makeText(this, "Ошибка загрузки устройств: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }




    /**
     *
     * нужно дождаться результата
     *
     * @param actionToPerform будет выполнено после
     *                        того, как сеть будет определена.
     */
    private fun getNetworkAndContinue(actionToPerform: (KnownNetwork?) -> Unit) {
        lifecycleScope.launch {
            val network = detectNewNetworkAsync()
            actionToPerform(network)
        }
    }

    /**
     * ОПРЕДЕЛЕНИЕ СЕТИ
     *
     */
    private suspend fun detectNewNetworkAsync(): KnownNetwork? =
        suspendCancellableCoroutine { continuation ->
            val logTag = "detectNewNetworkAsync"

            // Коллбэк для завершения корутины.
            val onDecision: (KnownNetwork?) -> Unit = { network ->
                if (continuation.isActive) {
                    continuation.resume(network)
                }
            }

            splitNetwork = SettingsManager.isSplitNetworkEnabled
            splitNetworkManual = SettingsManager.isSplitNetworkManualEnabled

            if (!splitNetwork || splitNetworkManual) {
                onDecision(null)
                return@suspendCancellableCoroutine
            }

            // === для начала проверяем разрешения
            if (!checkPermissions()) {

                Log.e(logTag, "Нет разрешений!!!")
                onDecision(null)
                return@suspendCancellableCoroutine
            }



            // === геолокация
            if(!isLocationEnabled()) {
                if (isLocationDialogShowing) {
                    Log.e(logTag, "Диалог геолокации уже показан.")
                    onDecision(null)
                    return@suspendCancellableCoroutine
                }

                isLocationDialogShowing = true
                showLocationDialog(this, locationSettingsLauncher) { splitEnabled, manualMode ->
                    if(splitEnabled != null)  splitNetwork = splitEnabled
                    if(manualMode != null)  splitNetworkManual = manualMode

                    Log.d(logTag, "Dialog Location - callback: split=$splitEnabled, manual=$manualMode")
                    isLocationDialogShowing = false

                    onDecision(null)     // И завершаем корутину

                }
                return@suspendCancellableCoroutine
            }

            // === Получаем текущие бсид и ccbl
            val (ssid, bssid, subnet) = networkAnalyzer.getNetworkInfo()
            Log.d(logTag, "networkAnalyzer вернул SSID: $ssid, BSSID: $bssid")
            if (bssid == null || ssid == null) {
                Log.e(logTag, "Данные не получены!!!")
                onDecision(null)
                return@suspendCancellableCoroutine
            }



         ////////////////////////



            val bssidPrefix = FingerprintGenerator.getBssidPrefix(bssid)
            val network = NetworkManager.findNetworkByBssidPrefix(this, bssidPrefix)

            if(bssidPrefix == null || ssid == null) {
                Log.e(logTag, "bssidPrefix == null || ssid == null")
                onDecision(null)
                return@suspendCancellableCoroutine
            }

            when {
                network?.isTrusted == true -> {
                    Log.d(logTag, "Сеть известна и доверенная.")

                    onDecision(network)
                    return@suspendCancellableCoroutine
                }

                network != null && !network.isTrusted -> {
                    Log.d(logTag, "Сеть известна, недоверенная.")

                    onDecision(network)
                    return@suspendCancellableCoroutine
                }

                network == null -> {
                    Log.d(logTag, "Точная сеть не найдена. Проверяем возможных кандидатов на объединение...")

                    // Генерируем текущие параметры для сравнения
                    //val currentFingerprint = FingerprintGenerator.generate(this, scanResult)
                    val currentBssidPrefix = bssidPrefix
                    val currentSsid = ssid

                   // val currentSubnetStr1 = FingerprintGenerator.getNetworkSubnet(this)?.removePrefix("subnet=")
                   val currentSubnetStr = networkAnalyzer.getCurrentSubnet()
                    // Ищем сети-кандидаты: совпадает SSID + подсеть, но BSSID другой
                    val candidateNetworks = NetworkManager.getKnownNetworks(this).filter { known ->
                        val knownSsid = FingerprintGenerator.extractSsid(known.fingerprint)
                        val knownSubnet = known.subnet

                        knownSsid == currentSsid &&
                                knownSubnet == currentSubnetStr &&
                                known.mainBssidPrefix != currentBssidPrefix &&  // Не основной
                                !known.additionalBssids.contains(currentBssidPrefix)  // И не дополнительный
                    }

                    when {
                        candidateNetworks.isNotEmpty() -> {
                            Log.d(logTag, "Найдены кандидаты на объединение: ${candidateNetworks.size}")
                            // Показываем диалог: "Это та же сеть?"
                            showMergeNetworkDialog(
                                candidates = candidateNetworks,
                                newBssidPrefix = currentBssidPrefix,
                                newSsid = currentSsid
                            )
                        }

                        else -> {


                            val cellularBssidPrefix = FingerprintGenerator.extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)
                            val cellularNetwork = NetworkManager.findNetworkByBssidPrefix(this, cellularBssidPrefix?: "null")
//
                            if (cellularNetwork != null && cellularNetwork.mainBssidPrefix == currentBssidPrefix) {
                                Log.d(logTag, "НАЙДЕНА СОТОВАЯ СЕТЬ!!!\n" +
                                        "fingerprint: ${cellularNetwork.fingerprint}\n" +
                                        "name: ${cellularNetwork.name}\n" +
                                        "ssid: ${cellularNetwork.ssid}\n" +
                                        "bssid: ${cellularNetwork.mainBssidPrefix}\n" +
                                        "subnet ${cellularNetwork.subnet}")

                                onDecision(cellularNetwork)
                                return@suspendCancellableCoroutine
                            }
                            else {
                                Log.d(logTag, "Сеть полностью новая (SSID или подсеть отличаются)\n" +
                                        "    currentBssidPrefix $currentBssidPrefix\n" +
                                        "    currentSsid $currentSsid\n" +
                                        "    currentSubnetStr $currentSubnetStr\n")
                            }


                            // диалог доверия
                            //showTrustNetworkDialog(currentBssidPrefix, currentSsid, currentSubnetStr)
                             trustNetworkDialog(currentBssidPrefix, currentSsid, currentSubnetStr)


                        }
                    }
                }
            }

            onDecision(null)
            return@suspendCancellableCoroutine



        }









    private fun showMergeNetworkDialog(candidates: List<KnownNetwork>,newBssidPrefix: String,newSsid: String) {
        if (isMergeNetworkDialogShowing) {
            Log.e("MergeNetworkDialog", "Диалог объединения сети уже показывается.")
            return
        }
        isMergeNetworkDialogShowing = true



        // Проверяем, есть ли вообще кандидаты. Если нет выходим.
        if (candidates.isEmpty()) {
            Log.e("showMergeNetworkDialog", "Вызван диалог слияния, но кандидаты пусты. Этого не должно было случиться.")
            //  создать новую сеть ??
            val subnet = networkAnalyzer.getCurrentSubnet()
            trustNetworkDialog(newBssidPrefix, newSsid, subnet)
            return
        }

        // первый кандидат
        val candidate = candidates.first()


        AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialog)
            .setTitle("Обнаружена знакомая сеть")
            .setMessage(
                "Эта точка доступа (MAC: ${newBssidPrefix}) " +
                        "похожа на уже известную сеть \"${candidate.name}\".\n\n" +
                        "Объединить их или создать новую запись?"
            )


            .setPositiveButton("Объединить с \"${candidate.name}\"") { _, _ ->

                NetworkManager.addBssidToNetwork(this, candidate.id, newBssidPrefix)

                NetworkManager.processUserDecision(this, candidate.ssid, candidate.mainBssidPrefix, candidate.subnet, candidate.isTrusted)

                Toast.makeText(
                    this,
                    "Точка доступа добавлена в сеть \"${candidate.name}\"",
                    Toast.LENGTH_SHORT
                ).show()

                // onNetworkTrustedOrMerged()
            }

            // Кнопка для СОЗДАНИЯ НОВОЙ (как setNegativeButton)
            .setNegativeButton("Создать новую сеть") { _, _ ->
                // Просто вызываем стандартный диалог для новой сети
                trustNetworkDialog(candidate.mainBssidPrefix, candidate.ssid, candidate.subnet)
            }
            .setCancelable(false)
            .setOnDismissListener {
                isMergeNetworkDialogShowing = false
                Log.d("MergeNetworkDialog", "Диалог доверия закрыт.")
            }
            .show()
    }







private fun trustNetworkDialog(bssidPrefix: String, ssid: String, subnet: String?){
    isTrustNetworkDialogShowing = true


    showTrustNetworkDialog(
        context = this,
        ssid = ssid,
        bssidPrefix = bssidPrefix,
        subnet = subnet,
        onSave = {ssid, newNetworkName, trust, newSubnet ->
            // Этот блок выполнится ПОСЛЕ нажатия "Сохранить"

            Log.d("ConfigureNetwork", "Сохраняем сеть: Имя='$newNetworkName', Доверие=$trust")

            // Вызываем вашу логику сохранения с новыми данными
            NetworkManager.processUserDecision(this, ssid, bssidPrefix, newSubnet, trust, newNetworkName)

            Toast.makeText(this, "Сеть '$newNetworkName' сохранена.", Toast.LENGTH_LONG).show()

            val networkFingerprint = FingerprintGenerator.generate(ssid, bssidPrefix, newSubnet)
            Log.d("ConfigureNetwork", "Fingerprint: $networkFingerprint")

            val network = NetworkManager.findNetworkByFingerprint(this, networkFingerprint)
            Log.d("ConfigureNetwork", "Сеть '${network?.name}':\n" +
                    "     network ID=${network?.id}\n" +
                    "     network ID=${network?.name}\n" +
                    "     network ID=${network?.fingerprint}")

            applyNetwork(network)
            // Сбрасываем флаг
            isTrustNetworkDialogShowing = false
        },
        onDismiss = {
            isTrustNetworkDialogShowing = false
            Log.d("TrustNetworkDialog", "Диалог доверия закрыт.")
        }
    )

}







    private fun findDeviceInList(deviceToFind: Device): Device? {
        // Сначала ищем по URL
        var foundDevice = allDevices.find { it.url.equals(deviceToFind.url, ignoreCase = true) }
        if (foundDevice != null) return foundDevice

        // Если по URL не нашли, ищем по имени
        foundDevice = allDevices.find { it.name.equals(deviceToFind.name, ignoreCase = true) }
        return foundDevice
    }




    override fun onResume() {
        super.onResume()
        Log.e("MainActivity", "========== onResume ==========")
        webView.onResume()
        //webView.clearHistory()
        isLocationDialogShowing = false


        if(SettingsManager.themeWasChanged) {
            Log.e("MainActivity", "========== onResume themeChanged =========")
            SettingsManager.themeWasChanged = false
            SettingsManager.openPanel = true
            recreate()  // пересоздаём

        }
        ThemeManager.applyTheme(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT


        if (!oneStart){
            Log.e("MainActivity", "=== onResume === loadSettings === updateNoDevicePlaceholder ======")
            loadSettings()
            updateNoDevicePlaceholder()

           // val network = NetworkManager.findNetworkByFingerprint(this, getCurrentNetworkFingerprint() ?: App.ALL_NETWORKS_FINGERPRINT)
           // //setNetwork(getCurrentNetworkFingerprint())

            loadDevices(getCurrentNetworkFingerprint())


        }
        oneStart = false



        if (SettingsManager.deviceForOpen != null){
            openDevice(SettingsManager.deviceForOpen!!)
            SettingsManager.deviceForOpen = null
            closePanel()
        }

        if ((webView.url == null || webView.url == "about:blank") && !isShowingPlaceholder && !SettingsManager.openPanel) {
            Log.d("onResume", "=== onResume === загрузилась пустая страница ")
           // openUrl("about:blank")
            openPanel()
            viewManager.updateLabel(null)
            //openLastOpenedDevice()
        }
        else  if(!SettingsManager.openPanel) closePanel()


        viewManager.lableVisibility()
        viewManager.updateAddressAreaVisability()
        viewManager.updateUrlFieldState(false, false)



        // вернуть состояние кнопки
        val shadowLayout = findViewById<ShadowLayout>(R.id.btn_add_device_cont)
        val strokeWidthDp = 4
        val strokeWidthPx = strokeWidthDp * resources.displayMetrics.density
        shadowLayout.setStrokeWidth(strokeWidthPx)
        shadowLayout.setShadowHidden(false)
        shadowLayoutWelcome.setShadowHidden(false)
        shadowLayoutWelcome.setStrokeWidth(strokeWidthPx)

        if(splitNetwork && splitNetworkManual) {
            val allKnownNetworks = NetworkManager.getKnownNetworks(this@MainActivity)
            val lastNetworkId = SettingsManager.lastNetwork
            val lastKnownNetwork = allKnownNetworks.find { it.id == lastNetworkId }
            viewManager.updateNetworksForSpinner(lastKnownNetwork)
            applyNetwork(lastKnownNetwork)
            if (lastKnownNetwork != null) {
                NetworkManager.getLastDeviceByFingerprint(this, lastKnownNetwork.fingerprint)
                    ?.let { openDevice(it) }
            }
        }


        val currentNetwork = NetworkManager.findNetworkByFingerprint(this, getCurrentNetworkFingerprint() ?: App.ALL_NETWORKS_FINGERPRINT)
        applyNetwork(currentNetwork)



    }// onresume

    override fun onPause() {
        webView.onPause()
        super.onPause()

    }

    override fun onDestroy() {

        super.onDestroy()


        Log.e("MainActivity", "========== onDestroy =========")


    }

    private fun saveLastOpenedDevice(device: Device, fingerprint: String? = null, id: String? = null) {
        if (fingerprint != null) NetworkManager.updateLastDeviceByFingerprint(this, fingerprint, device)
        if (id != null) NetworkManager.updateLastDeviceById(this, id, device)

        NetworkManager.updateLastDeviceByFingerprint(this, currentNetworkFingerprint?:App.DEFAULT_NETWORK_FINGERPRINT, device)
      //  SettingsManager.lastDevice = device

    }
    private fun loadLastOpenedDevice(fingerprint: String? = null, id: String? = null): Device? {

        if(fingerprint != null) return NetworkManager.getLastDeviceByFingerprint(this, fingerprint)
        if (id != null) return NetworkManager.getLastDeviceById(this, id)

        return NetworkManager.getLastDeviceByFingerprint(this, currentNetworkFingerprint?:App.DEFAULT_NETWORK_FINGERPRINT)
        //return SettingsManager.lastDevice
    }

    private fun saveLastOpenedLink(link: String, name: String) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            // Сохраняем имя и урл
            putString("last_device_name", name)
            putString("last_device_url", link)

            // Сбрасываем информацию об иконке, так как это просто ссылка
            putString("last_device_favicon", "")
            putBoolean("last_device_is_builtin", false)

            apply()
        }
    }



    fun playAnimWelcome(view: View) {
        firstStartPlay()
    }

    private fun startHeaderPlay() {
        val currentTheme = ThemeManager.getCurrentTheme(this)

        if (currentTheme != "yellowgreen" && currentTheme != "colorgradient" && currentTheme != "colors" && currentTheme != "black") {
            Log.i("startHeaderPlay", "Тема не Аракес и не Море и не кавай")
            if (!snow_man) return
            if(oneStartSnowAnim) {
                Log.i("startHeaderPlay", "Первый старт снеговика")
                oneStartSnowAnim = false
            }
            else   if (!snow_man_animation && !SettingsManager.isFirstRunSnowMan()) return
        } else {
            Log.i("startHeaderPlay", "Тема Аракес или Море")
        }


        slidingLayout.panelState = PanelState.EXPANDED
        //viewManager.setLabelTheme(light = true)

        Handler(Looper.getMainLooper()).postDelayed({

            val imageNoDeviceWelcome = findViewById<ImageView>(R.id.header_background)
            //  Получаем Drawable
            val drawable = imageNoDeviceWelcome.drawable

            //  Проверяем, что он  анимированный
            if (drawable is AnimatedImageDrawable) {

                //  Если анимация уже идет остановить
                if (drawable.isRunning) drawable.stop()


                drawable.repeatCount = 0
                drawable.start()
                Log.e("MainActivity", "ШАПКА")
            } else {
                Log.e("MainActivity", "Шапка не является анимированной")
            }

        }, 100)


    }

    private fun firstStartPlay() {
        setPlaceholders(noDevicesWelcome = true)


        val imageNoDeviceWelcome = findViewById<ImageView>(R.id.image_nodevices_welcome)
        //  Получаем Drawable
        val drawable = imageNoDeviceWelcome.drawable

        //  Проверяем, что он анимированный
        if (drawable is AnimatedImageDrawable) {

            //  Если анимация уже идет остановить
            if (drawable.isRunning) drawable.stop()


            drawable.repeatCount = 0
            drawable.start()
            Log.e("MainActivity", "Первый!!!")
        } else {
            Log.e("MainActivity", "welcome Drawable не является анимированным")
        }
    }

    private fun noDevicePlay() {
        setPlaceholders(noDevices = true)

        val imageNoDevice = findViewById<ImageView>(R.id.image_nodevice)
        //  Получаем Drawable
        val drawable = imageNoDevice.drawable

        //  Проверяем, что он  анимированный
        if (drawable is AnimatedImageDrawable) {

            //  Если анимация уже идет остановить
            if (drawable.isRunning) drawable.stop()


            drawable.repeatCount = 0
            drawable.start()
            Log.e("noDevicePlay", "Нет устройств!!!")
        } else {
            Log.e("noDevicePlay", "welcome Drawable не является анимированным")
        }
    }


    private fun hideError(){

    }
    private fun closeAllPlaceholders(){
        placeholderDeviceUnavailable.visibility = View.GONE
        placeholderNoDevices.visibility = View.GONE
        placeholderNoDevicesWelcome.visibility = View.GONE

        isShowingPlaceholder = false
    }
    private fun setPlaceholders(noDevicesWelcome: Boolean = false, noDevices: Boolean = false, unavailable: Boolean = false) {
        // Сначала скрываем все
        closeAllPlaceholders()

        when {
            noDevicesWelcome -> {
                placeholderNoDevicesWelcome.visibility = View.VISIBLE
                isShowingPlaceholder = true
                //openUrl("about:blank")

            }
            noDevices -> {
                placeholderNoDevices.visibility = View.VISIBLE
                isShowingPlaceholder = true
                //openUrl("about:blank")
            }
            unavailable -> {
                placeholderDeviceUnavailable.visibility = View.VISIBLE
                isShowingPlaceholder = true

            }
            else -> {
                isShowingPlaceholder = false
            }
        }
    }

    /**
     * открыть последнее открытое устройство
     *
     * если open_last_device == false - открываем пустую страницу
     *
     * если последнего нет - открываем пустую страницу
     */
    private fun openLastOpenedDevice(){
        val lastDevice = loadLastOpenedDevice()
        Log.d("openLastOpenedDevice", "Last Device  ${lastDevice}")

        updateNoDevicePlaceholder()

        if (!open_last_device){
            openUrl("about:blank")
            viewManager.updateLabel(null)
            openPanel()
            return
        }

        if(lastDevice != null) {
            closePanel()
            openDevice(lastDevice)
        }
        else {
            openPanel()
            openUrl("about:blank")
            viewManager.updateLabel(null)
        }

    }


    private fun loadSettings() {

        snow_man = SettingsManager.isSnowmanEnabled
        snow_man_animation = SettingsManager.isSnowmanAnimationEnabled
        easter_egg = SettingsManager.isEasterEggEnabled
        btn_back = SettingsManager.isBackButtonEnabled
        address_input = SettingsManager.isAddressInputEnabled
        check_version = SettingsManager.isCheckNewVersionEnabled

        open_last_device = SettingsManager.isOpenLastDeviceEnabled
        solid_color_icon = SettingsManager.isSolidColorIconEnabled
        label = SettingsManager.isLabelEnabled
        icon_in_label = SettingsManager.isIconInLabelEnabled

        theme = SettingsManager.appTheme

        // Загрузка цвета иконки
        icon_color = SettingsManager.iconColor

        splitNetwork = SettingsManager.isSplitNetworkEnabled
        splitNetworkManual = SettingsManager.isSplitNetworkManualEnabled

        Log.d("loadSettings", "========== ЗАГРУЗКА НАСТРОЕК ==========\n" +
                                        "snow_man: $snow_man\n" +
                                        "snow_man_animation: $snow_man_animation\n" +
                                        "easter_egg: $easter_egg\n" +
                                        "btn_back: $btn_back\n" +
                                        "address_input: $address_input\n" +
                                        "check_version: $check_version\n" +
                                        "open_last_device: $open_last_device\n" +
                                        "solid_color_icon: $solid_color_icon\n" +
                                        "icon_in_label: $icon_in_label\n" +
                                        "icon_color: $icon_color\n" +
                                        "splitNetwork: $splitNetwork\n" +
                                        "splitNetworkManual: $splitNetworkManual\n" +
                                        "======================================="

        )
    }





    /**
     * Возвращает количество устройств, видимых в данный момент, в зависимости от настроек разделения сетей.
     * Использует текущий определенный currentNetworkFingerprint.
     * @return Int - количество устройств.
     */
    private fun getDevicesCount(): Int {
        // на всякий
        if (!::deviceManager.isInitialized) {
            deviceManager = DeviceManager(this)
            Log.e("getDevicesCount", "deviceManager не инициализирован")
        }
        var count: Int
        try {
            if (currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT) {
                count = deviceManager.getTotalDeviceCount()
                Log.d("getDevicesCount", "Подсчет устройств для fingerprint '$currentNetworkFingerprint': $count шт.")

            }
            else {
                count = deviceManager.getDeviceCountForNetwork(currentNetworkFingerprint)
                Log.d(
                    "getDevicesCount",
                    "Подсчет устройств для fingerprint '${currentNetworkFingerprint ?: "сеть не определена, количество для сети по умолчанию"}': $count шт."
                )
            }
        } catch (e: Exception) {
            Log.e("getDevicesCount", "Ошибка при подсчете устройств через DeviceManager", e)
            count = 0
        }

        return count
    }







    /**
     * Логирует информацию обо всех сохраненных устройствах, сгруппировав их по сетям,
     * а также информацию о последнем открытом устройстве.
     * Использует DeviceManager для доступа к данным.
     */
    private fun logSavedDevices() {

        if (!::deviceManager.isInitialized) {
            deviceManager = DeviceManager(this)
        }

        Log.w("MainActivity", "========================================")
        Log.w("MainActivity", "===      СОХРАНЕННЫЕ УСТРОЙСТВА      ===")
        Log.w("MainActivity", "========================================")

        try {
            //    Получаем все сети, для которых есть сохраненные устройства.
            //    Ключ - это объект KnownNetwork, значение - список устройств.
            //    null в качестве ключа означает "Неизвестную сеть".
            val groupedDevices = deviceManager.loadGroupedDevices()

            if (groupedDevices.isEmpty()) {
                Log.w("MainActivity", "--> Устройства не найдены ни в одной сети.")
            } else {
                Log.i("MainActivity", "--> Найдено ${groupedDevices.values.sumOf { it.size }} устройств в ${groupedDevices.size} сетях:")

                // по каждой группе (по каждой сети)
                groupedDevices.forEach { (network, devicesInNetwork) ->
                    val networkName = network?.name ?: "Неизвестная сеть"
                    val networkFingerprint = network?.fingerprint ?: App.DEFAULT_NETWORK_FINGERPRINT
                    val deviceCount = devicesInNetwork.size

                    Log.i("MainActivity", "  ------------------------------------")
                    Log.i("MainActivity", "  СЕТЬ: \"$networkName\" ($deviceCount устр.)")
                    Log.i("MainActivity", "  Fingerprint: $networkFingerprint")

                    // 3. Логируем каждое устройство внутри этой сети
                    devicesInNetwork.forEachIndexed { index, device ->
                        Log.i("MainActivity", "    Устройство ${index + 1}:")
                        Log.i("MainActivity", "      - Имя: ${device.name}")
                        Log.i("MainActivity", "      - URL: ${device.url}")
                        Log.i("MainActivity", "      - networkName: ${device.networkName}")
                        Log.i("MainActivity", "      - networkId: ${device.networkId}")
                        Log.i("MainActivity", "      - Favicon: ${device.faviconPath ?: "не указан"}")
                        Log.i("MainActivity", "      - Встроенная иконка: ${device.isBuiltinIcon ?: "не указан"}")

                    }
                }
            }
            // последнее открытое сеть
            Log.i("MainActivity", "========================================")
            Log.i("MainActivity", "=== ПОСЛЕДНЯЯ ОТКРЫТАЯ СЕТЬ ===")
            val lastNetwork = SettingsManager.lastNetwork
            var network: KnownNetwork? = null

            if (lastNetwork != null) {
                network = NetworkManager.findNetworkById(this, lastNetwork)
                Log.i("MainActivity", "  - Имя: ${network?.name}")
                Log.i("MainActivity", "  - SSID: ${network?.ssid}")
                Log.i("MainActivity", "  - Subnet: ${network?.subnet}")
                Log.i("MainActivity", "  - MAC Prefix: ${network?.mainBssidPrefix}")
                Log.i("MainActivity", "  - Additional Bssids: ${network?.additionalBssids}")


            } else {
                Log.w("MainActivity", "--> Последняя открытая сеть не найдена.")
            }


            // последнее открытое устройство
            Log.i("MainActivity", "========================================")
            Log.i("MainActivity", "=== ПОСЛЕДНЕЕ ОТКРЫТОЕ УСТРОЙСТВО В СЕТИ Имя: ${network?.name ?: "null"} ===")
            val lastDevice = loadLastOpenedDevice(id = lastNetwork)
            if (lastDevice != null) {
                Log.i("MainActivity", "  - Имя: ${lastDevice.name}")
                Log.i("MainActivity", "  - URL: ${lastDevice.url}")
                Log.i("MainActivity", "      - networkName: ${lastDevice.networkName}")
                Log.i("MainActivity", "      - networkId: ${lastDevice.networkId}")
                Log.i("MainActivity", "  - Favicon: ${lastDevice.faviconPath ?: "не указан"}")
                Log.i("MainActivity", "  - Встроенная иконка: ${lastDevice.isBuiltinIcon}")
            } else {
                Log.w("MainActivity", "--> Последнее открытое устройство не найдено.")
            }





            Log.i("MainActivity", "========================================")

        } catch (e: Exception) {
            Log.e("MainActivity", "Критическая ошибка при логировании сохраненных устройств: ${e.message}", e)
        }
    }







    private fun updateAppIcon() {
        val useSnowman = snow_man && easter_egg

        try {
            val packageManager = packageManager
            val packageName = packageName


            //val defaultAlias = ComponentName(packageName, ".MainActivityDefault")
            //val snowAlias = ComponentName(packageName, ".MainActivitySnow")

            val defaultAlias = ComponentName(packageName, "$packageName.MainActivityDefault")
            val snowAlias = ComponentName(packageName, "$packageName.MainActivitySnow")

            if (useSnowman) {
                // Включаем "Снеговика", выключаем "Обычную" иконку
                packageManager.setComponentEnabledSetting(
                    snowAlias,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
                packageManager.setComponentEnabledSetting(
                    defaultAlias,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
                Toast.makeText(this, "Ура! 🎉 \nRed Snow Man активирован!", Toast.LENGTH_LONG).show()
            } else {
                // Включаем "Обычную" иконку, выключаем "Снеговика"
                packageManager.setComponentEnabledSetting(
                    defaultAlias,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
                packageManager.setComponentEnabledSetting(
                    snowAlias,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )

            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка смены иконки", Toast.LENGTH_SHORT).show()
        }
    }











    private val locationSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Пользователь вернулся из настроек.
        Log.i("Permissions", "Возврат из настроек, пересоздание активности.")

        recreate()
    }



    // запрос нескольких разрешений
    private val permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissions ->
        // Проверяем, что все необходимые разрешения выданы
        if (permissions.all { it.value }) {
            Log.i("Permissions", "Все необходимые разрешения получены.")
            // Разрешения получены, запускаем основную логику
            //proceedWithScan()
            // Вызываем сохраненное действие, если оно есть
            onPermissionsGrantedAction?.invoke()
            // Сбрасываем действие, чтобы избежать случайного повторного вызова
            onPermissionsGrantedAction = null
        } else {
            Log.w("Permissions", "Не все разрешения были предоставлены.")
            Toast.makeText(this, "Необходимы все разрешения для определения сети и сканирования.", Toast.LENGTH_LONG).show()
            onPermissionsGrantedAction = null // Также сбрасываем
        }
    }

    /**
     * Проверяем разрешения
     */
    private fun checkPermissions(): Boolean {
        if(!SettingsManager.isSplitNetworkEnabled || SettingsManager.isSplitNetworkManualEnabled) {//
            // не требуется
            return true
        }

        val permissionsToRequest = getRequiredPermissions().filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            // Все разрешения уже есть
            Log.i("checkPermissions", "Все необходимые разрешения получены.")

            return true
        } else {
            // Разрешений нет, показываем диалог с объяснением
            Log.e("checkPermissions", "Не все необходимые разрешения получены. показываем диалог")


           if (isPermissionDialogShowing) {
               Log.e("checkPermissions", "Диалог уже показывается, тикаем")
               return false
           }
           isPermissionDialogShowing = true

            showPermissionsDialog(this,permissionsLauncher,  permissionsToRequest.toTypedArray()) { splitEnabled, manualMode ->
                if(splitEnabled != null)  splitNetwork = splitEnabled
                if(manualMode != null)  splitNetworkManual = manualMode

                Log.d(   "showPermissionsDialog",          "Dialog Permissions - callback: split=$splitEnabled, manual=$manualMode" )
                isPermissionDialogShowing = false



            }
           return false
        }

    }





    // список нужных разрешений в зависимости от версии Android
    private fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
           // permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
        return permissions.toTypedArray()
    }

    /**
     * Проверяем включена ли геолокация в настройках
     * @return true, если включена или если разделение сетей отключено или ручное определение, иначе false
     */
    private fun isLocationEnabled(): Boolean {
        if(!SettingsManager.isSplitNetworkEnabled || SettingsManager.isSplitNetworkManualEnabled) {
            // не требуется вернем типа включено
            return true
        }
        val lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isLocationEnabled

    }











    /**
     * класс для управления всеми элементами.
     * ну почти
     *
     * инициализация, слушатели,
     * анимации, видимость элементов,
     * обновление иконок ,текста и т.д.
     */
    inner class ViewManager {


        private val addressAreaContainer: View = findViewById(R.id.address_area_container)

        private val networkSpinnerContainer: FrameLayout = findViewById(R.id.spinner_network_container)
        private val networkSpinner: Spinner = findViewById(R.id.spinner_network)
        private val deviceCountText: TextView = findViewById(R.id.device_count)
        private val networkIcon: ImageView = findViewById(R.id.split_network_manual)
        private val addButton: com.google.android.material.button.MaterialButton = findViewById(R.id.add_device_button)
        private val goButton: com.google.android.material.button.MaterialButton = findViewById(R.id.refresh_device_button)



        private var deviceCountJob: Job? = null
        private var deviceCountAnimator: AnimatorSet? = null

        private lateinit var networkSpinnerAdapter: ArrayAdapter<String>


        private var fullSpinnerContainerWidth: Int = 0



        private var labelContainer: LinearLayout = findViewById(R.id.floating_titlebar_container)
        private var labelViewContainer: LinearLayout = findViewById(R.id.floating_titlebar)
        private var labelIcon: ImageView = findViewById(R.id.floating_icon)
        private var labelText: TextView = findViewById(R.id.floating_address_field)
        private var labelThemeAnimator: ValueAnimator? = null


        private var addressContainer: LinearLayout = findViewById(R.id.url_autocomplete_input_container)
        private var addressInput: MaterialAutoCompleteTextView = findViewById(R.id.url_autocomplete_input)
        private val connectionIcon: ImageView = findViewById(R.id.connection_type)


        private var addRefreshButtCont: FrameLayout = findViewById(R.id.add_refresh_contqiner)
        private var addDeviceButton: MaterialButton = findViewById(R.id.add_device_button)
        private var refreshDeviceButton : MaterialButton= findViewById(R.id.refresh_device_button)

        private var mainLayout: FrameLayout = findViewById(R.id.main)

        private var webView: WebView = findViewById(R.id.web_view)
        private var swipeRefresh: SwipeRefreshLayout = findViewById(R.id.swipe_refresh)
        private var webProgress: ProgressBar = findViewById(R.id.web_progress)

        private var placeholderDeviceUnavailable: ConstraintLayout = findViewById(R.id.placeholder_device_unavailable)
        private var placeholderNoDevices: ConstraintLayout = findViewById(R.id.placeholder_no_devices)
        private var placeholderNoDevicesWelcome: ConstraintLayout = findViewById(R.id.placeholder_no_devices_welcome)

        private var smartLoadingView: SmartLoadingView = findViewById(R.id.btn_add_device)
        private var shadowLayout: ShadowLayout = findViewById(R.id.btn_add_device_cont)

        private var smartLoadingViewWelcome: SmartLoadingView = findViewById(R.id.btn_add_devices_welcome)
        private var shadowLayoutWelcome: ShadowLayout = findViewById(R.id.btn_add_device_conts_welcome)

        private var smartLoadingViewRefresh: SmartLoadingView = findViewById(R.id.btn_reload)
        private var shadowLayoutRefresh: ShadowLayout = findViewById(R.id.btn_reload_cont)

        private var devicesList: ListView = findViewById(R.id.devices_list)
        private var btnDevices: TextView = findViewById(R.id.btn_devices)
        private var btnSettings: TextView = findViewById(R.id.btn_settings)
        private var slidingLayout: SlidingUpPanelLayout = findViewById(R.id.sliding_layout)

        private var blur: BlurView = findViewById(R.id.blur)

        init {
            // Начальная настройка
            updateUrlFieldState(expanded = false, animate = false)

            initAdapters()
            initLesteners()
            updateAddressAreaVisability()

            val radius = 3f
            val decorView = window.decorView
            val target = findViewById<BlurTarget>(R.id.target)
            val windowBackground = decorView.background

            blur.setupWith(target)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)


            blur.outlineProvider = ViewOutlineProvider.BACKGROUND
            blur.clipToOutline = true

        }
        //===== ИНИЦИАЛИЗАЦИЯ =====//

        // Cлушательная
        private fun initLesteners(){


            networkIcon.setOnClickListener { view ->

                this@MainActivity.currentFocus?.clearFocus()
                hideKeyboard(view)
               // view.showAsDropDown(networkInfoBalloon)
                val textForTooltip = generateSpannedTextFor(TooltipType.NETWORK_STATUS)
                val balloon = createConfiguredBalloon(textForTooltip)
                balloon.showAlignBottom(view)



            }

            connectionIcon.setOnClickListener { view ->

                val textForTooltip = generateSpannedTextFor(TooltipType.CONNECTION_STATUS)
                val balloon = createConfiguredBalloon(textForTooltip)
                balloon.showAlignBottom(view)

            }
            // поле сеть
            networkSpinner.setOnTouchListener { view, event ->
                // Проверяем, что событие - это НАЖАТИЕ (палец опустился)
                if (event.action == MotionEvent.ACTION_DOWN) {
                    // Убираем фокус с текущего активного элемента
                    // Это автоматически вызовет его сворачивание через onFocusChangeListener
                    this@MainActivity.currentFocus?.clearFocus()

                    // Прячем клавиатуру
                    hideKeyboard(view)
                    view.performClick()
                }
                // Возвращаем false, чтобы не мешать стандартной обработке касания
                // (иначе Spinner не откроется)
                false
            }
            networkSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                    if(knownNetworksList.isEmpty()) {
                        // пункт добавить сеть
                        val addItemPosition = parent?.adapter?.count?.minus(1)
                        // пункт никакая сеть
                        val nullItemPosition = parent?.adapter?.count?.minus(2)
                        if (position == addItemPosition) {

                            showAddNetworkDialog(this@MainActivity) { newNetwork ->
                                // успешно создали сеть
                                // Обновляем спиннер и выбираем новую
                               // viewManager.updateNetworksForSpinner(newNetwork, null)
                               // val fingerprint = newNetwork.fingerprint
                               // setNetwork(fingerprint)

                                Log.d("showAddNetworkDialog", "успешно создали сеть ${newNetwork.fingerprint}")
                            }

                            Log.d("onItemSelected", "Открываем диалог добавления сети...")

                            //  возвращаем выбор на предыдущий
                            val lastValidPosition = currentNetworkFingerprint?.let { fp ->
                                knownNetworksList.indexOfFirst { it.fingerprint == fp } + 1
                            } ?: 0
                            networkSpinner.setSelection(lastValidPosition, false)

                            return // тикаем
                        }
                        else if (position == nullItemPosition) {
                            viewManager.updateNetworksForSpinner(null, null)

                            setNetwork(App.ALL_NETWORKS_FINGERPRINT)
                            loadDevices(App.ALL_NETWORKS_FINGERPRINT)
                            Log.d("onItemSelected", "Пусто, тикаем... Выбрали все сети!")
                            return // тикаем
                        }
                    }



                    var selectedFingerprint: String? = null
                    var selectedNetwork: KnownNetwork? = null

                    if (position == 0) {
                        selectedFingerprint = App.ALL_NETWORKS_FINGERPRINT

                    }
                    else {
                        // Позиция уменьшается на 1, так как в knownNetworksList нет пункта "Все"
                        selectedNetwork = knownNetworksList[position - 1]
                        selectedFingerprint = selectedNetwork.fingerprint
                    }
                    Log.d("onItemSelectedListener", "========== Выбрали сеть: ${selectedNetwork?.name} fingerprint: $selectedFingerprint  ==========")


                    if (currentNetworkFingerprint != selectedFingerprint) {
                        Log.d("onItemSelectedListener", "========== Загружаем устройства  ==========")
                        currentNetworkFingerprint = selectedFingerprint
                    }
                    else {
                        Log.d("onItemSelectedListener", "========== Таже самая сеть  ==========")
                        currentNetworkFingerprint = selectedFingerprint
                    }

                    //updateNetworkIcon(selectedNetwork)
                    viewManager.updateNetworksForSpinner(selectedNetwork)
                    applyNetwork(selectedNetwork)
                   // setNetwork(selectedFingerprint)
                    //loadDevices(selectedFingerprint)

                    val deviceCount =
                        if(selectedFingerprint == App.ALL_NETWORKS_FINGERPRINT) deviceManager.getTotalDeviceCount()
                        else                            deviceManager.getDeviceCountForNetwork(selectedFingerprint)

                    viewManager.showDeviceCount(deviceCount)
                    pingAndUpdateDevicesStatus()




                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // поле адреса
            addressInput.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    viewManager.updateUrlFieldState(expanded = true, animate = true)
                }
                else {
                    viewManager.updateUrlFieldState(expanded = false, animate = true)
                    hideKeyboard(view)
                }

            }
            addressInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // при каждом изменении текста
                    updateButtonForAddressField()
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
            addressInput.setOnEditorActionListener { textView, actionId, _ ->
                // Проверяем, что была нажата именно кнопка "Go"
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    val url = textView.text.toString()

                    // Проверяем, что URL не пустой
                    if (url.isNotEmpty()) {
                        openUrl(url)
                        closePanel()
                        hideKeyboard(textView)
                        // Закрываем клавиатуру, чтобы она не мешала
                        //val inputMethodManager =
                        //    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        //inputMethodManager.hideSoftInputFromWindow(textView.windowToken, 0)


                    }

                    // Сообщаем системе, что мы обработали это событие
                    return@setOnEditorActionListener true
                }

                // Если это не "Go", возвращаем false для стандартной обработки
                return@setOnEditorActionListener false
            }

            // Label
            labelContainer.setOnClickListener {
                if (slidingLayout.panelState != PanelState.EXPANDED) {
                    openPanel()
                    ////mainLayout.setBackgroundColor("#225C6E".toColorInt())
                } else {
                    closePanel()
                    //mainLayout.setBackgroundColor(topColor)
                }
            }

            setupLabelDrag()

            // кнопки
            btnDevices.setOnClickListener {
                //slidingLayout.panelState = PanelState.COLLAPSED
                startActivity(Intent(this@MainActivity, DeviceScanActivity::class.java))
            }
            btnSettings.setOnClickListener {
                // slidingLayout.panelState = PanelState.COLLAPSED
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }

            // панель
            slidingLayout.addPanelSlideListener(object : PanelSlideListener {
                override fun onPanelSlide(panel: View, slideOffset: Float) {
                    //Log.i("Main","onPanelSlide, offset $slideOffset")


                }

                override fun onPanelStateChanged(
                    panel: View,
                    previousState: PanelState,
                    newState: PanelState
                ) {

                    // если неразвернута, то спрямляем углы,
                    // и если не на якоре, то сбрасываем фокус с поля адреса
                    if (slidingLayout.panelState != PanelState.EXPANDED) {
                        updateBlueBackgroundAlpha(false) // квадратишпрактишгуд

                        if(slidingLayout.panelState != PanelState.ANCHORED) {

                            val currentFocusView = currentFocus
                            // Проверяем, что фокус именно на поле ввода
                            if (currentFocusView is AutoCompleteTextView) {
                                currentFocusView.clearFocus() // Убираем фокус
                                hideKeyboard(currentFocusView) // Прячем клавиатуру

                                // сворачиваем поле
                                viewManager.updateUrlFieldState(expanded = false, animate = true)
                            }


                        }
                       // viewManager.getTopColorFromWebView()
                    }
                    // если таки развернуто, то углы скругляем
                    // и перестраиваем все в панели
                    else {
                        updateBlueBackgroundAlpha(true) // закругляем
                         updaeteForPanel()


                        //  setLabelTheme(true)

                    }

                    if(slidingLayout.panelState != PanelState.COLLAPSED) {
                        Log.d("onPanelStateChanged", "не COLLAPSED")
                        setLabelTheme(true)
                    }
                    else {
                        Log.d("onPanelStateChanged", "COLLAPSED")
                        setLabelTheme(isLight)
                       // viewManager.getTopColorFromWebView()
                    }

                    // цвет,,,, и количество устройств
                    if (slidingLayout.panelState == PanelState.EXPANDED) {
                        mainLayout.setBackgroundColor("#225C6E".toColorInt())

                        viewManager.showDeviceCount(allDevices.size)

                        //viewManager.updateNetworkIcon()

                    }
                    else if (slidingLayout.panelState == PanelState.COLLAPSED) mainLayout.setBackgroundColor(topColor)
                    else if (slidingLayout.panelState == PanelState.ANCHORED) mainLayout.setBackgroundColor(topColor)
                    // цвет строки
                    //if (slidingLayout.panelState == PanelState.EXPANDED) viewManager.setStatusBarTheme(false)
                    //else viewManager.getTopColorFromWebView()





                    Log.i("Main","onPanelStateChanged $newState")
                }

                override fun onPanelHiddenExecuted(
                    panel: View,
                    interpolator: Interpolator,
                    duration: Int
                ) {
                    Log.i("Main", "onPanelHiddenExecuted")

                }

                override fun onPanelShownExecuted(
                    panel: View,
                    interpolator: Interpolator,
                    duration: Int
                ) {
                    Log.i("Main", "onPanelShownExecuted")

                }

                override fun onPanelExpandedStateY(panel: View, reached: Boolean) {
                    Log.i(
                        "Main",
                        "onPanelExpandedStateY" + (if (reached) "reached" else "left")
                    )
                }

                override fun onPanelCollapsedStateY(panel: View, reached: Boolean) {
                    Log.i(
                        "Main",
                        "onPanelCollapsedStateY" + (if (reached) "reached" else "left")
                    )
                    //val titleBar = findViewById<LinearLayout>(R.id.titlebar)
                    //if (reached) {
                    //    titleBar.setBackgroundColor(Color.WHITE)
                    //} else {
                    //    titleBar.setBackgroundColor("#ffff9431".toColorInt())
                    //}
                }

                override fun onPanelLayout(panel: View, state: PanelState) {
                    //val titleBar = findViewById<LinearLayout>(R.id.titlebar)
                    //if (state == PanelState.COLLAPSED) {
                    //    titleBar.setBackgroundColor(Color.WHITE)
                    //} else if (state == PanelState.EXPANDED || state == PanelState.ANCHORED) {
                    //    titleBar.setBackgroundColor("#ffff9431".toColorInt())
                    //}
                }
            })
            
            
            // список устройств
            devicesList.setOnItemClickListener { _, _, position, _ ->
                val device = allDevices[position]
                openDevice(device)

                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isDestroyed) {
                        closePanel()
                    }
                }, 100) // подожждем



            }


        }



        private fun generateSpannedTextFor(tooltipType: TooltipType): Spanned {

            val imageGetter = Html.ImageGetter { source ->
                val drawableId = when (source) {
                    "wifi_icon"    -> R.drawable.ic_wifi_circle
                    "info_icon" -> R.drawable.ic_info_circle
                    "cross_icon"   -> R.drawable.ic_close_circle
                    "cellular"    -> R.drawable.ic_connection_cellular_inet
                    "cellular_no"    -> R.drawable.ic_connection_cellular_no_inet
                    "hand_icon"    -> R.drawable.ic_connection_hand
                    "http"    -> R.drawable.ic_http
                    "https"    -> R.drawable.ic_https
                    "https_err"    -> R.drawable.ic_https_err
                    "location_off"  -> R.drawable.ic_connection_geo_err

                    else -> 0
                }
                if (drawableId != 0) {
                    val drawable = ContextCompat.getDrawable(this@MainActivity, drawableId)!!
                    val iconSize = (15f * resources.displayMetrics.density).toInt()
                    drawable.setBounds(0, 0, iconSize, iconSize)
                    return@ImageGetter drawable
                }
                return@ImageGetter null
            }

            // Определяем, какой текст нужен
            val htmlString = when (tooltipType) {
                TooltipType.NETWORK_STATUS -> """
                        <b>Статус сети:</b><br>
                        <img src="wifi_icon"/> - wifi, соответствует сети устройства<br>
                        <img src="cellular"/> - сотовая, соответствует сети устройства<br>
                        <img src="cellular_no"/> - сотовая, без интернета<br>
                        <img src="info_icon"/> - сеть не соответствует сети устройства<br>
                        <img src="cross_icon"/> - сеть не определена<br>
                        <img src="location_off"/> - нет доступа к геолокации<br><br>
                        <img src="hand_icon"/> - ручное разделение
                    """.trimIndent()

                TooltipType.CONNECTION_STATUS -> """
                        <b>Статус соединения:</b><br>
                        <img src="http"/> - http соединение<br>
                        <img src="https"/> - https соединение<br>
                        <img src="https_err"/> - ошибка сертификата https!!!
                    """.trimIndent()


            }

            return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY, imageGetter, null)
        }
        private fun createConfiguredBalloon(spannedText: Spanned): Balloon {
            return Balloon.Builder(this@MainActivity)
                .setText(spannedText)
                .setArrowSize(12)

                .setArrowColor(getColorFromAttr(R.attr.buttonStrokeTint))
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setTextGravity(Gravity.START)
                .setWidth(BalloonSizeSpec.WRAP)
                .setHeight(BalloonSizeSpec.WRAP)
                .setTextSize(15f)
                .setCornerRadius(8f)

                .setPadding(12)
                .setBackgroundDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.spinner_dropdown_background))
                .setBalloonAnimation(BalloonAnimation.FADE)
                .setDismissWhenClicked(true)
                .setLifecycleOwner(this@MainActivity)
                .build()
        }
        fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
            val typedValue = TypedValue()
            theme.resolveAttribute(attrColor, typedValue, true)
            return typedValue.data
        }







// В файле MainActivity.kt

        private fun pingAndUpdateDevicesStatus(onComplete: (() -> Unit)? = null) {

            if(!SettingsManager.isPingDevicesEnabled) {
                Log.d("PingDevices_Main", "Проверка доступности отключена")
                return
            }
            Log.d("PingDevices_Main", "Начинаем проверку доступности устройств...")

            // Запускаем корутину в фоновом потоке
            CoroutineScope(Dispatchers.IO).launch {

                // Создаем копию списка
                val devicesToCheck = allDevices.toList()

                devicesToCheck.forEach { device ->
                    try {
                        val host = URL(device.url).host
                        val command = "/system/bin/ping -c 1 -W 1 $host"
                        val process = Runtime.getRuntime().exec(command)
                        val exitCode = process.waitFor()

                        // Обновляем статус в модели данных
                        val newStatus = (exitCode == 0)

                        // Если статус изменился, даем команду UI-потоку обновиться
                        if (device.isActive != newStatus) {
                            device.isActive = newStatus
                            // Переключаемся в основной поток, чтобы обновить адаптер
                         //   withContext(Dispatchers.Main) {        }
                        }

                        if (device.isActive) {
                            Log.i("PingResult_Main", "Устройство '${device.name}' ($host) ДОСТУПНО.")
                        } else {
                            Log.w("PingResult_Main", "Устройство '${device.name}' ($host) НЕ доступно.")
                        }

                    } catch (e: Exception) {
                        // Если статус изменился на неактивный
                        if (device.isActive) {
                            device.isActive = false
                            withContext(Dispatchers.Main) {

                              //  (devicesList.adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()
                            }
                        }
                        Log.e("PingDevices_Main", "Ошибка при пинге '${device.name}': ${e.message}")
                    }
                }

                withContext(Dispatchers.Main) {
                    Log.d("PingDevices_Main", "Проверка ВСЕХ устройств завершена.")
                    (devicesList.adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()
                    onComplete?.invoke()
                }
            }
        }



        // Адаптерная
        private fun initAdapters(){
            //   поле сеть
            networkSpinnerAdapter = ArrayAdapter(this@MainActivity, R.layout.spinner_item_white, mutableListOf<String>())
            networkSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown)
            networkSpinner.adapter = networkSpinnerAdapter

            // список устройств
            val adapterDeviceList = object : ArrayAdapter<Device>(this@MainActivity, 0, allDevices) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view =
                        convertView ?: layoutInflater.inflate(R.layout.item_device, parent, false)
                    val device = getItem(position)!!

                    val icon = view.findViewById<ImageView>(R.id.device_icon)
                    val name = view.findViewById<TextView>(R.id.device_name)
                    val address = view.findViewById<TextView>(R.id.device_address)
                    val activeIcon = view.findViewById<ImageView>(R.id.active)

                    val targetAlpha = if (device.isActive) 1.0f else 0.0f

                    // Если текущая прозрачность не совпадает с целевой, запускаем анимацию.
                    if(targetAlpha == 0.0f) activeIcon.alpha = targetAlpha
                    else if (activeIcon.alpha != targetAlpha) {
                        activeIcon.animate()
                            .alpha(targetAlpha)
                            .setDuration(1000)
                            .start()
                    }


                    // Установка фавикона
                    if (device.faviconPath != null) {
                        if (device.isBuiltinIcon || device.faviconPath.startsWith("ic_")) {
                            val resId =
                                resources.getIdentifier(device.faviconPath, "drawable", packageName)
                            icon.setImageResource(if (resId != 0) resId else R.drawable.ic_device_device)
                        } else {
                            val file = File(device.faviconPath)
                            if (file.exists()) {
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                icon.setImageBitmap(bitmap)
                            } else {
                                icon.setImageResource(R.drawable.ic_device_device)
                            }
                        }
                    } else {
                        icon.setImageResource(R.drawable.ic_device_device)
                    }

                    if (solid_color_icon) {
                        ImageViewCompat.setImageTintList(
                            icon,
                            icon_color?.let { ColorStateList.valueOf(it.toColorInt()) })
                    } else {
                        ImageViewCompat.setImageTintList(icon, null)
                    }


                    name.text = device.name
                    //name.setTextColor(0xFFFFFFFF.toInt())
                    //name.textSize = 16f
                    //name.setTypeface(null, Typeface.BOLD)

                    address.text = device.url
                    address.setTextColor(0xFFAAAAAA.toInt())
                    address.textSize = 13f


                    val deviceNetwork = view.findViewById<TextView>(R.id.device_network)
                    //if(currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT) {
                    //        deviceNetwork.visibility = View.VISIBLE
                    //        deviceNetwork.text = "Все сети"
                    //}
                    //else deviceNetwork.visibility = View.GONE


                    if (currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT) {

                        deviceNetwork.visibility = View.VISIBLE
                        deviceNetwork.text = device.networkName ?: "Неизвестная сеть"
                    } else {
                        if(splitNetwork){
                            deviceNetwork.visibility = View.VISIBLE
                            deviceNetwork.text = device.networkName ?: "Неизвестная сеть"
                        }
                        else {
                            deviceNetwork.visibility = View.GONE
                        }



                    }




                    return view
                }
            }
            devicesList.adapter = adapterDeviceList
        }



        //===== ОБЛАСТЬ СЕТЕЙ И АДРЕСА =====//
        fun updateAddressAreaVisability(visible: Boolean? = null){

            if(visible == null){
                if(address_input) {
                    addressAreaContainer.visibility = View.VISIBLE
                }
                else {
                    splitNetwork = SettingsManager.isSplitNetworkEnabled
                    if(splitNetwork) {
                        addressAreaContainer.visibility = View.VISIBLE
                    }
                    else addressAreaContainer.visibility = View.GONE

                }

            }
            else addressAreaContainer.visibility = if(visible) View.VISIBLE else View.GONE


        }





        //===== СЕТИ =====//

        // обновить сеть в спинере
        fun updateNetworksForSpinner(networkToSelect: KnownNetwork?, networkSSID: String? = null) {
            knownNetworksList.clear()
            knownNetworksList.addAll(NetworkManager.getKnownNetworks(this@MainActivity))

            val networkNamesForSpinner = mutableListOf("Все сети")
            networkNamesForSpinner.addAll(knownNetworksList.map { it.name })
            if (knownNetworksList.size == 0) {
                networkNamesForSpinner.add("-----")
                networkNamesForSpinner.add("Добавить сеть...")
            }

            // Обновляем адаптер
            networkSpinnerAdapter.clear()
            networkSpinnerAdapter.addAll(networkNamesForSpinner)
            networkSpinnerAdapter.notifyDataSetChanged()


            var selectionIndex = 0 // По умолчанию "Все сети"
            if (knownNetworksList.size == 0) selectionIndex = 1

            if (networkToSelect != null) {
                // Ищем индекс сети, которую нам ПЕРЕДАЛИ
                val index = knownNetworksList.indexOfFirst { it.id == networkToSelect.id }
                if (index != -1) {
                    selectionIndex = index + 1 // +1 из-за "Всех сетей"
                }
            }



            networkSpinner.setSelection(selectionIndex, false)


            //currentNetworkFingerprint =
            //    if (selectionIndex == 0)                                      App.ALL_NETWORKS_FINGERPRINT
            //    else if(selectionIndex == 1 && knownNetworksList.size == 0)   App.NULL_NETWORKS_FINGERPRINT
            //    else                 knownNetworksList.getOrNull(selectionIndex - 1)?.fingerprint

            Log.d("onItemSelected", "Обновляем спинер сетей:\n selectionIndex  $selectionIndex\n selectionFingerprint: ${networkToSelect?.fingerprint}\n currentNetworkFingerprint: $currentNetworkFingerprint")

        }


        /**
         * принимает строку с типом сединения
         * @param http ic_http
         * @param https  ic_https
         * @param err все остальное иконка ic_https
         */
        fun updateConnectionIcon(icon: String = "http"){

            if (icon == "http") {
                connectionIcon.setImageResource(R.drawable.ic_http)
            }
            else if (icon == "https"){
                connectionIcon.setImageResource(R.drawable.ic_https)

            }
            else {
                connectionIcon.setImageResource(R.drawable.ic_https_err)
            }

        }





        /**
         * Обновляет иконку типа сети в зависимости от режима.
         * @param selectedNetwork : объект сеть .
         */
        fun updateNetworkIcon(selectedNetwork: KnownNetwork?) {
            val logTag = "updateNetworkIcon"


            val (ssid, bssid, subnet) = networkAnalyzer.getNetworkInfo()


            val bssidPrefix = FingerprintGenerator.getBssidPrefix(bssid)
            val currentNetwork = NetworkManager.findNetworkByBssidPrefix(this@MainActivity, bssidPrefix)

            if(splitNetwork) {
                if(splitNetworkManual) networkIcon.setImageResource(R.drawable.ic_connection_hand)
                else {

                    if (ssid != null && bssid != null && selectedNetwork != null) {


                        val currentNetworkFingerprint = FingerprintGenerator.generate( ssid, bssid, subnet)
                        val bssidCellularPrefix = FingerprintGenerator.extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)


                        if(selectedNetwork == currentNetwork) {
                            Log.d(logTag, "СЕТИ СОВПАДАЮТ")
                            if (selectedNetwork.mainBssidPrefix == bssidCellularPrefix) {
                                Log.d(logTag, "СОТОВАЯ СЕТЬ")
                                if (networkAnalyzer.isDefaultNetworkCellularWithInternet()){
                                    networkIcon.setImageResource(R.drawable.ic_connection_cellular_inet)
                                }
                                else {
                                    networkIcon.setImageResource(R.drawable.ic_connection_cellular_no_inet)
                                }
                            }
                            else {
                                networkIcon.setImageResource(R.drawable.ic_wifi_circle)
                            }

                        }
                        else {
                            // выбрали одну сеть, а подключены к другой.
                            networkIcon.setImageResource(R.drawable.ic_info_circle)
                            Log.e(logTag, "WIFI НЕ СОВПАДАЕТ")
                            Log.w(logTag, "currentNetworkFingerprint: $currentNetworkFingerprint")
                            Log.w(logTag, "selectedNetwork.fingerprint: ${selectedNetwork.fingerprint}")

                        }


                    } else {
                        // Если что-то не определено (например, Wi-Fi выключен),

                        if(!isLocationEnabled())   networkIcon.setImageResource(R.drawable.ic_connection_geo_err)
                        else                       networkIcon.setImageResource(R.drawable.ic_close_circle)


                        Log.e(logTag, "WIFI ERROR:\n" +
                                "      ssid = ${ssid}\n" +
                                "      bssid = ${bssid}\n" +
                                "      selectedNetwork = $selectedNetwork")
                    }



                }
            }


        }












        /**
         * В поле СЕТИ на несколько секунд показывает количество устройств и скрывает.
         * @param count Количество устройств.
         */
        fun showDeviceCount(count: Int?) {


            splitNetwork = NetworkManager.isSplitNetworkEnabled(this@MainActivity )
            splitNetworkManual = NetworkManager.isSplitNetworkManual(this@MainActivity)

            networkIcon.alpha = 0.0f

            if(!splitNetwork) {
                networkSpinner.visibility = View.GONE
                networkSpinnerContainer.visibility = View.GONE
                return
            }
            else {
                networkSpinner.visibility = View.VISIBLE
                networkSpinnerContainer.visibility = View.VISIBLE
            }




            if (count == null) deviceCountText.text = "err"
            else if(count > 99) deviceCountText.text = "∞"
            else deviceCountText.text = count.toString()


            deviceCountText.alpha = 1.0f // Убеждаемся, что он непрозрачный
            deviceCountText.visibility = View.VISIBLE


            deviceCountAnimator?.cancel() // Отменяем анимацию
            deviceCountJob?.cancel()      // Отменяем предыдущую корутину
            deviceCountJob = lifecycleScope.launch {
                // Ждем 3 секунды
                delay(1000)

                // Анимация исчезновения текста
                val fadeOutAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
                    duration = 500 //
                    addUpdateListener {
                        deviceCountText.alpha = it.animatedValue as Float
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            // Прячем View со счетчиком, когда оно стало полностью прозрачным
                            deviceCountText.visibility = View.GONE

                            // --- НАЧАЛО ИЗМЕНЕНИЙ ---
                            // Плавно проявляем иконку сети
                            ObjectAnimator.ofFloat(networkIcon, "alpha", 0f, 1f).apply {
                                duration = 400 // Длительность анимации проявления
                                start()
                            }
                            // --- КОНЕЦ ИЗМЕНЕНИЙ ---
                        }
                    })
                }


                android.animation.AnimatorSet().apply {
                    play(fadeOutAnimator)
                    start()
                }
            }

        }






        //===== АДРЕС =====//

        /**
         * Устанавливает текст в поле ввода URL.
         * @param url URL для отображения.
         */
        fun setUrlText(url: String) {
            addressInput.setText(url)
        }
        /**
         * Управляет состоянием поля ввода URL (свернуто/развернуто/скрыто) с анимацией.
         * @param expanded True - развернуть, false - свернуть.
         * @param animate True - использовать анимацию, false - изменить мгновенно.
         */
        fun updateUrlFieldState(expanded: Boolean, animate: Boolean = true) {

            if (address_input) addressContainer.visibility = View.VISIBLE
            else {
                addressContainer.visibility = View.GONE
                return
            }


            if (isUrlBarExpanded == expanded) return
            isUrlBarExpanded = expanded

            val params = networkSpinnerContainer.layoutParams as LinearLayout.LayoutParams

            // замеряем и сохраняем его текущую ширину.
            if (expanded && params.weight > 0) {
                fullSpinnerContainerWidth = networkSpinnerContainer.width
            }
            // на всякий
            if (fullSpinnerContainerWidth == 0) {
                fullSpinnerContainerWidth = (addressContainer.parent as? View)?.width ?: addressContainer.width
            }

            // в пиксели
            val minWidthPx = (30 * resources.displayMetrics.density).toInt()

            // --- ОПРЕДЕЛЯЕМ НАЧАЛЬНУЮ И КОНЕЧНУЮ ШИРИНУ ---
            val startWidth = networkSpinnerContainer.width
            // Если разворачиваем поле URL (expanded = true) -> конечная ширина минимальная (minWidthPx).
            // Если сворачиваем поле URL (expanded = false) -> конечная ширина та, которую замерили (fullSpinnerContainerWidth).
            val endWidth = if (expanded) minWidthPx else fullSpinnerContainerWidth

            // Анимируем ширину в пикселях
            val widthAnimator = ValueAnimator.ofInt(startWidth, endWidth).apply {
                duration = if (animate) 500L else 0L
                interpolator = DecelerateInterpolator()

                addUpdateListener { animation ->
                    val newWidth = animation.animatedValue as Int
                    params.width = newWidth
                    networkSpinnerContainer.requestLayout()
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        // Убираем вес
                        params.weight = 0f
                        if (expanded) {
                            findViewById<ImageView>(R.id.arrow).visibility = View.GONE
                           // refreshDeviceButton.visibility = View.VISIBLE
                           // addRefreshButtCont.visibility = View.VISIBLE
                        }
                        else {
                            findViewById<ImageView>(R.id.arrow).visibility = View.VISIBLE
                        }
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        if (!expanded) {
                            // Возвращаем вес
                            params.width = 0
                            params.weight = 100f
                            updateButtonForAddressField(refresh = false)
                            //refreshDeviceButton.visibility = View.GONE
                            // addRefreshButtCont.visibility = View.GONE
                        } else {
                            // Фиксируем минимальную ширину.
                            updateButtonForAddressField(true)
                            params.width = minWidthPx
                        }
                        networkSpinnerContainer.requestLayout()
                    }
                })
            }

            widthAnimator.start()
        }
        fun getUrlFronAddressField(): String {
            val url = addressInput.text.toString()
            return url
            return url.ifEmpty { "about:blank" }
        }

        /**
         *
         * если устройство есть покажет + иначе ничего
         */
        fun updateButtonForAddressField(refresh: Boolean? = null){
            val url = addressInput.text.toString()



            val deviceExists = isDeviceExists(url)

            // не разделяем
            if(!splitNetwork && deviceExists) {
                refreshDeviceButton.visibility = View.VISIBLE
                addRefreshButtCont.visibility = View.VISIBLE
                addDeviceButton.visibility = View.GONE
                return
            }
            else if(!splitNetwork) {
                refreshDeviceButton.visibility = View.GONE
                addRefreshButtCont.visibility = View.GONE
                addDeviceButton.visibility = View.VISIBLE
                return
            }

            if(!isUrlBarExpanded) {
                refreshDeviceButton.visibility = View.GONE
                addRefreshButtCont.visibility = View.GONE
                addDeviceButton.visibility = View.GONE
                return
            }



            if (deviceExists) {// если устройство есть
                addRefreshButtCont.visibility = View.VISIBLE

                addDeviceButton.visibility = View.GONE
                refreshDeviceButton.visibility = View.VISIBLE
                return

            } else {// если нет
                addRefreshButtCont.visibility = View.VISIBLE

                addDeviceButton.visibility = View.VISIBLE
                refreshDeviceButton.visibility = View.GONE
            }
        }






        //===== ЯРЛЫК =====//

        /**
         * Устанавливает видимость ярлыка и иконки ярлыка
         * @param: visible - видимость ярлыка, если не указан то в зваисимости от настройки label
         * @param: iconVisible - видимость иконки ярлыка, если не указан то в зависимости от icon_in_label
         */
        fun lableVisibility(visible: Boolean? = null, iconVisible: Boolean? = null){

            val newVisible = visible ?: label

            if(newVisible)   labelContainer.visibility = View.VISIBLE
            else             labelContainer.visibility = View.GONE

            if(iconVisible != null) {
                labelIcon.visibility = if (iconVisible) View.VISIBLE else View.GONE
            }
            else {
                labelIcon.visibility = if (icon_in_label) View.VISIBLE else View.GONE
            }

        }
        /**
         * Обновляем текст ярлыка и иконку
         * @param device Объект устройства
         * @param text Текст ярлыка - если не null проигнорирует device,
         * и установит переданный текст, скрыв иконку (для гитхаба)
         */
        fun updateLabel(device: Device? = null, text: String? = null) {
            val padding = 6
            val padding_start = 20
            if (device != null) {
                Log.d("updateLabel", "Обновление ярлыка, вызвано для устройства:  '${device.name}' из сети: ${device.networkName}")
            }
            else {
                if (text != null){
                    Log.d("updateLabel", "Обновление ярлыка, вызвано с текстом: '$text' ")
                }
                else  Log.e("updateLabel", "Обновление ярлыка, вызвано с пустым устройством ")
            }
            //if(device == null) {
            //    labelText.text = "      "
            //    labelIcon.visibility = View.GONE
            //    return
            //}


            // если указывам что писать то пишем и скрываем иконку
            if (text != null) {
                labelText.text = text
                labelIcon.visibility = View.GONE
                return
            }

            // в остальных случаях определям
            if (getDevicesCount() == 0 && device == null) {
                labelText.text = "       "
                // если показывам иконку
                if (icon_in_label) {
                    labelIcon.visibility = View.VISIBLE

                    labelViewContainer.setPaddingRelative(
                        (4 * resources.displayMetrics.density).toInt(),
                        labelViewContainer.paddingTop,
                        labelViewContainer.paddingEnd,
                        labelViewContainer.paddingBottom
                    )
                    labelIcon.setPaddingRelative(padding_start, padding, padding, padding)

                    labelIcon.setImageResource(R.drawable.ic_device_device)

                }
                // если не показываем иконку
                else {

                    labelIcon.setImageDrawable(null)
                    labelIcon.visibility = View.GONE

                    labelIcon.setPaddingRelative(
                        (4 * resources.displayMetrics.density).toInt(),
                        labelIcon.paddingTop,
                        labelIcon.paddingEnd,
                        labelIcon.paddingBottom
                    )
                    labelViewContainer.setPaddingRelative(
                        (16 * resources.displayMetrics.density).toInt(),
                        labelViewContainer.paddingTop,
                        labelViewContainer.paddingEnd,
                        labelViewContainer.paddingBottom
                    )
                }
                return

            }

            if (device?.url == "about:blank") {
                labelText.text = "              "
                if (icon_in_label) {
                    labelIcon.visibility = View.VISIBLE

                    labelViewContainer.setPaddingRelative(
                        (4 * resources.displayMetrics.density).toInt(),
                        labelViewContainer.paddingTop,
                        labelViewContainer.paddingEnd,
                        labelViewContainer.paddingBottom
                    )
                    labelIcon.setPaddingRelative(padding_start, padding, padding, padding)

                    labelIcon.setImageResource(R.drawable.ic_device_device)

                } else {
                    labelIcon.setImageDrawable(null)
                    labelIcon.visibility = View.GONE

                    labelIcon.setPaddingRelative(
                        (4 * resources.displayMetrics.density).toInt(),
                        labelIcon.paddingTop,
                        labelIcon.paddingEnd,
                        labelIcon.paddingBottom
                    )
                    labelViewContainer.setPaddingRelative(
                        (16 * resources.displayMetrics.density).toInt(),
                        labelViewContainer.paddingTop,
                        labelViewContainer.paddingEnd,
                        labelViewContainer.paddingBottom
                    )
                }
                return
            }


            if (device == null) {
                // Случай, когда нет активного устройства
                labelText.text = "           "
                if (icon_in_label) {
                    labelIcon.visibility = View.VISIBLE

                    labelViewContainer.setPaddingRelative(
                        (4 * resources.displayMetrics.density).toInt(),
                        labelViewContainer.paddingTop,
                        labelViewContainer.paddingEnd,
                        labelViewContainer.paddingBottom
                    )
                    labelIcon.setPaddingRelative(padding_start, padding, padding, padding)

                    labelIcon.setImageResource(R.drawable.ic_device_device)
                } else {
                    labelIcon.setImageDrawable(null)
                    labelIcon.visibility = View.GONE

                    labelIcon.setPaddingRelative(
                        (4 * resources.displayMetrics.density).toInt(),
                        labelIcon.paddingTop,
                        labelIcon.paddingEnd,
                        labelIcon.paddingBottom
                    )
                    labelViewContainer.setPaddingRelative(
                        (16 * resources.displayMetrics.density).toInt(),
                        labelViewContainer.paddingTop,
                        labelViewContainer.paddingEnd,
                        labelViewContainer.paddingBottom
                    )
                }

            }
            else {
                // Устанавливаем имя
                labelText.text = device.name

                // Устанавливаем иконку
                if (icon_in_label) {

                    labelIcon.visibility = View.VISIBLE
                    if (device.faviconPath != null) {
                        if (device.isBuiltinIcon || device.faviconPath.startsWith("ic_")) {
                            val resId =
                                resources.getIdentifier(device.faviconPath, "drawable", packageName)

                            labelIcon.setImageResource(if (resId != 0) resId else R.drawable.ic_device_device)

                            labelViewContainer.setPaddingRelative(
                                (4 * resources.displayMetrics.density).toInt(),
                                labelViewContainer.paddingTop,
                                labelViewContainer.paddingEnd,
                                labelViewContainer.paddingBottom
                            )
                            labelIcon.setPaddingRelative(padding_start, padding, padding, padding)


                        } else {
                            val file = File(device.faviconPath)
                            if (file.exists()) {
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                labelIcon.setImageBitmap(bitmap)
                                labelIcon.setPaddingRelative(
                                    padding_start,
                                    padding,
                                    padding,
                                    padding
                                )
                                labelViewContainer.setPaddingRelative(
                                    (4 * resources.displayMetrics.density).toInt(),
                                    labelViewContainer.paddingTop,
                                    labelViewContainer.paddingEnd,
                                    labelViewContainer.paddingBottom
                                )
                            } else {

                                labelViewContainer.setPaddingRelative(
                                    (4 * resources.displayMetrics.density).toInt(),
                                    labelViewContainer.paddingTop,
                                    labelViewContainer.paddingEnd,
                                    labelViewContainer.paddingBottom
                                )
                                labelIcon.setImageResource(R.drawable.ic_device_device)
                                labelIcon.setPaddingRelative(
                                    padding_start,
                                    padding,
                                    padding,
                                    padding
                                )
                            }
                        }
                    }
                    else {
                        labelIcon.visibility = View.VISIBLE
                        labelViewContainer.setPaddingRelative(
                            (4 * resources.displayMetrics.density).toInt(),
                            labelViewContainer.paddingTop,
                            labelViewContainer.paddingEnd,
                            labelViewContainer.paddingBottom
                        )
                        labelIcon.setPaddingRelative(padding_start, padding, padding, padding)

                        // если передали только имя и ссылку то пробуем найти устройство для отображения правильного фавикона
                        val findDevice = findDeviceInList(device)


                        if (findDevice?.faviconPath != null) {
                            Log.d("updateLabel", "Найдено устройство '${findDevice.name}' с иконкой.")
                            if (findDevice.isBuiltinIcon || findDevice.faviconPath.startsWith("ic_")) {
                                val resId = resources.getIdentifier(findDevice.faviconPath, "drawable", packageName)
                                labelIcon.setImageResource(if (resId != 0) resId else R.drawable.ic_device_device)
                            } else {
                                val file = File(findDevice.faviconPath)
                                if (file.exists()) {
                                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                    labelIcon.setImageBitmap(bitmap)
                                } else {
                                    // Файл иконки не найден, используем стандартную
                                    labelIcon.setImageResource(R.drawable.ic_device_device)
                                }
                            }
                        } else {
                            // Если устройство не найдено или у него нет иконки, ставим стандартную
                            Log.d("updateLabel", "Полное устройство не найдено или без иконки. Используем иконку по умолчанию.")
                            labelIcon.setImageResource(R.drawable.ic_device_device)
                        }

                    }
                } else {
                    // icon_in_label == false
                    labelIcon.setImageDrawable(null)
                    labelIcon.visibility = View.GONE
                    labelIcon.setPaddingRelative(
                        (4 * resources.displayMetrics.density).toInt(),
                        labelIcon.paddingTop,
                        labelIcon.paddingEnd,
                        labelIcon.paddingBottom
                    )
                    labelViewContainer.setPaddingRelative(
                        (16 * resources.displayMetrics.density).toInt(),
                        labelViewContainer.paddingTop,
                        labelViewContainer.paddingEnd,
                        labelViewContainer.paddingBottom
                    )
                }
            }
        }
        @SuppressLint("ClickableViewAccessibility")
        fun setupLabelDrag() {


            val displayMetrics = resources.displayMetrics
            val edgeMarginPx = (edgeMargin * displayMetrics.density).toInt()
            val clickThreshold = 10 // максимальное движение для определения клика в px
            val minBottomMarginPx = (50 * displayMetrics.density).toInt()

            // Переменные для отслеживания двойного клика
            var lastClickTime: Long = 0
            val doubleClickInterval = 200L // интервал для двойного клика в мс
            var isDoubleClick = false

            // Переменные для отслеживания свайпа
            var isHorizontalSwipe = false

            labelContainer.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Запоминаем начальные позиции
                        initialX = view.x
                        initialY = view.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isDragging = false
                        isHorizontalSwipe = false
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (!isDragging && !isHorizontalSwipe) {
                            // Проверяем, это свайп влево или просто перемещение
                            val dx = event.rawX - initialTouchX
                            val dy = event.rawY - initialTouchY

                            // Если движение достаточно большое по горизонтали - начинаем обработку свайпа
                            if (abs(dx) > swipeThreshold && abs(dy) < swipeThreshold) {
                                isHorizontalSwipe = true

                                // Свайп слева направо (dx > 0) - переключаем видимость текста
                                if (dx > 0) {

                                    if (viewManager.labelText.isVisible) {
                                        viewManager.labelText.visibility = View.GONE
                                    } else {
                                        viewManager.labelText.visibility = View.VISIBLE
                                    }


                                    sitchPaddingLabelName()
                                    return@setOnTouchListener true
                                }
                            }

                            // Если движение достаточно большое по горизонтали влево - начинаем драг
                            if (abs(dx) > swipeThreshold && abs(dy) < swipeThreshold && dx < 0) {
                                isDragging = true

                                // Сдвигаем панель от края
                                view.x =
                                    (displayMetrics.widthPixels - view.width - edgeMarginPx).toFloat()

                                if (!viewManager.labelText.isVisible) {
                                    viewManager.labelText.visibility = View.VISIBLE
                                }

                            }
                        }

                        if (isDragging) {
                            // Перемещаем только по вертикали
                            val newY = initialY + (event.rawY - initialTouchY)

                            // Ограничиваем перемещение в пределах экрана
                            //val maxY = displayMetrics.heightPixels - view.height

                            val maxY = displayMetrics.heightPixels - view.height - minBottomMarginPx

                            val clampedY = newY.coerceIn(0f, maxY.toFloat())

                            view.y = clampedY
                        }
                        sitchPaddingLabelName()
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        if (isDragging) {
                            // Сохраняем новую позицию Y
                            saveLabelPosition(view.y)
                            // Возвращаем к правому краю
                            view.x = (displayMetrics.widthPixels - view.width).toFloat()
                        } else if (!isHorizontalSwipe) {
                            // Проверяем, был ли это клик (маленькое перемещение)
                            val dx = abs(event.rawX - initialTouchX)
                            val dy = abs(event.rawY - initialTouchY)

                            if (dx < clickThreshold && dy < clickThreshold) {
                                val currentTime = System.currentTimeMillis()

                                // Проверяем двойной клик
                                if (currentTime - lastClickTime < doubleClickInterval) {
                                    // Двойной клик - отменяем одинарный и открываем на anchorPoint
                                    isDoubleClick = true
                                    val anchorPoint = 1400f / resources.displayMetrics.heightPixels
                                    slidingLayout.anchorPoint = anchorPoint
                                    slidingLayout.panelState =
                                        com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.ANCHORED
                                    //val mainLayout = findViewById<FrameLayout>(R.id.main)
                                    //mainLayout.setBackgroundColor(topColor)
                                    lastClickTime = 0
                                } else {
                                    // Одинарный клик - но откладываем выполнение на случай двойного клика
                                    lastClickTime = currentTime
                                    isDoubleClick = false

                                    // Запускаем отложенную проверку для одинарного клика
                                    view.postDelayed({
                                        if (!isDoubleClick) {
                                            //val mainLayout = findViewById<FrameLayout>(R.id.main)
                                            // Если за время задержки не было двойного клика - выполняем одинарный
                                            if (slidingLayout.panelState !=  PanelState.EXPANDED) {
                                                slidingLayout.panelState = PanelState.EXPANDED
                                                startHeaderPlay()
                                                viewManager.setLabelTheme(light = true)
                                                ////mainLayout.setBackgroundColor("#225C6E".toColorInt())
                                            } else {
                                                slidingLayout.panelState = PanelState.COLLAPSED
                                                //mainLayout.setBackgroundColor(topColor)
                                            }
                                        }
                                    }, doubleClickInterval)
                                }
                            }
                        }
                        isDragging = false
                        isHorizontalSwipe = false
                        true
                    }

                    MotionEvent.ACTION_CANCEL -> {
                        if (isDragging) {
                            // Сохраняем новую позицию Y
                            saveLabelPosition(view.y)
                            // Возвращаем к правому краю
                            view.x = (displayMetrics.widthPixels - view.width).toFloat()
                        }
                        isDragging = false
                        isHorizontalSwipe = false
                        true
                    }

                    else -> false
                }
            }

            // Восстанавливаем сохраненную позицию при создании
            restoreLabelPosition()
            // и проверяем не уехало ли куда
            checkLabelPosition()

        }
        fun sitchPaddingLabelName() {
            var paddingEnd = 0
            var paddingTop = 0
            var paddingBottom = 0

            if (labelText.isVisible) {
                paddingEnd = 0
                paddingTop = 0
                paddingBottom = 0
            } else {
                paddingEnd = (20 * resources.displayMetrics.density).toInt()
                paddingTop = (5 * resources.displayMetrics.density).toInt()
                paddingBottom = (6 * resources.displayMetrics.density).toInt()


            }
            labelViewContainer.setPaddingRelative(
                labelViewContainer.paddingStart,
                paddingTop,
                paddingEnd,
                paddingBottom
            )
        }
        fun saveLabelPosition(yPosition: Float) {
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putFloat("floating_panel_y", yPosition)
                apply()
            }
        }
        fun restoreLabelPosition() {
            
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            val savedY = sharedPref.getFloat("floating_panel_y", -1f)

            if (savedY != -1f) {
                val displayMetrics = resources.displayMetrics
                val maxY = displayMetrics.heightPixels - labelContainer.height
                val clampedY = savedY.coerceIn(0f, maxY.toFloat())

                labelContainer.y = clampedY
            }
        }
        fun checkLabelPosition() {
            
            // Ждём, пока вид полностью измерен
            labelContainer.post {
                val displayMetrics = resources.displayMetrics
                val minBottomMarginPx = (50 * displayMetrics.density).toInt()
                val safeTopPositionPx = (150 * displayMetrics.density) // 150 dp от верха

                val maxAllowedY =
                    displayMetrics.heightPixels - labelContainer.height - minBottomMarginPx

                // Если текущая позиция слишком низко или вообще за экраном
                if (labelContainer.y > maxAllowedY || labelContainer.y + labelContainer.height > displayMetrics.heightPixels) {

                    val newY = safeTopPositionPx.coerceAtMost(maxAllowedY.toFloat()) // на всякий случай

                    labelContainer.y = newY
                    Log.e("MainActivity", "ой, ярлык уехал за экран :(")
                    // Сохраняем исправленную позицию
                    saveLabelPosition(newY)

                    // Прижимаем к правому краю (как и при обычном драге)
                    labelContainer.x = (displayMetrics.widthPixels - labelContainer.width).toFloat()
                }
            }
        }


        fun getTopColorFromWebView() {
            Log.d("getTopColorFromWebView", "Получаем цвет для заголовка")
            if (slidingLayout.panelState == PanelState.EXPANDED) return
            webView.postDelayed({
                // Создаем скриншот верхней части вебвью
                val bitmap = createBitmap(webView.width, 10)
                val canvas = Canvas(bitmap)
                webView.draw(canvas)

                // Получаем цвет центрального пикселя в верхней части
                val centerX = webView.width / 2
                val centerY = 5 // Середина анализируемой области

                val dominantColor = bitmap[centerX, centerY]
                bitmap.recycle()

                applyColorToUI(dominantColor)

            }, 500) // Задержка для полной загрузки страницы
        }

        fun isColorLight(color: Int): Boolean {
            val darkness =
                1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
            return darkness < 0.5
        }
        fun applyColorToUI(color: Int) {
          //  if (slidingLayout.panelState == PanelState.EXPANDED) return
            // Устанавливаем цвет фона
            mainLayout.setBackgroundColor(color)
            // Определяем светлый или темный цвет
            isLight = isColorLight(color)

            // Настраиваем статус бар
            setStatusBarTheme()

            topColor = color
            Log.d("applyColorToUI", "Применен цвет: $color")
            setLabelTheme(isLight)
        }

        fun setStatusBarTheme() {

            var appearance = if (isLight) {
                // Светлый фон - темные иконки
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS

            } else {
                // Темный фон - светлые иконки
                0
            }

            if(slidingLayout.panelState != PanelState.COLLAPSED) {
                val currentTheme = ThemeManager.getCurrentTheme(this@MainActivity)
                if (
                    currentTheme == ThemeManager.THEME_YELLOW ||
                    currentTheme == ThemeManager.THEME_BLACK ||
                    currentTheme == ThemeManager.THEME_PURPLEGRADIENT
                    ){
                    appearance = 0
                }
            }



            window.insetsController?.setSystemBarsAppearance(
                appearance,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )

            // Прозрачный статус бар
            window.statusBarColor = Color.TRANSPARENT
        }
        fun setLabelTheme(light: Boolean) {
            // Отменяем предыдущую анимацию, если она еще идет
            labelThemeAnimator?.cancel()

            //  Определяем начальный и конечный цвета
            val startColor = labelText.currentTextColor
            val endColor = if (light) {
                "#787878".toColorInt()
            } else {
                Color.WHITE
            }
            val colorStr = if(endColor == Color.WHITE) "Белый" else "Темный"

            Log.d("setLabelTheme", "Цвет: $endColor и $startColor, light = $light\n" +
                    "Применен цвет: $colorStr")

            // Если цвет уже нужный, ничего не делаем
            if (startColor == endColor) return

            // убираем тень
            if (light) {
                labelText.setShadowLayer(0F, 0F, 0F, 0)
            } else {
                labelText.setShadowLayer(0F, 0F, 0F, 0) // Ваша логика тени
            }

            //  анимация
            labelThemeAnimator = ValueAnimator.ofArgb(startColor, endColor).apply {
                duration = 600 // Длительность анимации в миллисекундах (можно настроить)

                addUpdateListener { animator ->
                    val animatedColor = animator.animatedValue as Int
                    // Применяем анимированный цвет на каждом кадре
                    labelText.setTextColor(animatedColor)
                    labelIcon.setColorFilter(animatedColor)
                }

                //  интерполятор
                interpolator = DecelerateInterpolator()

                start()
            }
        }
        fun abs(value: Float): Float = if (value < 0) -value else value






        fun updaeteForPanel(){
            if(!splitNetwork) {
                createDefaultNetwork()
                val knownNetwork = NetworkManager.getKnownNetworks(this@MainActivity)
                val defaultNetwork = knownNetwork.find {  it.fingerprint == App.DEFAULT_NETWORK_FINGERPRINT}

                applyNetwork(defaultNetwork)


                if (defaultNetwork != null) {
                    Log.i("onPanelStateChanged", "Разделение отключено, загрузили в спинер сеть по умолчанию: ${defaultNetwork.name}")
                }
                else Log.e("onPanelStateChanged", "Разделение отключено, загрузили в спинер сеть: null")
            }
            else if (splitNetworkManual){
               // val allKnownNetworks = NetworkManager.getKnownNetworks(this@MainActivity)
               // val lastNetworkId = NetworkManager.getLastNetworkId(this@MainActivity)
               // val lastKnownNetwork = allKnownNetworks.find { it.id == lastNetworkId }

               // viewManager.updateNetworksForSpinner(lastKnownNetwork)
               // applyNetwork(lastKnownNetwork)

               // if (lastKnownNetwork != null) {
               //     Log.i("onPanelStateChanged", "Разделение ручное, загрузили в спинер  сеть: ${lastKnownNetwork.name}")
               // }
               // else Log.e("onPanelStateChanged", "Разделение ручное, загрузили в спинер  сеть: null")
            }
            else {

                getNetworkAndContinue { resultingNetwork ->
                    if (resultingNetwork != null) {
                        Log.d("updaeteForPanel", "Сеть успешно определена: ${resultingNetwork.name}")
                        applyNetwork(resultingNetwork)
                    } else {
                        Log.d("updaeteForPanel", "Сеть не была определена или пользователь отменил действие.")
                        val currentNetwork = NetworkManager.findNetworkByFingerprint(this@MainActivity, getCurrentNetworkFingerprint() ?: App.ALL_NETWORKS_FINGERPRINT)
                        applyNetwork(currentNetwork)
                    }


                }


            }

            pingAndUpdateDevicesStatus()

        }


















        //===== ОКНО =====//




        


    
    
    //==============================//
        
    
    
    }


    private enum class TooltipType {
        NETWORK_STATUS,
        CONNECTION_STATUS

    }

}










