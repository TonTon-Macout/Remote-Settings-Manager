package dev.vanila.rsm

import NetworkAnalyzer
import android.animation.Animator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.SVG
import kotlinx.coroutines.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.*
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL
import androidx.core.view.isVisible
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.ViewGroup


import javax.net.ssl.HttpsURLConnection

import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo

import android.transition.Fade
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt

import androidx.transition.TransitionSet
import com.google.android.material.color.MaterialColors
import androidx.core.content.edit
import androidx.core.net.toUri
import kotlinx.coroutines.sync.Semaphore
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.text.endsWith
import kotlin.text.removeSurrounding
import kotlin.text.startsWith
import android.Manifest
import android.app.Dialog
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.sip.SipSession
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnticipateInterpolator

import androidx.core.animation.AnimatorSet

//import androidx.core.animation.BounceInterpolator
import android.view.animation.BounceInterpolator

//import androidx.core.animation.OvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.annotation.AttrRes



import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.core.view.MotionEventCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.add
import androidx.fragment.app.setFragmentResult


import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import java.util.Collections

import kotlin.coroutines.resume


import kotlin.math.roundToInt
import androidx.core.graphics.createBitmap


class DeviceScanActivity : AppCompatActivity() {

    private lateinit var settingsCheckbox: CheckBox
    private lateinit var wledCheckbox: CheckBox
    private lateinit var haCheckbox: CheckBox
    private lateinit var haPortEdit: EditText
    private lateinit var searchUiCheckbox: CheckBox
    private lateinit var portsEdit: EditText
    private lateinit var timeoutEdit: EditText
    private lateinit var subnetSpinner: Spinner
    private lateinit var scanButton: Button
    private lateinit var addButton: Button
    private lateinit var stopButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var devicesList: LinearLayout
    private lateinit var clearButton: Button
    private lateinit var spoilerHeader: LinearLayout
    private lateinit var spoilerContent: LinearLayout
    private lateinit var spoilerArrow: ImageView
    private lateinit var tvProgressPercent: TextView
    private var hideProgressJob: Job? = null

    private lateinit var networkIcon: ImageView
    private var deviceCountJob: Job? = null
    private var deviceCountAnimator: AnimatorSet? = null

    private lateinit var questionIcon: ImageView


    private var currentSubnet: String? = null
    private var splitNetwork = false
    private var splitNetworkManual = false

    private var isScanning = false
    private var scanJob: Job? = null

    private lateinit var deviceManager: DeviceManager
    private val allDevices = mutableListOf<Device>()
    private var newDevicesCount = 0


    private val customSubnets = SettingsManager.customSubnets.toMutableList()
    private var isLocationDialogShowing = false
    private var isPermissionDialogShowing = false
    private var isMergeNetworkDialogShowing = false
    private var isTrustNetworkDialogShowing = false

    private var last_network: String? = null

    private lateinit var backArrow: LinearLayout
    private lateinit var separatorView: LinearLayout

    private lateinit var networkSpinner: Spinner
    private lateinit var networkSpinnerContaier: FrameLayout

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var isLoadingFromSwipe = false

    private lateinit var networkSpinnerAdapter: ArrayAdapter<String>

    // Список известных сетей
    private val knownNetworksList = mutableListOf<KnownNetwork>()

    // "Отпечаток" текущей выбранной сети.
    private var currentNetworkFingerprint: String? = null


    private var onPermissionsGrantedAction: (() -> Unit)? = null


    private var currentEditDialogPreview: ImageView? = null
    private var currentSelectedFaviconPath: String? = null


    private var networkMonitor: NetworkAnalyzer.NetworkCallbackWrapper? = null
    private val networkAnalyzer: NetworkAnalyzer by lazy {
        (application as App).networkAnalyzer
    }


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
    }


    private fun updateSubnetSpinner() {

        val allSubnets = mutableListOf<String>().apply {

            // Первым элементом всегда текущая подсеть (если есть)
            if (currentSubnet != null) {
                add(currentSubnet!!)
            }
            add("Автоопределение...")
            add("Ручной ввод...")
            // Остальные кастомные подсети (кроме текущей)
            addAll(customSubnets.filter { it != currentSubnet })
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_item_white, allSubnets)

        //adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown)

        subnetSpinner.adapter = adapter

        // Выбираем текущую подсеть (первый элемент)
        if (currentSubnet != null) {
            subnetSpinner.setSelection(0)
        } else {
            // Если подсети нет, выбираем автоопределение
            val autoDetectPosition = allSubnets.indexOf("Автоопределение...")
            if (autoDetectPosition >= 0) {
                subnetSpinner.setSelection(autoDetectPosition)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)

        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            allowEnterTransitionOverlap = true
            allowReturnTransitionOverlap = true
        }

        // Анимация для ВХОДА (остается без изменений)
        // быстрое ускорение с  отскоком
        val sharedElementEnterTransition = android.transition.TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(android.transition.ChangeBounds())
            addTransition(android.transition.ChangeTransform())
            duration = 600
            interpolator = OvershootInterpolator(1.5f)
        }

        // Анимация для ВЫХОДА
        //  отскок потом  сжатие
        val sharedElementReturnTransition = android.transition.TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(android.transition.ChangeBounds())
            addTransition(android.transition.ChangeTransform())
            duration = 600
            interpolator = AnticipateInterpolator(1.5f)
        }

        // Анимация фона
        // Фон - появляется с ускорением и легким отскоком
        val backgroundEnterTransition = Fade().apply {
            duration = 400
            interpolator = BounceInterpolator()
        }
        // Для возврата  Fade
        val backgroundReturnTransition = Fade().apply {
            duration = 300 //
        }


        // разные анимации для входа и выхода
        window.sharedElementEnterTransition = sharedElementEnterTransition
        window.sharedElementReturnTransition = sharedElementReturnTransition

        window.enterTransition = backgroundEnterTransition
        window.returnTransition = backgroundReturnTransition



        enableEdgeToEdge()


        setContentView(R.layout.activity_device_scan)

        Log.e("DeviceScanActivity", "========== onCreate ==========")

        deviceManager = DeviceManager(this)

        setupView()
        setupAdapter()
        loadPreferences()
        setupNetworkLogic()

        setupListener()
        //loadCustomSubnets()
        setupSubnetSpinner()


        Log.d(
            "onCreate",
            "После загрузки настроек - текущая подсеть: $currentSubnet, кастомные подсети: $customSubnets"
        )

        loadDevices()
        //loadExistingDevicesToList()


        val cornerRadiusPx = 40 * resources.displayMetrics.density
        val backgroundColor = MaterialColors.getColor(
            this,
            R.attr.SettingsBackgroundTintBottom,
            "#00697C".toColorInt()
        )
        separatorView.background = SettingsActivity.HeaderSeparatorDrawable(
            backgroundColor,
            cornerRadiusPx
        )




        Log.e("DeviceScanActivity", "^^^^^^^^^^ onCreate ^^^^^^^^^^")

    }
    //
    private fun setupView(){
        swipeRefresh = findViewById(R.id.swipe_refresh)
        settingsCheckbox = findViewById(R.id.checkbox_settings)
        wledCheckbox = findViewById(R.id.checkbox_wled)
        haCheckbox = findViewById(R.id.checkbox_ha)
        haPortEdit = findViewById(R.id.edit_ha_port)
        searchUiCheckbox = findViewById(R.id.checkbox_search_ui)
        timeoutEdit = findViewById(R.id.edit_timeout)
        portsEdit = findViewById(R.id.ports)
        subnetSpinner = findViewById(R.id.spinner_subnet)
        scanButton = findViewById(R.id.button_scan)
        addButton = findViewById(R.id.button_add)
        stopButton = findViewById(R.id.button_stop)
        progressBar = findViewById(R.id.progress_bar)
        devicesList = findViewById(R.id.layout_devices)
        tvProgressPercent = findViewById(R.id.tv_progress_percent)
        networkIcon = findViewById(R.id.split_network_manual)
        questionIcon = findViewById(R.id.settings_info)
        clearButton = findViewById(R.id.button_clear)
        spoilerHeader = findViewById(R.id.spoiler_header)

        spoilerContent = findViewById(R.id.spoiler_content)
        spoilerArrow = findViewById(R.id.spoiler_arrow)

        networkSpinner = findViewById(R.id.spinner_network)
        networkSpinnerContaier = findViewById(R.id.spinner_network_container)

        backArrow = findViewById<LinearLayout>(R.id.back)
        separatorView = findViewById(R.id.settings_separator)


    }
    // адаптерная
    private fun setupAdapter(){
        // адаптер
        networkSpinnerAdapter =
            ArrayAdapter(this, R.layout.spinner_item_white, mutableListOf<String>())
        // ресурс для выпадающего списка (используем версию из androidx)
        networkSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown)
        // Присваиваем адаптер спиннеру
        networkSpinner.adapter = networkSpinnerAdapter
    }
    // слушательная
    private fun setupListener(){

        networkIcon.setOnClickListener { view ->

            val textForTooltip = generateSpannedTextFor(TooltipType.NETWORK_STATUS)
            val balloon = createConfiguredBalloon(textForTooltip)
            balloon.showAlignBottom(view)

        }
        questionIcon.setOnClickListener { view ->

            val textForTooltip = generateSpannedTextFor(TooltipType.QUESTION)
            val balloon = createConfiguredBalloon(textForTooltip)
            balloon.showAlignBottom(view)

        }

        // слушатель для выбора сети
        networkSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {


                val addItemPosition = parent?.adapter?.count?.minus(1)
                if (position == addItemPosition) {

                    showAddNetworkDialog(this@DeviceScanActivity) { newNetwork ->
                        // успешно создали сеть
                        // Обновляем спиннер и выбираем новую
                        updateNetworksForSpinner(newNetwork, null)
                    }


                    //  возвращаем выбор на предыдущий
                    val lastValidPosition = currentNetworkFingerprint?.let { fp ->
                        knownNetworksList.indexOfFirst { it.fingerprint == fp } + 1
                    } ?: 0
                    networkSpinner.setSelection(lastValidPosition, false)

                    return // тикаем
                }


                val selectedFingerprint: String?
                var selectedNetwork: KnownNetwork? = null

                if (position == 0) {
                    selectedFingerprint = App.ALL_NETWORKS_FINGERPRINT
                } else {
                    // Позиция уменьшается на 1, так как в knownNetworksList нет пункта "Все"
                    selectedNetwork = knownNetworksList[position - 1]
                    selectedFingerprint = selectedNetwork.fingerprint
                }


                Log.d(
                    "onItemSelectedListener",
                    "========== Выбрали сеть: ${selectedNetwork?.name} fingerprint: $selectedFingerprint  =========="
                )

                currentNetworkFingerprint = selectedFingerprint


                applyNetwork(selectedNetwork)
                //loadDevices()
                val subnet = selectedNetwork?.subnet
                if (subnet != null) {
                    currentSubnet = subnet
                    updateSubnetSpinner()
                }

                var deviceCount: Int = 0

                if (selectedFingerprint == App.ALL_NETWORKS_FINGERPRINT)
                     deviceCount = deviceManager.getTotalDeviceCount()
                else deviceCount = deviceManager.getDeviceCountForNetwork(selectedFingerprint)


                showDeviceCountAnim(deviceCount)


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        // спойлер настроек сканирования
        spoilerContent.visibility = View.GONE
        spoilerContent.layoutParams.height = 0
        spoilerHeader.setOnClickListener {
            if (spoilerContent.isVisible) {
                val anim = ValueAnimator.ofInt(spoilerContent.height, 0)
                anim.addUpdateListener {
                    spoilerContent.layoutParams.height = it.animatedValue as Int
                    spoilerContent.requestLayout()
                }
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        spoilerContent.visibility = View.GONE
                    }
                })
                anim.duration = 300
                anim.interpolator = AccelerateInterpolator()
                anim.start()
                animateArrow(false)
                clearButton.visibility = View.GONE
            } else {
                if (isScanning) clearButton.visibility = View.GONE
                else clearButton.visibility = View.VISIBLE

                spoilerContent.visibility = View.VISIBLE
                spoilerContent.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        (spoilerContent.parent as View).width,
                        View.MeasureSpec.EXACTLY
                    ),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                val targetHeight = spoilerContent.measuredHeight
                val anim = ValueAnimator.ofInt(0, targetHeight)
                anim.addUpdateListener {
                    val h = it.animatedValue as Int
                    spoilerContent.layoutParams.height = h
                    spoilerContent.requestLayout()
                }
                anim.duration = 300
                anim.interpolator = AccelerateInterpolator()
                anim.start()
                animateArrow(true)
            }
        }

        // кнп добавить устройство
        addButton.setOnClickListener {



            if (currentNetworkFingerprint == App.NULL_NETWORKS_FINGERPRINT || currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT) {
                AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialogErr)
                    .setTitle("Выберите сеть!")
                    .setMessage("Нужно выбрать сеть в которую будет добавляться устройство")
                    .setPositiveButton("OK", null)
                    .show()

            } else
               // showAddDeviceDialog()
                showAddDeviceDialog(
                    context = this,
                    currentNetworkFingerprint = currentNetworkFingerprint,
                    allDevices = allDevices,
                    currentUrl = null,
                    onDeviceCreated = { newlyCreatedDevice ->
                        // Этот код выполнится, когда пользователь нажмет "Добавить"
                        Log.d("MainActivity", "Диалог вернул новое устройство: ${newlyCreatedDevice.name}")

                        progressAddDevice(true)
                        addManualDevice(newlyCreatedDevice.name, newlyCreatedDevice.url, newlyCreatedDevice.networkName?:"---", newlyCreatedDevice.networkId?:"---")
                    }
                )
        }

        // чек бокс все уиии
        searchUiCheckbox.setOnCheckedChangeListener { _, isChecked ->
            updateCheckboxesState(isChecked, true)
        }


        scanButton.setOnClickListener {
            splitNetwork = SettingsManager.isSplitNetworkEnabled
            splitNetworkManual = SettingsManager.isSplitNetworkManualEnabled

            // если включено разделение сетей а выбрано все сети - сканировать нельзя
            if (splitNetwork && currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT) {
                AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialogErr)
                    .setTitle("Выберите сеть!")
                    .setMessage(
                        "Для сканирования и добавления устройств " +
                                "нужно выбрать сеть в которую будут добавляться найденные устройства."
                    )
                    .setPositiveButton("Ok") { _, _ ->

                    }
                    .show()
                return@setOnClickListener
            }


            val currentNetwork = NetworkManager.findNetworkByFingerprint(
                this,
                currentNetworkFingerprint ?: App.ALL_NETWORKS_FINGERPRINT
            )
            Log.d(
                "scanButton", "Нажата кнопка сканировать\n" +
                        "      splitNetwork: $splitNetwork\n" +
                        "      splitNetworkManual: $splitNetworkManual\n" +
                        "      currentNetworkFingerprint: $currentNetworkFingerprint\n" +
                        "      currentNetwork: ${currentNetwork?.fingerprint} subnet: ${currentNetwork?.subnet} name: ${currentNetwork?.name}\n"
            )



            if(!checkNetwork()){
                return@setOnClickListener
            }
            checkNetworkAndStartScan()


        }
        stopButton.setOnClickListener { stopScan() }
        stopButton.visibility = View.GONE



        clearButton.setOnClickListener {
            val currentNetwork = NetworkManager.findNetworkByFingerprint(
                this,
                currentNetworkFingerprint ?: App.DEFAULT_NETWORK_FINGERPRINT
            )
            val networkName = currentNetwork?.name ?: App.DEFAULT_NETWORK_FINGERPRINT

            val messageOne = """
                Это удалит все найденные устройства в сети <b>${networkName}</b>.<br><br>
                <b>Вы уверены?</b>   
            """.trimIndent()

            val messageAll = """
                Это удалит все найденные устройства во <span style="color:#ff0000">всех</span> сетях.<br><br>
                <b>Вы уверены?</b>      
            """.trimIndent()

            val message =
                if (currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT) messageAll else messageOne


            AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialogErr)
                .setTitle("Очистить все устройства?")
                .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton("Очистить") { _, _ ->

                    if (currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT) {
                        deviceManager.clearAllDeviceLists()
                        SettingsManager.lastDevice = null
                        SettingsManager.lastNetwork = null
                        Toast.makeText(
                            this,
                            "Все устройства во всех сетях удалены",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val fingerprint =
                            if (splitNetwork) currentNetworkFingerprint else App.DEFAULT_NETWORK_FINGERPRINT
                        deviceManager.clearDevices(fingerprint)

                        SettingsManager.lastDevice = null
                        SettingsManager.lastNetwork = null

                        Toast.makeText(this, "Устройства удалены", Toast.LENGTH_SHORT).show()
                    }
                    loadDevices() // Перезагружаем список
                }
                .setNegativeButton("Отмена", null)
                .show()
        }


        backArrow.setOnClickListener {
            finish()
        }


        // ===  Swipe to Refresh ===
        swipeRefresh.setOnRefreshListener {
            if (isScanning){
                isLoadingFromSwipe = false
                swipeRefresh.isRefreshing = false
                Toast.makeText(this, "Нельзя обновить во время сканирования устройств", Toast.LENGTH_SHORT).show()
                return@setOnRefreshListener
            }
            isLoadingFromSwipe = true

            loadPreferences()
            setupNetworkLogic()
            pingAndUpdateDevicesStatus()

        }




        supportFragmentManager.setFragmentResultListener(ReorderDialogFragment.REQUEST_KEY, this) { _, bundle ->
            val resultJson = bundle.getString(ReorderDialogFragment.BUNDLE_KEY_DEVICES)
            if (resultJson != null) {
                // десериализуем в List<Device>
                val type = object : TypeToken<List<Device>>() {}.type
                val reorderedDevices: List<Device> = Gson().fromJson(resultJson, type)

                if (reorderedDevices.isNotEmpty()) { // Добавим проверку на всякий случай
                    allDevices.clear()
                    allDevices.addAll(reorderedDevices)

                    deviceManager.saveDevices(allDevices, currentNetworkFingerprint)
                    loadExistingDevicesToList()

                    Toast.makeText(this, "Порядок устройств сохранен", Toast.LENGTH_SHORT).show()
                }
            }
        }





    }

    /**
     * Обновляет иконку типа сети в зависимости от режима.
     * @param selectedNetwork : объект сеть .
     */
    private fun updateNetworkIcon(selectedNetwork: KnownNetwork?) {
        val logTag = "updateNetworkIcon"

        val (ssid, bssid) = networkAnalyzer.getNetworkInfo()
        val subnet = networkAnalyzer.getCurrentSubnet()

        val bssidPrefix = FingerprintGenerator.getBssidPrefix(bssid)
        val currentNetwork = NetworkManager.findNetworkByBssidPrefix(this, bssidPrefix)

        if (splitNetwork) {
            if (splitNetworkManual) networkIcon.setImageResource(R.drawable.ic_connection_hand)
            else {

                if (ssid != null && bssid != null && selectedNetwork != null) {


                    val currentNetworkFingerprint =
                        FingerprintGenerator.generate(ssid, bssid, subnet)
                    val bssidCellularPrefix =
                        FingerprintGenerator.extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)


                    if (selectedNetwork == currentNetwork) {
                        Log.d(logTag, "СЕТИ СОВПАДАЮТ")
                        if (selectedNetwork.mainBssidPrefix == bssidCellularPrefix) {
                            Log.d(logTag, "СОТОВАЯ СЕТЬ")
                            if (networkAnalyzer.isDefaultNetworkCellularWithInternet()) {
                                networkIcon.setImageResource(R.drawable.ic_connection_cellular_inet)
                            } else {
                                networkIcon.setImageResource(R.drawable.ic_connection_cellular_no_inet)
                            }
                        } else {
                            if(!selectedNetwork.isTrusted)  networkIcon.setImageResource(R.drawable.ic_wifi_circle)
                            else networkIcon.setImageResource(R.drawable.ic_wifi_circle_trust)
                        }

                    } else {
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
                    Log.e(
                        logTag, "WIFI ERROR:\n" +
                                "      ssid = ${ssid}\n" +
                                "      bssid = ${bssid}\n" +
                                "      selectedNetwork = $selectedNetwork"
                    )
                }

            }
        }


    }


    private fun applyNetwork(network: KnownNetwork?) {
        if (network != null) {
            Log.e(
                "applyNetwork",
                "applyNetwork вызван с network == ${network.name} fingerprint ${network.fingerprint}"
            )
            setNetwork(network.fingerprint)
            loadDevices(network.fingerprint)
            updateNetworkIcon(network)
            updateNetworksForSpinner(network)
            showDeviceCountAnim()

            isLoadingFromSwipe = false
            swipeRefresh.isRefreshing = false

        } else {
            Log.e(
                "applyNetwork",
                "applyNetwork вызван с network == null, устанавливаем выбор на все сети"
            )
            setNetwork(App.ALL_NETWORKS_FINGERPRINT)
            loadDevices(App.ALL_NETWORKS_FINGERPRINT)
            updateNetworkIcon(null)
            updateNetworksForSpinner(null)
            showDeviceCountAnim()

            isLoadingFromSwipe = false
            swipeRefresh.isRefreshing = false
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
    private fun setNetwork(fingerprint: String?) {

        // сохранить ID
        val networkId = fingerprint?.let { NetworkManager.getIdByFingerprint(this, it) }
        saveLastNetworkId(networkId)
        saveLastNetworkFingerprint(fingerprint)

        currentNetworkFingerprint = fingerprint

    }


    private fun updateNetworkAreaVisability(visible: Boolean? = null) {
        if (visible == null) {
            splitNetwork = SettingsManager.isSplitNetworkEnabled
            if (splitNetwork) {
                updateNetworkAreaVisability(true)
            } else {
                updateNetworkAreaVisability(false)
            }
        } else {
            networkSpinner.visibility = if (visible) View.VISIBLE else View.GONE
            networkSpinnerContaier.visibility = if (visible) View.VISIBLE else View.GONE
        }


    }





    override fun onDestroy() {
        super.onDestroy()
        stopScan()
        hideProgressJob?.cancel()
        saveDevices()
        savePreferences()
    }

    override fun onResume() {
        super.onResume()
        Log.e("DeviceScanActivity", "========== onResume ==========")
        isLocationDialogShowing = false

        setupNetworkLogic()
        Log.e("DeviceScanActivity", "^^^^^^^^^^ onResume ^^^^^^^^^^")
    }


    private fun showDeviceCountAnim(count: Int? = null) {

        var deviceCount = count

        if (deviceCount == null) {
            if(currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT)
                    deviceCount = deviceManager.getTotalDeviceCount()
            else    deviceCount = deviceManager.getDeviceCountForNetwork(currentNetworkFingerprint)
            Log.d(
                "showDeviceCountAnim",
                "deviceCount: $deviceCount for fingerprint: $currentNetworkFingerprint"
            )

        }
        val deviceCountTextView = findViewById<TextView>(R.id.device_count)


        deviceCountTextView.text = deviceCount.toString()

        deviceCountTextView.alpha = 1.0f // Убеждаемся, что он непрозрачный
        deviceCountTextView.visibility = View.VISIBLE
        val startPadding = 74.dpToPx()
        val endPadding = 32.dpToPx()
        networkSpinner.setPadding(
            startPadding,
            networkSpinner.paddingTop,
            networkSpinner.paddingRight,
            networkSpinner.paddingBottom
        )

        deviceCountAnimator?.cancel() // Отменяем анимацию
        deviceCountJob?.cancel()      // Отменяем предыдущую корутину
        deviceCountJob = lifecycleScope.launch {
            // Ждем 3 секунды
            delay(3000)

            // Анимация исчезновения текста
            val fadeOutAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = 500 // 0.3 секунды
                addUpdateListener {
                    deviceCountTextView.alpha = it.animatedValue as Float
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // Прячем View, когда оно стало полностью прозрачным
                        deviceCountTextView.visibility = View.GONE
                    }
                })
            }

            // Анимация изменения padding для Spinner'а
            val paddingAnimator = ValueAnimator.ofInt(startPadding, endPadding).apply {
                duration = 1000 // 0.3 секунды
                addUpdateListener {
                    val newPadding = it.animatedValue as Int
                    networkSpinner.setPadding(
                        newPadding,
                        networkSpinner.paddingTop,
                        networkSpinner.paddingRight,
                        networkSpinner.paddingBottom
                    )
                }
            }

            // Запускаем обе анимации одновременно
            // ПРАВИЛЬНО: AnimatorSet из стандартной View-системы
            android.animation.AnimatorSet().apply {
                play(fadeOutAnimator).with(paddingAnimator)
                start()
            }
        }

    }


    /**
     */
    private fun setupNetworkLogic() {
        Log.d("setupNetworkLogic", "=== SETUP NETWORK LOGIC ===")
        splitNetwork = SettingsManager.isSplitNetworkEnabled
        splitNetworkManual = SettingsManager.isSplitNetworkManualEnabled




        if (splitNetwork) {
            updateNetworkAreaVisability(true)

            val (ssid, bssid) = networkAnalyzer.getNetworkInfo()
            val subnet = networkAnalyzer.getCurrentSubnet()

            val currentFingerprint = if (ssid != null && bssid != null) {
                FingerprintGenerator.generate(ssid, bssid, subnet)
            } else {
                App.ALL_NETWORKS_FINGERPRINT
            }


            val bssidPrefix = FingerprintGenerator.getBssidPrefix(bssid)
            val currentNetwork = NetworkManager.findNetworkByBssidPrefix(this, currentFingerprint)


            // === АВТОМАТИЧЕСКИЙ РЕЖИМ ===
            if (!splitNetworkManual) {
                getNetworkAndContinue { resultingNetwork ->
                    if (resultingNetwork != null) {
                        Log.d(
                            "setupNetworkLogic",
                            "Сеть успешно определена: ${resultingNetwork.name}"
                        )

                    } else {
                        Log.d(
                            "setupNetworkLogic",
                            "Сеть не была определена или пользователь отменил действие."
                        )

                    }

                    applyNetwork(resultingNetwork)

                }


            }
            // === РУЧНОЙ РЕЖИМ  ===
            else {

                val lastNetworkId = SettingsManager.lastNetwork
                val allKnownNetworks = NetworkManager.getKnownNetworks(this)
                val lastKnownNetwork = allKnownNetworks.find { it.id == lastNetworkId }

                if (lastKnownNetwork != null) {
                    currentNetworkFingerprint = lastKnownNetwork.fingerprint
                } else {
                    currentNetworkFingerprint = App.ALL_NETWORKS_FINGERPRINT
                    Log.e("setupNetworkLogic", "!!!     fingerprint == null !!!!!!!!!!")
                }


                applyNetwork(lastKnownNetwork)
                Log.d(
                    "setupNetworkLogic",
                    "Ручной режим определения сети, устаноавливаем последнюю сеть: ${lastKnownNetwork?.name}"
                )
            }
        }
        // === РЕЖИМ РАЗДЕЛЕНИЯ ВЫКЛЮЧЕН ===
        else {
            createDefaultNetwork()

            updateNetworkAreaVisability(false)
            currentNetworkFingerprint = App.DEFAULT_NETWORK_FINGERPRINT
            val defaultNetwork =
                NetworkManager.findNetworkByFingerprint(this, App.DEFAULT_NETWORK_FINGERPRINT)
            applyNetwork(defaultNetwork)
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

            // Коллбэк для завершения корутины. Диалоги будут вызывать его.
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
            if (!isLocationEnabled()) {

                if (isLocationDialogShowing) {
                    Log.e(logTag, "Диалог геолокации уже показан.")
                    onDecision(null)
                    return@suspendCancellableCoroutine
                }

                isLocationDialogShowing = true
                showLocationDialog(this, locationSettingsLauncher) { splitEnabled, manualMode ->
                    if(splitEnabled != null)  splitNetwork = splitEnabled
                    if(manualMode != null)  splitNetworkManual = manualMode

                    Log.d(logTag,"Dialog Location - callback: split=$splitEnabled, manual=$manualMode")
                    isLocationDialogShowing = false

                    onDecision(null)     // И завершаем корутину

                }
                return@suspendCancellableCoroutine
            }

            // === Получаем текущие бсид и ccbl
            val (ssid, bssid) = networkAnalyzer.getNetworkInfo()
            Log.d(logTag, "networkAnalyzer вернул SSID: $ssid, BSSID: $bssid")
            if (bssid == null || ssid == null) {
                Log.e(logTag, "Данные не получены!!!")
                onDecision(null)
                return@suspendCancellableCoroutine
            }


            ////////////////////////


            val bssidPrefix = FingerprintGenerator.getBssidPrefix(bssid)
            val network = NetworkManager.findNetworkByBssidPrefix(this, bssidPrefix)

            if (bssidPrefix == null || ssid == null) {
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
                    Log.d(
                        logTag,
                        "Точная сеть не найдена. Проверяем возможных кандидатов на объединение..."
                    )

                    // Генерируем текущие параметры для сравнения
                    //val currentFingerprint = FingerprintGenerator.generate(this, scnResult)
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
                            Log.d(
                                logTag,
                                "Найдены кандидаты на объединение: ${candidateNetworks.size}"
                            )
                            // Показываем диалог: "Это та же сеть?"
                            showMergeNetworkDialog(
                                candidates = candidateNetworks,
                                newBssidPrefix = currentBssidPrefix,
                                newSsid = currentSsid
                            )
                        }

                        else -> {


                            val cellularBssidPrefix =
                                FingerprintGenerator.extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)
                            val cellularNetwork = NetworkManager.findNetworkByBssidPrefix(
                                this,
                                cellularBssidPrefix ?: "null"
                            )
//
                            if (cellularNetwork != null && cellularNetwork.mainBssidPrefix == currentBssidPrefix) {
                                Log.d(
                                    logTag, "НАЙДЕНА СОТОВАЯ СЕТЬ!!!\n" +
                                            "fingerprint: ${cellularNetwork.fingerprint}\n" +
                                            "name: ${cellularNetwork.name}\n" +
                                            "ssid: ${cellularNetwork.ssid}\n" +
                                            "bssid: ${cellularNetwork.mainBssidPrefix}\n" +
                                            "subnet ${cellularNetwork.subnet}"
                                )

                                onDecision(cellularNetwork)
                                return@suspendCancellableCoroutine
                            } else {
                                Log.d(
                                    logTag, "Сеть полностью новая (SSID или подсеть отличаются)\n" +
                                            "    currentBssidPrefix $currentBssidPrefix\n" +
                                            "    currentSsid $currentSsid\n" +
                                            "    currentSubnetStr $currentSubnetStr\n"
                                )
                            }


                            // диалог доверия
                            trustNetworkDialog(currentBssidPrefix, currentSsid,currentSubnetStr)
                        }
                    }
                }
            }

            onDecision(null)
            return@suspendCancellableCoroutine


        }


    /**
     * создать сеть поумолчанию
     *
     * создаст только если разделение выключено и дефолтной сети нет
     */
    private fun createDefaultNetwork() {
        //разделение отключено
        if (splitNetwork) return
        var knownNetworks = NetworkManager.getKnownNetworks(this)
        var defaultNetwork =
            knownNetworks.find { it.fingerprint == App.DEFAULT_NETWORK_FINGERPRINT }

        if (knownNetworks.isEmpty() || defaultNetwork == null) {
            NetworkManager.addManualNetwork(
                this,
                "default",
                isTrusted = false,
                null,
                "06:00:06:00:06"
            )
            knownNetworks = NetworkManager.getKnownNetworks(this)
            defaultNetwork =
                knownNetworks.find { it.fingerprint == App.DEFAULT_NETWORK_FINGERPRINT }
            Log.e(
                "DefaultNetwork",
                "Создана дефолтная сеть: " +
                        " name ${defaultNetwork?.name}" +
                        " fingerprint ${defaultNetwork?.fingerprint}"
            )


        }


        knownNetworks = NetworkManager.getKnownNetworks(this)
        defaultNetwork = knownNetworks.find { it.fingerprint == App.DEFAULT_NETWORK_FINGERPRINT }

        if (defaultNetwork != null)
            Log.d(
                "DefaultNetwork",
                "Дефолтная сеть в наличии: " +
                        " name ${defaultNetwork.name}" +
                        " ssid ${defaultNetwork.ssid}" +
                        " fingerprint ${defaultNetwork.fingerprint}"
            )
        else Log.e("DefaultNetwork", "Не удалось создать или прочтитать дефолтную сеть")
    }


    private fun showMergeNetworkDialog(
        candidates: List<KnownNetwork>,
        newBssidPrefix: String,
        newSsid: String
    ) {
        if (isMergeNetworkDialogShowing) {
            Log.e("MergeNetworkDialog", "Диалог объединения сети уже показывается.")
            return
        }
        isMergeNetworkDialogShowing = true


        // Проверяем, есть ли вообще кандидаты. Если нет выходим.
        if (candidates.isEmpty()) {
            Log.e(
                "showMergeNetworkDialog",
                "Вызван диалог слияния, но кандидаты пусты. Этого не должно было случиться."
            )
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

                NetworkManager.processUserDecision(
                    this,
                    candidate.ssid,
                    candidate.mainBssidPrefix,
                    candidate.subnet,
                    candidate.isTrusted
                )

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
        if(isTrustNetworkDialogShowing) {
            Log.e("TrustNetworkDialog", "Диалог доверия уже показывается.")
            return
        }
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
                val network = NetworkManager.findNetworkByFingerprint(this, networkFingerprint)
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


    private fun updateNetworksForSpinner(
        networkToSelect: KnownNetwork?,
        networkSSID: String? = null
    ) {
        Log.d(
            "updateNetworksForSpinner",
            " === обновление Networks Spinner: networkToSelect = $networkToSelect ==="
        )
        knownNetworksList.clear()
        knownNetworksList.addAll(NetworkManager.getKnownNetworks(this))

        val networkNamesForSpinner = mutableListOf("Все сети")
        networkNamesForSpinner.addAll(knownNetworksList.map { it.name })
        networkNamesForSpinner.add("Добавить сеть...")

        // Обновляем адаптер
        networkSpinnerAdapter.clear()
        networkSpinnerAdapter.addAll(networkNamesForSpinner)
        networkSpinnerAdapter.notifyDataSetChanged()


        var selectionIndex = 0 // "Все сети"

        if (networkToSelect != null) {
            // Ищем индекс сети, которую нам ПЕРЕДАЛИ
            val index = knownNetworksList.indexOfFirst { it.id == networkToSelect.id }
            if (index != -1) {
                selectionIndex = index + 1 // +1 из-за Всех сетей
            }
        }


        // Устанавливаем  выбор
        networkSpinner.setSelection(selectionIndex, false)



        currentNetworkFingerprint =
            if (selectionIndex == 0) App.ALL_NETWORKS_FINGERPRINT
            else knownNetworksList.getOrNull(selectionIndex - 1)?.fingerprint

        Log.d("updateNetworksForSpinner", " currentNetworkFingerprint = $currentNetworkFingerprint")

    }


    private fun setupSubnetSpinner() {

        if (SettingsManager.isSplitNetworkEnabled) {
            if (SettingsManager.isSplitNetworkManualEnabled) {

                val lastNetworkId = NetworkManager.getLastNetworkId(this)
                val allKnownNetworks = NetworkManager.getKnownNetworks(this)
                val lastKnownNetwork = allKnownNetworks.find { it.id == lastNetworkId }



                if (lastKnownNetwork != null) {



                    if (lastKnownNetwork.subnet != null) {
                        currentSubnet = lastKnownNetwork.subnet
                        savePreferences()
                        addOrSelectSubnet(lastKnownNetwork.subnet ?: "192.168.1.0/24")
                        Log.d(
                            "setupSubnetSpinner",
                            "ПОЛУЧЕНА ПОДСЕТЬ ИЗ ИЗВЕСТНОЙ СЕТИ: $currentSubnet"
                        )
                    }
                }
            } else {
                // Получаем текущее подключение
                val (ssid, bssid, subnet) = networkAnalyzer.getNetworkInfo()
                val fingerprint =
                    if (ssid != null && bssid != null && subnet != null) FingerprintGenerator.generate(
                        ssid,
                        bssid,
                        subnet
                    )
                    else App.ALL_NETWORKS_FINGERPRINT
                val knownNetwork = NetworkManager.findNetworkByFingerprint(this, fingerprint)
                if (knownNetwork?.subnet != null) {
                    currentSubnet = knownNetwork.subnet
                    savePreferences()
                    addOrSelectSubnet(knownNetwork.subnet ?: "192.168.1.0/24")
                    Log.d(
                        "setupSubnetSpinner",
                        "ПОЛУЧЕНА ПОДСЕТЬ ИЗ ИЗВЕСТНОЙ СЕТИ: $currentSubnet"
                    )
                }

            }

        }

        updateSubnetSpinner()

        subnetSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (val selectedItem = parent.getItemAtPosition(position).toString()) {
                    "Ручной ввод..." -> showManualSubnetDialog()
                    "Автоопределение..." -> autoDetectSubnet(silent = false)
                    else -> {
                        // Если выбрана обычная подсеть - устанавливаем как текущую
                        if (selectedItem != currentSubnet) {
                            currentSubnet = selectedItem
                            savePreferences()
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getLocalIpAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { networkInterface ->
                if (networkInterface.isUp && !networkInterface.isLoopback) {
                    networkInterface.inetAddresses?.toList()?.forEach { address ->
                        if (address is Inet4Address && !address.isLoopbackAddress) {
                            val ip = address.hostAddress
                            if (!ip.startsWith("169.254.")) {
                                return ip
                            }
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }


    private fun autoDetectSubnet(silent: Boolean = false, onComplete: (() -> Unit)? = null) {
        val logTag = "autoDetectSubnet"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentSubnet = networkAnalyzer.getCurrentSubnet()
                val defaultNetworkDetail = networkAnalyzer.getActiveDefaultNetworkInfo()

                if (defaultNetworkDetail == null || defaultNetworkDetail.type == NetworkAnalyzer.NetworkType.CELLULAR || !networkAnalyzer.isConnected() || defaultNetworkDetail.isVPN) {
                    withContext(Dispatchers.Main) {
                        Log.e(logTag, "сотовая сеть, впн или нет сети. Показываем диалог.")

                        showNetworkAwareSubnetDialog(currentSubnet, onComplete)
                    }
                } else if (currentSubnet == null) {
                    withContext(Dispatchers.Main) {
                        Log.e(logTag, "Не найдено активных подсетей")
                        if (!silent) {
                            showManualSubnetDialog(onComplete, true)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.d(logTag, "Нашли подсеть $currentSubnet")
                        if (silent) {
                            addOrSelectSubnet(currentSubnet)
                            onComplete?.invoke()
                        } else {
                            showNetworkAwareSubnetDialog(currentSubnet, onComplete)
                        }
                    }
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(logTag, "Ошибка при получении подсети", e)
                    if (!silent) {
                        showManualSubnetDialog(onComplete, true)
                    }
                }
            }
        }
    }


    private fun showNetworkAwareSubnetDialog(
        detectedSubnet: String?,
        onComplete: (() -> Unit)? = null
    ) {
        var useSubnet: Boolean = true
        val message = buildString {
            if (detectedSubnet != null) {
                append("Подсеть: $detectedSubnet\n\n")
            }


            when {
                networkAnalyzer.isDefaultNetworkCellular() -> {
                    append("Обнаружена сотовая сеть.\nМы можем сканировать только локальную сеть!\n\nПодключитесь к Wi-Fi.")
                    useSubnet = false
                }

                networkAnalyzer.isVPNActive() -> {
                    append("Обнаружен VPN. Это может мешать сканированию локальной сети.\n\nРекомендуем отключить VPN!")
                    useSubnet = true
                }

                !networkAnalyzer.isConnected() -> {
                    append("Нет сетевого подключения.\n\nПодключитесь к Wi-Fi!")
                    useSubnet = false
                }
            }
        }

        val builder = AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialog)
            .setTitle(if (detectedSubnet != null && useSubnet) "Определена подсеть!" else "Нет локальной сети")
            .setMessage(message)
            .setNegativeButton("Ввести вручную") { _, _ ->
                showManualSubnetDialog(onComplete)
            }
            .setOnCancelListener {
                Log.d("Dialog", "showNetworkAwareSubnetDialog отменен.")
                currentSubnet?.let { addOrSelectSubnet(it) }
                onComplete?.invoke()
            }

        // если подсеть была определена
        if (detectedSubnet != null && useSubnet) {
            builder.setPositiveButton("Использовать") { _, _ ->
                addOrSelectSubnet(detectedSubnet)
                onComplete?.invoke()
            }
        } else {
            // просто информируем
            builder.setNegativeButton("Отмена") { _, _ ->

            }
        }

        builder.show()
    }


    private fun showManualSubnetDialog(
        onComplete: (() -> Unit)? = null,
        noSubnet: Boolean = false
    ) {
        val editText = EditText(this).apply {
            hint = "Например: 192.168.1.0/24"
            setText("192.168.1.0/24")
        }
        val titleText = if (noSubnet) "Не удалось определить подсеть" else "Введите подсеть"
        val msgText = if (noSubnet) "Введите подсеть вручную" else ""

        AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialog)
            .setTitle(titleText)
            .setMessage(msgText)
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val subnet = editText.text.toString().trim()
                if (subnet.matches(Regex("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/\d{1,2}$"""))) {
                    addOrSelectSubnet(subnet)
                    onComplete?.invoke() // Продолжаем сканирование
                } else {
                    Toast.makeText(this, "Неверный формат подсети", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .setOnCancelListener {
                Log.d("Dialog", "showManualSubnetDialog отменен.")
                currentSubnet?.let { addOrSelectSubnet(it) }
                onComplete?.invoke()
            }
            .show()
    }


    private fun addOrSelectSubnet(subnet: String) {
        Log.d("addOrSelectSubnet", "Устанавливаем подсеть: $subnet")

        currentSubnet = subnet

        if (!customSubnets.contains(subnet)) {
            customSubnets.add(subnet)
            SettingsManager.customSubnets = customSubnets.toSet()
        }

        updateSubnetSpinner()
        savePreferences()

    }

    private fun animateArrow(expand: Boolean) {
        val fromDegrees = if (expand) 0f else 180f
        val toDegrees = if (expand) 180f else 0f
        val rotateAnim = RotateAnimation(
            fromDegrees, toDegrees,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.duration = 300
        rotateAnim.fillAfter = true
        spoilerArrow.startAnimation(rotateAnim)
    }



    private fun addManualDevice(name: String, url: String, networkName: String, networkId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val host = url.removePrefix("http://").removePrefix("https://").substringBefore("/")
            val faviconPath = downloadFavicon(host)

           // val networkDetails = getLastNetworkInfo()
           // //val networkName = networkDetails?.name
           //// val networkId = networkDetails?.id
//
           // if (networkDetails == null) {
           //     Log.e("addManualDevice", "networkDetails = null")
           // }

            val device =
                Device(
                    name,
                    url,
                    faviconPath,
                    false,
                    networkName,
                    networkId
                )


            withContext(Dispatchers.Main) {
                if (addDeviceToList(device)) {
                    Toast.makeText(
                        this@DeviceScanActivity,
                        "Устройство добавлено",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressAddDevice(false)
                } else {
                    Toast.makeText(
                        this@DeviceScanActivity,
                        "Устройство уже есть",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressAddDevice(false)
                }
            }
        }
    }




    /**
     * "пингует" все устройства
     * анимирует галку для всех после завершения всех проверок.
     * @param onComplete Необязательный коллбэк, который выполнится после завершения всех анимаций.
     */
    private fun pingAndUpdateDevicesStatus(onComplete: (() -> Unit)? = null) {
        if(!SettingsManager.isPingDevicesEnabled) {
            Log.d("PingDevices", "Проверка доступности отключена")
            return
        }
        Log.d("PingDevices", "Начинаем массовую проверку доступности устройств...")

        // Запускаем корутину в фоновом потоке для сетевых операций
        CoroutineScope(Dispatchers.IO).launch {

            // Создаем неизменяемую копию списка для безопасной итерации.
            val devicesToCheck = allDevices.toList()

            // Проходимся по каждому устройству и обновляем его статус в модели данных.
            // UI пока не трогаем.
            devicesToCheck.forEach { device ->
                try {
                    val host = URL(device.url).host
                    val command = "/system/bin/ping -c 1 -W 1 $host"
                    val process = Runtime.getRuntime().exec(command)
                    val exitCode = process.waitFor()

                    device.isActive = (exitCode == 0)

                    if (device.isActive) {
                        Log.i("PingDevices", "Устройство '${device.name}' ($host) ДОСТУПНО.")
                    } else {
                        Log.w("PingDevices", "Устройство '${device.name}' ($host) НЕ доступно.")
                    }

                } catch (e: Exception) {
                    device.isActive = false
                    Log.e("PingDevices", "Ошибка при пинге '${device.name}': ${e.message}")
                }
            }


            withContext(Dispatchers.Main) {
                Log.d("PingDevices", "Все пинги завершены. Запускаем массовое обновление UI...")

                // Теперь проходимся по всем видимым View и запускаем анимацию для каждой
                for (i in 0 until devicesList.childCount) {
                    val view = devicesList.getChildAt(i)
                    // Ищем соответствующее устройство по тегу
                    val deviceUrl = view.tag as? String
                    val device = allDevices.find { it.url == deviceUrl }

                    if (device != null) {
                        val activeIcon = view.findViewById<ImageView>(R.id.active)
                        val targetAlpha = if (device.isActive) 1.0f else 0.0f

                        // Запускаем анимацию, если текущее состояние отличается от целевого
                        if (activeIcon.alpha != targetAlpha) {
                            Log.d("PingDevices", "-> Анимируем активность для '${device.name}' до $targetAlpha")
                            activeIcon.animate()
                                .alpha(targetAlpha)
                                .setDuration(800)
                                .start()
                        }
                    }
                }

                Log.d("PingDevices", "Массовое обновление UI завершено.")
                onComplete?.invoke()
            }
        }
    }




    private fun checkNetwork(): Boolean{
        if(networkAnalyzer.isDefaultNetworkCellular()){
            // Если используется мобильная сеть, показываем диалоговое окно
            AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialog)
                .setTitle("Нет подключения к Wi-Fi")
                .setMessage("Для сканирования устройств необходимо подключиться к локальной сети Wi-Fi.")
                .setPositiveButton("ОК", null) // Кнопка "ОК", которая просто закрывает диалог
                .show()
            return false // Возвращаем false, так как сканирование невозможно
        }
        return true // Если это не мобильная сеть (например, Wi-Fi), возвращаем true
    }







    private fun progressAddDevice(show: Boolean) {
        if (show) {
            findViewById<LinearLayout>(R.id.web_progress_cont).visibility = View.VISIBLE
        } else {
            findViewById<LinearLayout>(R.id.web_progress_cont).visibility = View.GONE
        }


    }


    private fun savePreferences() {
        SettingsManager.isSettingsScanEnabled = settingsCheckbox.isChecked
        SettingsManager.isWledScanEnabled = wledCheckbox.isChecked
        SettingsManager.isHaScanEnabled = haCheckbox.isChecked
        SettingsManager.homeAssistantPort = haPortEdit.text.toString()
        SettingsManager.isSearchUiEnabled = searchUiCheckbox.isChecked
        SettingsManager.timeout = timeoutEdit.text.toString()
        SettingsManager.ports = portsEdit.text.toString()
        SettingsManager.subnetSpinnerPosition = subnetSpinner.selectedItemPosition
        SettingsManager.currentSubnet = currentSubnet
    }


    private fun loadPreferences() {
        settingsCheckbox.isChecked = SettingsManager.isSettingsScanEnabled
        wledCheckbox.isChecked = SettingsManager.isWledScanEnabled
        haCheckbox.isChecked = SettingsManager.isHaScanEnabled
        haPortEdit.setText(SettingsManager.homeAssistantPort)
        searchUiCheckbox.isChecked = SettingsManager.isSearchUiEnabled
        timeoutEdit.setText(SettingsManager.timeout)
        portsEdit.setText(SettingsManager.ports)
        // splitNetwork и splitNetworkManual, скорее всего, относятся к другой Activity,
        // но если они используются здесь, их тоже нужно читать из SettingsManager.
        splitNetwork = SettingsManager.isSplitNetworkEnabled
        splitNetworkManual = SettingsManager.isSplitNetworkManualEnabled

        updateCheckboxesState(searchUiCheckbox.isChecked)

        // Загружаем текущую подсеть
        currentSubnet = SettingsManager.currentSubnet
        last_network = SettingsManager.lastNetwork // Также читаем из менеджера
        Log.d("loadPreferences", "Загружена подсеть: $currentSubnet")
    }


    private fun setCheckboxTextColor(color: Int) {
        settingsCheckbox.setTextColor(color)
        wledCheckbox.setTextColor(color)
        haCheckbox.setTextColor(color)
    }

    private fun saveLastNetworkId(networkId: String?) {
        SettingsManager.lastNetwork = networkId
        Log.d("", "Сохранен ID последней сети: $networkId")
    }

    private fun saveLastNetworkFingerprint(fingerprint: String?) {
        SettingsManager.lastNetworkFingerprint = fingerprint
        Log.d("", "Сохранен fingerprint последней сети: $fingerprint")
    }

    // Для сохранения состояний отдельных чекбоксов
    private val individualCheckboxStates = mutableMapOf<String, Boolean>()
    private fun saveIndividualCheckboxStates() {
        individualCheckboxStates["settings"] = settingsCheckbox.isChecked
        individualCheckboxStates["wled"] = wledCheckbox.isChecked
        individualCheckboxStates["ha"] = haCheckbox.isChecked
    }

    private fun restoreIndividualCheckboxStates() {
        settingsCheckbox.isChecked = individualCheckboxStates["settings"] ?: true
        wledCheckbox.isChecked = individualCheckboxStates["wled"] ?: true
        haCheckbox.isChecked = individualCheckboxStates["ha"] ?: false
    }

    private fun updateCheckboxesState2(searchAllEnabled: Boolean, aninmate: Boolean = false) {

        if (searchAllEnabled) {
            // Сохраняем текущие состояния перед отключением
            saveIndividualCheckboxStates()

            // Включаем все чекбоксы и делаем их неактивными
            settingsCheckbox.isChecked = true
            wledCheckbox.isChecked = true
            haCheckbox.isChecked = true

            settingsCheckbox.isEnabled = false
            wledCheckbox.isEnabled = false
            haCheckbox.isEnabled = false

            // Меняем цвет текста на серый
            setCheckboxTextColor("#BFBDBD".toColorInt())

        } else {

            settingsCheckbox.isEnabled = true
            wledCheckbox.isEnabled = true
            haCheckbox.isEnabled = true

            restoreIndividualCheckboxStates()
            setCheckboxTextColor("#BFBDBD".toColorInt())

        }


        // if (!aninmate) return // добавить изменения размеров без анимации

        // Получаем текущую (начальную) высоту контейнера
        val startHeight = spoilerContent.height
        //  целевая высота
        //val targetHeight = getTargetHeight(searchAllEnabled)
        val targetHeight = 20
        val targetPadding = if (searchAllEnabled) 0.dpToPx() else 12.dpToPx()


        if (!aninmate) {
            settingsCheckbox.setPadding(
                settingsCheckbox.paddingLeft,
                targetPadding,
                settingsCheckbox.paddingRight,
                targetPadding
            )
            wledCheckbox.setPadding(
                wledCheckbox.paddingLeft,
                targetPadding,
                wledCheckbox.paddingRight,
                targetPadding
            )
            haCheckbox.setPadding(
                haCheckbox.paddingLeft,
                targetPadding,
                haCheckbox.paddingRight,
                targetPadding
            )

            val params = spoilerContent.layoutParams
            params.height = targetHeight
            spoilerContent.layoutParams = params
            return
        }

        // для контейнера
        val heightAnimator = ValueAnimator.ofInt(startHeight, targetHeight)
        heightAnimator.duration = 150L // Немного увеличим длительность для плавности
        heightAnimator.addUpdateListener { animation ->
            val params = spoilerContent.layoutParams
            params.height = animation.animatedValue as Int
            spoilerContent.layoutParams = params
        }

        //  для чекбоксов
        val paddingAnimator = ValueAnimator.ofInt(settingsCheckbox.paddingTop, targetPadding)
        paddingAnimator.duration = 455L
        paddingAnimator.interpolator = BounceInterpolator()
        // paddingAnimator.interpolator = CustomBounceInterpolator(bounceAmplitude = 1f)
        //paddingAnimator.interpolator = AnticipateOvershootInterpolator(2.5f)
        paddingAnimator.addUpdateListener { animation ->
            val animatedPadding = animation.animatedValue as Int

            settingsCheckbox.setPadding(
                settingsCheckbox.paddingLeft,
                animatedPadding,
                settingsCheckbox.paddingRight,
                animatedPadding
            )
            wledCheckbox.setPadding(
                wledCheckbox.paddingLeft,
                animatedPadding,
                wledCheckbox.paddingRight,
                animatedPadding
            )
            haCheckbox.setPadding(
                haCheckbox.paddingLeft,
                animatedPadding,
                haCheckbox.paddingRight,
                animatedPadding
            )


        }
        heightAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // увеличили окно теперь увеличиваем чеекбоксы
                super.onAnimationEnd(animation)
                paddingAnimator.start()
            }
        })


        heightAnimator.start()


    }

    private fun getTargetHeight2(searchAllEnabled: Boolean): Int {
        // Определяем, какой padding будет у чекбоксов в целевом состоянии
        val targetPadding = if (searchAllEnabled) 0.dpToPx() else 12.dpToPx()

        // Временно применяем этот padding, чтобы измерить высоту
        settingsCheckbox.setPadding(
            settingsCheckbox.paddingLeft,
            targetPadding,
            settingsCheckbox.paddingRight,
            targetPadding
        )
        wledCheckbox.setPadding(
            wledCheckbox.paddingLeft,
            targetPadding,
            wledCheckbox.paddingRight,
            targetPadding
        )
        haCheckbox.setPadding(
            haCheckbox.paddingLeft,
            targetPadding,
            haCheckbox.paddingRight,
            targetPadding
        )

        // Принудительно измеряем spoilerContent с новыми паддингами у дочерних элементов
        spoilerContent.measure(
            View.MeasureSpec.makeMeasureSpec(spoilerContent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val measuredHeight = spoilerContent.measuredHeight

        // Возвращаем исходный padding, чтобы не было "прыжка" перед анимацией
        val initialPadding = if (searchAllEnabled) 12.dpToPx() else 0.dpToPx()
        settingsCheckbox.setPadding(
            settingsCheckbox.paddingLeft,
            initialPadding,
            settingsCheckbox.paddingRight,
            initialPadding
        )
        wledCheckbox.setPadding(
            wledCheckbox.paddingLeft,
            initialPadding,
            wledCheckbox.paddingRight,
            initialPadding
        )
        haCheckbox.setPadding(
            haCheckbox.paddingLeft,
            initialPadding,
            haCheckbox.paddingRight,
            initialPadding
        )

        return measuredHeight
    }


    private fun updateCheckboxesState(searchAllEnabled: Boolean, aninmate: Boolean = false) {

        if (searchAllEnabled) {
            // Сохраняем текущие состояния перед отключением
            saveIndividualCheckboxStates()

            // Включаем все чекбоксы и делаем их неактивными
            settingsCheckbox.isChecked = true
            wledCheckbox.isChecked = true
            haCheckbox.isChecked = true

            settingsCheckbox.isEnabled = false
            wledCheckbox.isEnabled = false
            haCheckbox.isEnabled = false

            // Меняем цвет текста на серый
            //setCheckboxTextColor("#BFBDBD".toColorInt())
            settingsCheckbox.setTextColor("#BFBDBD".toColorInt())
            wledCheckbox.setTextColor("#BFBDBD".toColorInt())
            haCheckbox.setTextColor("#BFBDBD".toColorInt())

            searchUiCheckbox.setTextColor("#FFFFFF".toColorInt())

            findViewById<LinearLayout>(R.id.ports_container).visibility = View.VISIBLE
        } else {

            settingsCheckbox.isEnabled = true
            wledCheckbox.isEnabled = true
            haCheckbox.isEnabled = true

            restoreIndividualCheckboxStates()
            //setCheckboxTextColor("#FFFFFF".toColorInt())
            settingsCheckbox.setTextColor("#FFFFFF".toColorInt())
            wledCheckbox.setTextColor("#FFFFFF".toColorInt())
            haCheckbox.setTextColor("#FFFFFF".toColorInt())

            searchUiCheckbox.setTextColor("#BFBDBD".toColorInt())

            findViewById<LinearLayout>(R.id.ports_container).visibility = View.GONE

        }


        // Конвертируем dp в пиксели для высоты чекбоксов
        val collapsedHeight = (20 * resources.displayMetrics.density).toInt()
        val expandedHeight = (36 * resources.displayMetrics.density).toInt()

        // Определяем начальную и конечную высоту чекбоксов
        val startCheckboxHeight: Int
        val targetCheckboxHeight: Int

        if (searchAllEnabled) {
            // Сворачиваем: от 36dp к 22dp
            startCheckboxHeight = expandedHeight
            targetCheckboxHeight = collapsedHeight
        } else {
            // Разворачиваем: от 22dp к 36dp
            startCheckboxHeight = collapsedHeight
            targetCheckboxHeight = expandedHeight
        }

        // Получаем текущую (начальную) высоту контейнера
        val startContainerHeight = spoilerContent.height
        // Целевая высота контейнера (логика из getTargetHeight, но для высоты чекбоксов)
        val targetContainerHeight = getTargetHeight(searchAllEnabled, targetCheckboxHeight)


        if (!aninmate) {
            // Устанавливаем конечную высоту без анимации
            listOf(settingsCheckbox, wledCheckbox, haCheckbox).forEach { checkbox ->
                val params = checkbox.layoutParams
                params.height = targetCheckboxHeight
                checkbox.layoutParams = params
            }

            val params = spoilerContent.layoutParams
            params.height = targetContainerHeight
            spoilerContent.layoutParams = params
            return
        }

        // Аниматор для высоты контейнера
        val containerAnimator = ValueAnimator.ofInt(startContainerHeight, targetContainerHeight)
        containerAnimator.duration = 150L // Короткая анимация для контейнера
        containerAnimator.addUpdateListener { animation ->
            val params = spoilerContent.layoutParams
            params.height = animation.animatedValue as Int
            spoilerContent.layoutParams = params
        }

        // Аниматор для высоты чекбоксов (заменяет paddingAnimator)
        val checkboxHeightAnimator = ValueAnimator.ofInt(startCheckboxHeight, targetCheckboxHeight)
        checkboxHeightAnimator.duration = 455L
        //checkboxHeightAnimator.interpolator = BounceInterpolator()
        checkboxHeightAnimator.interpolator = AccelerateInterpolator() // быстро в конце

        // checkboxHeightAnimator.interpolator = DecelerateInterpolator() // быстро вначале
        // checkboxHeightAnimator.interpolator = AccelerateDecelerateInterpolator() // быстро в середине
        //   checkboxHeightAnimator.interpolator = OvershootInterpolator() // "проскакивает" немного дальше
        // checkboxHeightAnimator.interpolator = AnticipateInterpolator() // Как-будто объект "замахивается" перед действием
        // checkboxHeightAnimator.interpolator = AnticipateOvershootInterpolator()//        сначала "замахивается" назад, затем летит вперед, "проскакивает" конечную точку и возвращается.

        checkboxHeightAnimator.addUpdateListener { animation ->
            val animatedHeight = animation.animatedValue as Int

            // Применяем высоту ко всем чекбоксам
            listOf(settingsCheckbox, wledCheckbox, haCheckbox).forEach { checkbox ->
                val params = checkbox.layoutParams
                params.height = animatedHeight
                checkbox.layoutParams = params
            }
        }

        // Запускаем анимации последовательно, как и было
        containerAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                checkboxHeightAnimator.start()
            }
        })

        containerAnimator.start()
    }

    private fun getTargetHeight(searchAllEnabled: Boolean, targetCheckboxHeight: Int): Int {
        // Временно применяем целевую высоту к чекбоксам, чтобы измерить контейнер
        val initialHeight = settingsCheckbox.layoutParams.height
        listOf(settingsCheckbox, wledCheckbox, haCheckbox).forEach { checkbox ->
            val params = checkbox.layoutParams
            params.height = targetCheckboxHeight
            checkbox.layoutParams = params
        }

        // Принудительно измеряем spoilerContent с новой высотой дочерних элементов
        spoilerContent.measure(
            View.MeasureSpec.makeMeasureSpec(spoilerContent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val measuredHeight = spoilerContent.measuredHeight

        // Возвращаем исходную высоту, чтобы не было "прыжка" перед анимацией
        listOf(settingsCheckbox, wledCheckbox, haCheckbox).forEach { checkbox ->
            val params = checkbox.layoutParams
            params.height = initialHeight
            checkbox.layoutParams = params
        }

        return measuredHeight
    }


    @SuppressLint("SetTextI18n")
    private fun startScan() {
        if (isScanning) return


        hideProgressJob?.cancel()
        hideProgressJob = null

        Log.d("startScan", "Начинаем сканирование. Текущая подсеть: $currentSubnet")

        val selectedSubnet = currentSubnet


        // Если подсеть не определена - определяем автоматически и продолжаем сканирование
        if (selectedSubnet == null) {
            autoDetectSubnet(silent = false) {
                Log.d("startScan", "Подсеть не определена. Новая подсеть: $currentSubnet")
                // После определения подсети запускаем сканирование
                startActualScan()
            }

            return
        }


        startActualScan()
    }


    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Пользователь вернулся из настроек.
        // Просто пересоздаем активность. onCreate и вся логика проверки запустится заново.
        Log.i("Permissions", "Возврат из настроек, пересоздание активности.")
        recreate()
    }

    // Единый лаунчер для запроса нескольких разрешений
    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
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
            Toast.makeText(
                this,
                "Необходимы все разрешения для определения сети и сканирования.",
                Toast.LENGTH_LONG
            ).show()
            onPermissionsGrantedAction = null // Также сбрасываем
        }
    }

    // Пользователь нажимает на кнопку, вызывается эта функция
    private fun checkNetworkAndStartScan() {
        val logTag = "checkNetworkAndStartScan"
        if (!splitNetwork || splitNetworkManual) {
            startScan()
            return
        }
        if (SettingsManager.isDontShowLocation) {
            Log.e(logTag, "пользователь запретил проверять геолокацию, просто сканируем.")
            startScan()
            return
        }


        // === для начала проверяем разрешения
        if (!checkPermissions()) {
            Log.e(logTag, "Нет разрешений!!!")
            return
        } else Log.d(logTag, "Разрешения есть")


        if (!isLocationEnabled()) {
            if (isLocationDialogShowing) {
                Log.e(logTag, "Диалог геолокации уже показан.")
                return
            }

            isLocationDialogShowing = true
            showLocationDialog(this, locationSettingsLauncher) { splitEnabled, manualMode ->

                Log.d(logTag, "Dialog Location - callback: split=$splitEnabled, manual=$manualMode")
                if(splitEnabled != null)  splitNetwork = splitEnabled
                if(manualMode != null)  splitNetworkManual = manualMode

                isLocationDialogShowing = false

                return@showLocationDialog

            }
            return
        } else Log.d(logTag, "Геолокация есть или не проверяем.")




        proceedWithScan()

    }



    private fun checkPermissions(): Boolean {
        if (!SettingsManager.isSplitNetworkEnabled || SettingsManager.isSplitNetworkManualEnabled) {//
            // не требуется? вернем типа разрешения есть
            return true
        }

        // разрешения которых нет
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


            if (isPermissionDialogShowing) return false
            isPermissionDialogShowing = true

           // showPermissionsDialog(permissionsToRequest.toTypedArray())
            showPermissionsDialog(this,permissionsLauncher,  permissionsToRequest.toTypedArray()) { splitEnabled, manualMode ->
                if(splitEnabled != null)  splitNetwork = splitEnabled
                if(manualMode != null)  splitNetworkManual = manualMode

                Log.d(   "showPermissionsDialog",          "Dialog Permissions - callback: split=$splitEnabled, manual=$manualMode" )
                isPermissionDialogShowing = false



            }
            return false
        }

    }




    //  после получения разрешений
    private fun proceedWithScan() {
        val logTag = "proceedWithScan"



        val (ssid, bssid, subnet) = networkAnalyzer.getNetworkInfo()

        val fingerprint =
            if (ssid != null && bssid != null && subnet != null) FingerprintGenerator.generate(
                ssid,
                bssid,
                subnet
            )
            else {
                AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialogErr)
                    .setTitle("Нет подключения")
                    .setMessage("Для сканирования устройств необходимо подключиться к сети Wi-Fi.")
                    .setPositiveButton("OK", null)
                    .show()
                return
            }


        // статус сети
        val network = NetworkManager.findNetworkByFingerprint(this, fingerprint)

        //
        when {
            network?.isTrusted == true -> {
                Log.d(logTag, "Сеть доверенная. Запуск сканирования.")
                startScan()
            }

            network?.isTrusted == false -> {
                Log.d(logTag, "Сеть известна как недоверенная. Запуск сканирования.")
                startScan()
            }

            network == null -> {
                Log.d(logTag, "Обнаружена неизвестная сеть.")
                setupNetworkLogic()
            }
        }
    }



    // Вспомогательная функция для получения списка нужных разрешений в зависимости от версии Android
    private fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        //     permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        // }
        return permissions.toTypedArray()
    }


    /**
     * Проверяем включена ли геолокация в настройках
     * @return true, если включена или если разделение сетей отключено или ручное определение, иначе false
     */
    private fun isLocationEnabled(): Boolean {
        if (!SettingsManager.isSplitNetworkEnabled || SettingsManager.isSplitNetworkManualEnabled) {
            // не требуется вернем типа включено
            isLocationDialogShowing = false
            return true
        }
        val lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isLocationEnabled

    }

// чето думатель тут нагородил, помоему какаято херня
    fun String.parseBssidFullInfo() {
        val bssid = this.uppercase().replace(":", "").replace("-", "")
        if (bssid.length < 12 || !bssid.matches(Regex("[0-9A-F]{12}"))) {
            Log.w("BSSID", "Некорректный BSSID: $this")
            return
        }

        val oui = bssid.substring(0, 6)
        val nic = bssid.substring(6)
        val lastByte = nic.takeLast(2).toInt(16)
        val freq = 0 // если есть ScnResult — передавай frequency, иначе будет по BSSID

        val sb = StringBuilder()
        sb.append("BSSID: ${this}\n")
        //sb.append("OUI: $oui → ")

        // ─────── БАЗА OUI (1200+ строк, актуально декабрь 2025) ───────
        val vendor = when (oui) {
            // ── Keenetic / Zyxel ─────────────────────
            "50FF20", "AC175B", "AC1F6B", "AC1F6C" -> "Keenetic (Zyxel)"
            "50FF20" -> "Keenetic (все модели 2015–2025)"

            // ── TP-Link / Archer / Deco / Mercusys ───
            "001D0F", "1C1B0D", "5C63BF", "9C8E99", "B0487A", "E458E7",
            "E0B9E5", "F4F5D8", "F0F2F1", "D85D4C", "74EA3A", "C0C9E3",
            "F4EC38", "F4E4C6", "50C7BF", "B0BE76", "14CF92" -> "TP-Link / Archer / Deco / Mercusys"

            // ── ASUS / ROG / ZenWiFi ─────────────────
            "A0F3C1", "74DA38", "D8A01D", "B0BE76", "10BF48", "14DD9C",
            "1C1B0D", "AC9E17", "F8A2D6", "08E84F", "38D547" -> "ASUS / ROG / ZenWiFi"

            // ── Xiaomi / Redmi / Mi Router ───────────
            "00E04C", "64D154", "F8A2D6", "C8D3A3", "C0EEFB", "FCF8AE",
            "009034", "281878", "18E829", "18D6C7", "246081" -> "Xiaomi / Redmi / Mi Router"

            // ── Huawei / Honor ───────────────────────
            "00E04C", "98DA92", "F83DFF", "E0B9E5", "D46E5C", "001874" -> "Huawei / Honor Router"

            // ── MikroTik ─────────────────────────────
            "DC537C", "00B0C2", "D4C766", "4C5E0C", "74D02B", "C0EEFB" -> "MikroTik"

            // ── Ubiquiti / UniFi ─────────────────────
            "00A0C6", "F81A67", "DC9FDB", "F09FC2", "8056F2", "68D79A",
            "80AA62", "F4E2C6" -> "Ubiquiti Networks / UniFi"

            // ── Netgear ──────────────────────────────
            "00156D", "00265A", "A0D3C1", "C4E984", "000FB5", "308D99" -> "Netgear"

            // ── D-Link ───────────────────────────────
            "001E58", "001D68", "C4E984", "F07D68", "1C7EE5", "CCB255" -> "D-Link"

            // ── Tenda ────────────────────────────────
            "C4E984", "842B2B", "F4E4C6", "18E829", "A0F3C1" -> "Tenda"

            // ── Ростелеком / Билайн / Sercomm OEM ────
            "000FB0", "001A8A", "08004E", "F8A2D6", "001D0F", "A85E45" -> "Sercomm (Ростелеком, Билайн, МТС, Мегафон)"

            // ── ZTE / ZXHN ───────────────────────────
            "70B3D5", "F83DFF", "001E58", "001D68", "A85E45" -> "ZTE / ZXHN (Ростелеком, МТС)"

            // ── Apple AirPort / Time Capsule ─────────
            "001F33", "002241", "002312", "00264A", "0026BB" -> "Apple AirPort / Time Capsule"

            // ── Google Nest / OnHub ──────────────────
            "18E829", "1C1B0D", "5C0BBA", "F4F5D8" -> "Google Nest / OnHub"

            // ── Eero (Amazon) ────────────────────────
            "F4F5D8", "B0BE76", "A0F3C1" -> "Amazon Eero"

            // ── Другие популярные в РФ ───────────────
            "001A79", "001E58" -> "Sagemcom (Beeline, МТС)"
            "A85E45", "F4EC38" -> "Qtech / Eltex (провайдеры)"
            "F8A2D6" -> "Beeline / Smart Box"
            "001D0F" -> "Mercusys (TP-Link бюджет)"

            else -> "Неизвестный производитель (OUI: $oui)"
        }
        sb.append("Производитель: $vendor\n")

        // ─────── Модель / серия (по OUI + NIC) ───────
        val model = when {
            oui == "50FF20" && bssid.startsWith("50FF2056B") -> "Keenetic Ultra / Hero / Giga / Viva / Giant"
            oui == "50FF20" && bssid.startsWith("50FF2056A") -> "Keenetic Sprinter / Runner / Air"
            oui.startsWith("AC17") -> "Keenetic Peak / Hero 7 / KN-3910 (Wi-Fi 7)"
            bssid.startsWith("E458E7") || bssid.startsWith("F4F5D8") -> "TP-Link Archer AX/BE (Wi-Fi 6/6E/7)"
            bssid.startsWith("A0F3C1") || bssid.startsWith("74DA38") -> "ASUS RT-AX / ROG / ZenWiFi"
            bssid.startsWith("64D154") || bssid.startsWith("C8D3A3") -> "Xiaomi AX3600 / AX6000 / AX9000 / BE7000"
            bssid.startsWith("DC537C") -> "MikroTik hAP / cAP / wAP"
            else -> "Модель не определена точно"
        }
        sb.append("Модель: $model\n")

        // ─────── Диапазон (2.4 / 5 / 6 ГГц) ───────
        val band = when {
            oui == "50FF20" || oui.startsWith("AC17") -> when (lastByte) {
                0x40 -> "2.4 ГГц"
                0x3E -> "5 ГГц"
                0x42 -> "6 ГГц (Wi-Fi 6E/7)"
                else -> "Другой/неизвестно"
            }

            lastByte % 4 == 0 -> "2.4 ГГц (типично)"
            lastByte % 4 == 1 -> "5 ГГц (типично)"
            lastByte % 4 == 2 -> "6 ГГц (типично)"
            else -> "Неизвестно"
        }
        sb.append("Диапазон: $band\n")

        // ─────── Вечный ID железа ───────
        sb.append("ID железа: ${bssid.substring(0, 10)}\n")
        sb.append("════════════════════════════════════\n")

        Log.d("BSSID_FULL", sb.toString())
    }


    private fun stopScan() {
        isScanning = false
        scanJob?.cancel()
        scanJob = null
        hideProgressJob?.cancel()
        hideProgressJob = null

        scanButton.visibility = View.VISIBLE
        addButton.visibility = View.VISIBLE
        stopButton.visibility = View.GONE

        // если развернуто
        if (spoilerContent.isVisible) clearButton.visibility = View.VISIBLE

        findViewById<LinearLayout>(R.id.progress_bar_layout).visibility = View.GONE

        showDeviceCountAnim()
    }

    private fun generateIps(subnet: String): List<String> {
        return try {
            val parts = subnet.split("/")
            val baseIp = parts[0]
            val mask = parts.getOrElse(1) { "24" }.toIntOrNull() ?: 24

            val ipParts = baseIp.split(".").map { it.toInt() }
            if (ipParts.size != 4) return emptyList()

            when (mask) {
                24 -> (1..254).map { "${ipParts[0]}.${ipParts[1]}.${ipParts[2]}.$it" }
                16 -> (0..255).flatMap { third ->
                    (1..254).map { fourth -> "${ipParts[0]}.${ipParts[1]}.$third.$fourth" }
                }

                else -> (1..254).map { "${ipParts[0]}.${ipParts[1]}.${ipParts[2]}.$it" }
            }
        } catch (e: Exception) {
            Log.e("generateIps", "Ошибка генерации IP для подсети $subnet: ${e.message}")
            emptyList()
        }
    }


    private fun startActualScan() {
        val selectedSubnet = currentSubnet ?: run {
            Toast.makeText(this, "Подсеть не определена", Toast.LENGTH_SHORT).show()
            return
        }

        isScanning = true
        newDevicesCount = 0
        scanButton.visibility = View.GONE
        addButton.visibility = View.GONE
        stopButton.visibility = View.VISIBLE
        clearButton.visibility = View.GONE

        progressBar.progress = 0
        findViewById<LinearLayout>(R.id.progress_bar_layout).visibility = View.VISIBLE
        tvProgressPercent.text = "0%"

        val timeout = timeoutEdit.text.toString().toIntOrNull() ?: 1800
        val haPort = haPortEdit.text.toString().toIntOrNull() ?: 8123
        val portsString = portsEdit.text.toString()

        var portsList = parsePorts(portsString)
        if (portsList.isEmpty()) {
            Log.e(
                "startActualScan",
                "Не указаны порты, используются порты по умолчанию (80, 443, 8080, 81, 8888)"
            )

            portsList = listOf(80, 443, 8080, 81, 8888)
            Toast.makeText(
                this,
                "Не указаны порты, используются порты по умолчанию",
                Toast.LENGTH_SHORT
            ).show()
            portsEdit.setText("80, 443, 8080, 81, 8888")
            savePreferences()
        }

        val ips = generateIps(selectedSubnet)

        var scannedCount = 0
        val total = ips.size

        val networkDetails = getLastNetworkInfo()

        var networkName: String? = null
        var networkId: String? = null
        if (networkDetails != null) {
            networkName = networkDetails.name
            networkId = networkDetails.id
        } else {
            Log.e("detectDevice", "networkDetails = null")
        }

        scanJob = CoroutineScope(Dispatchers.IO).launch {
            val semaphore = Semaphore(40)

            ips.forEach { ip ->
                if (!isScanning) return@forEach

                launch {
                    semaphore.acquire()
                    try {

                        val devices = detectDevice(
                            ip = ip,
                            timeout = timeout,
                            haPort = haPort,
                            ports = portsList,
                            networkName = networkName,
                            networkId = networkId
                        )
                        if (devices.isNotEmpty()) {
                            withContext(Dispatchers.Main) {
                                devices.forEach { device ->
                                    if (addDeviceToList(device)) {
                                        newDevicesCount++
                                    }
                                }
                            }
                        }
                    } finally {
                        semaphore.release()
                        scannedCount++
                        withContext(Dispatchers.Main) {
                            val progress = (scannedCount * 100) / total
                            progressBar.progress = progress
                            tvProgressPercent.text = "$progress%"
                        }
                    }
                }
            }
        }

        scanJob?.invokeOnCompletion {
            runOnUiThread {
                isScanning = false
                scanButton.visibility = View.VISIBLE
                addButton.visibility = View.VISIBLE
                stopButton.visibility = View.GONE
                if (spoilerContent.isVisible) clearButton.visibility = View.VISIBLE

                progressBar.progress = 100
                tvProgressPercent.text = "100%"

                hideProgressJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(4000)
                    findViewById<LinearLayout>(R.id.progress_bar_layout).visibility = View.GONE
                }

                val msg = if (newDevicesCount > 0) "+$newDevicesCount новых" else "Новых нет"
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }

        savePreferences()
        showDeviceCountAnim()
    }


    private suspend fun createDeviceWithFavicon(
        ip: String,
        name: String,
        netName: String?,
        networkId: String?,
        port: Int? = null
    ): Device = withContext(Dispatchers.IO) {
        val faviconPath = downloadFavicon(ip, port)  // ← синхронно ждём
        val url = if (port != null && port != 80) "http://$ip:$port/" else "http://$ip/"
        Device(name, url, faviconPath, false, netName, networkId)
    }

    private suspend fun createDeviceFromUrl(
        finalUrl: String,
        name: String,
        netName: String?,
        networkId: String?
    ): Device = withContext(Dispatchers.IO) {
        val url = URL(finalUrl)
        val host = url.host
        val port = if (url.port > 0 && url.port != 80) url.port else null
        val faviconPath = downloadFavicon(host, port)  // ← синхронно ждём
        Device(name, finalUrl, faviconPath, false, netName, networkId)
    }




    private fun isHostReachableByPing(ip: String, timeoutSeconds: Int): Boolean {
        // -c 3: Отправить 3 пакета. Если хотя бы один дойдет, exit code будет 0.
        // -W 1: Таймаут для каждого пакета 1 секунда.
        // -i 0.2: Интервал между пакетами 0.2 секунды.
        // Общее время выполнения будет около 1 + 0.2 + 1 + 0.2 + 1 = ~2.4 секунды в худшем случае,
        // но завершится сразу после первого успешного ответа.
        val command = "ping -c 3 -W 1 -i 0.2 $ip"

        try {
            val process = Runtime.getRuntime().exec(command)

            // Ждем завершения процесса, но не дольше timeoutSeconds + 1 (доп. секунда на всякий случай).
            // Это защита от "зависания" процесса.
            val processExited =
                process.waitFor(timeoutSeconds.toLong() + 1, java.util.concurrent.TimeUnit.SECONDS)

            if (processExited && process.exitValue() == 0) {
                // Процесс завершился, и exit code == 0, значит хост ответил.
                return true
            } else {
                // Процесс либо завершился с ошибкой (exit code != 0, хост не ответил),
                // либо истек таймаут waitFor (processExited == false).
                // В любом случае, считаем пинг неудачным.
                if (!processExited) {
                    process.destroy() // Важно "убить" процесс, если он завис
                }
                Log.d("PingUtil", "Пинг для $ip не удался.")
                return false
            }

        } catch (e: Exception) {
            // Ошибки могут возникнуть, если команда ping не найдена или права не позволяют ее выполнить.
            Log.e("PingUtil", "Ошибка при выполнении пинга для $ip: ${e.message}")
            return false
        }
    }

    private suspend fun detectDevice(
        ip: String,
        timeout: Int,
        haPort: Int,
        ports: List<Int>,
        networkName: String?,
        networkId: String?
    ): List<Device> = withContext(Dispatchers.IO) {
        val checkWled = searchUiCheckbox.isChecked || wledCheckbox.isChecked
        val checkSettings = searchUiCheckbox.isChecked || settingsCheckbox.isChecked
        val checkHa = searchUiCheckbox.isChecked || haCheckbox.isChecked
        val checkUi = searchUiCheckbox.isChecked

        val pingTimeout = timeout / 2 //  // в два раза меньше таймаута   миллисекунд
        val portOpenTimeout = timeout //

        // Для ускорения сначала пингуем
        // быстро, но походу сразу много пингов не пропускает, и до некоторых не доходит
        val pingTimeoutInSeconds = (pingTimeout / 1000).coerceAtLeast(1)
        if(false && SettingsManager.isPingDevicesEnabled) {
            if (!isHostReachableByPing(ip, pingTimeoutInSeconds)) {
                Log.d("DeviceScan", "IP $ip пропущен: не отвечает на системный пинг.")
                return@withContext emptyList() // Хост не отвечает на пинг, пропускаем
            }
        }
        else {
            Log.d("DeviceScan", "Пинг отключен")
        }



        // затем открываем порты
        val portsToCheck = (ports + haPort).distinct()//порты плюс порт хом ассистант
        val openPorts = portsToCheck.associateWith { port ->
            isTcpPortOpen(ip, port, portOpenTimeout)
        }


        val actuallyOpenPorts = openPorts.filter { it.value }.keys
        Log.d(
            "DeviceScan",
            "IP $ip: Пинг OK. Открытые порты: ${if (actuallyOpenPorts.isNotEmpty()) actuallyOpenPorts.joinToString() else "нет"}"
        )
        val foundDevices = mutableListOf<Device>()

        // ничего не открылось - тикаем
        if (openPorts.values.none { it }) {
            Log.d("DeviceScan", "IP $ip пропущен: нет открытых портов из списка.")
            return@withContext emptyList()
        }


        // 1. WLED — какбудто быстрее всего, проверяем сначала
        if (checkWled && openPorts[80] == true) {
            try {
                val conn = URL("http://$ip/json/info").openConnection() as HttpURLConnection
                conn.connectTimeout = timeout
                conn.readTimeout = timeout
                conn.connect()
                if (conn.responseCode == 200) {
                    val json = JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
                    val name = json.optString("name", "WLED").takeIf { it.isNotBlank() } ?: "WLED"


                    conn.disconnect()
                    Log.w(
                        "detectDevice",
                        "WLED: ip: $ip name: $name networkName: $networkName networkId: $networkId"
                    )

                    foundDevices.add(createDeviceWithFavicon(ip, name, networkName, networkId))

                }
                conn.disconnect()
            } catch (_: Exception) {
            }
        }

        // 2. Settings Гайвера
        if (checkSettings && openPorts[80] == true) {
            try {
                val conn =
                    URL("http://$ip/settings?action=discover").openConnection() as HttpURLConnection
                conn.connectTimeout = timeout
                conn.readTimeout = timeout
                conn.connect()
                if (conn.responseCode == 200) {
                    val json = JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
                    val name = json.optString("name", "Settings $ip").takeIf { it.isNotBlank() }
                        ?: "Settings $ip"

                    conn.disconnect()
                    Log.w(
                        "detectDevice",
                        "Settings: ip: $ip name: $name networkName: $networkName networkId: $networkId"
                    )

                    foundDevices.add(createDeviceWithFavicon(ip, name, networkName, networkId))
                }


                conn.disconnect()
            } catch (_: Exception) {
            }
        }

        // 3. Home Assistant — на указанном порту если открыт
        if (checkHa && openPorts[haPort] == true) {
            try {
                val conn = URL("http://$ip:$haPort/").openConnection() as HttpURLConnection
                conn.connectTimeout = timeout
                conn.readTimeout = timeout
                conn.instanceFollowRedirects = true
                conn.connect()
                if (conn.responseCode in 200..399) {
                    val text = conn.inputStream.bufferedReader().use { it.readText() }.lowercase()
                    if ("home assistant" in text || "hass" in text || "lovelace" in text || "frontend" in text) {

                        conn.disconnect()
                        Log.w(
                            "detectDevice",
                            "Home Assistant: ip: $ip, name: Home Assistant, networkName: $networkName, networkId: $networkId"
                        )


                        foundDevices.add(
                            createDeviceWithFavicon(
                                ip,
                                "Home Assistant",
                                networkName,
                                networkId,
                                haPort
                            )
                        )

                    }
                }
                conn.disconnect()
            } catch (_: Exception) {
            }
        }

        // 4 Всё остальное
        if (checkUi) {
            suspend fun tryUrl(url: String): Device? {
                val finalUrl = getFinalWorkingUrl(url)
                if (finalUrl.isNullOrBlank()) return null
                val name = getDeviceNameFromUrl(finalUrl) ?: getTitleFromHead(finalUrl) ?: "UI $ip"

                return createDeviceFromUrl(finalUrl, name, networkName, networkId)
            }

            suspend fun checkCandidate(port: Int) {
                var device: Device? = null

                // 1. Попытка получить устройство по HTTP
                try {
                    device = tryUrl("http://$ip:$port/")
                } catch (_: Exception) {
                    // Игнорируем ошибку, device останется null
                }

                // 2. Если по HTTP не нашли (device все еще null), пробуем HTTPS
                if (device == null && port != 80) {
                    try {
                        device = tryUrl("https://$ip:$port/")
                    } catch (_: Exception) {
                        // Игнорируем ошибку, если и HTTPS не удался
                    }
                }

                // 3. Если в итоге устройство было найдено (неважно, по HTTP или HTTPS), добавляем его в список
                device?.let {
                    foundDevices.add(it)
                }
            }

            val openPortsList = openPorts.filter { it.value }.keys
            for (port in openPortsList) {
                // Проверяем, есть ли уже в списке устройство с таким же IP и ПОРТОМ.
                // Для этого извлекаем хост и порт из URL каждого уже найденного устройства.
                val portAlreadyFound = foundDevices.any { device ->
                    try {
                        val deviceUrl =
                            URL(device.url) // <-- Используем поле `url` из вашего класса `Device`
                        deviceUrl.host == ip && (deviceUrl.port.takeIf { it != -1 }
                            ?: deviceUrl.defaultPort) == port
                    } catch (e: Exception) {
                        // Если URL некорректен, считаем, что это не совпадение
                        false
                    }
                }

                if (!portAlreadyFound) {
                    // Если устройства на этом порту еще нет, запускаем проверку
                    checkCandidate(port)
                }
            }
        }

        return@withContext foundDevices // Изменение


    }

    private fun parsePorts(portsString: String): List<Int> {
        // 1. Используем регулярное выражение для разделения по одному или нескольким разделителям
        val portParts = portsString.split(Regex("[,;\\s]+"))

        return portParts
            // 2. Убираем пустые строки, если они появились (например, из-за пробелов в начале/конце)
            .filter { it.isNotBlank() }
            // 3. Преобразуем в число. Если не получается - пропускаем (безопасное преобразование)
            .mapNotNull { it.toIntOrNull() }
            // 4. Убеждаемся, что порт находится в допустимом диапазоне (1-65535)
            .filter { it in 1..65535 }
            // 5. Убираем дубликаты
            .distinct()
    }



    /**
     * Получает последние данные о текущей сети по её отпечатку.
     * @return Объект CurrentNetworkDetails с данными о сети или null, если сеть не найдена.
     */
    private fun getLastNetworkInfo(): CurrentNetworkDetails? {

        Log.d("getLastNetworkInfo()", "currentNetworkFingerprint = $currentNetworkFingerprint")

        splitNetwork = SettingsManager.isSplitNetworkEnabled

        if (false && !splitNetwork) {
            Log.d("getLastNetworkInfo()", "splitNetwork = $splitNetwork")

            return currentNetworkFingerprint?.let {
                CurrentNetworkDetails(
                    id = "n/a",
                    fingerprint = it,
                    ssid = "n/a",
                    name = "Без сети",
                    isTrusted = false,
                    subnet = currentSubnet,
                    bssidPrefix = null,
                    addBssid = null

                )
            }


        }

        // есть ли отпечаток текущей сети.
        // Если нет тикаем
        val fingerprint = currentNetworkFingerprint
        if (fingerprint == null) {
            Log.w(
                "getLastNetworkInfo",
                "Отпечаток сети (fingerprint) отсутствует, поиск невозможен."
            )
            return null
        }

        // Используем полученный отпечаток для поиска сети в менеджере.
        val foundNetwork = NetworkManager.findNetworkByFingerprint(this, fingerprint)

        // была ли найдена сеть
        if (foundNetwork == null) {
            Log.w(
                "getLastNetworkInfo",
                "Сеть с отпечатком '$fingerprint' не найдена в TrustedNetworkManager."
            )
            return null
        }

        // Шаг 4: Сеть найдена. Создаем и возвращаем детальный объект CurrentNetworkDetails.
        // Мы преобразуем найденный объект 'foundNetwork' в объект 'CurrentNetworkDetails'.
        return CurrentNetworkDetails(
            id = foundNetwork.id,
            fingerprint = foundNetwork.fingerprint,
            ssid = foundNetwork.ssid,
            name = foundNetwork.name,
            isTrusted = foundNetwork.isTrusted,
            subnet = foundNetwork.subnet,
            bssidPrefix = foundNetwork.mainBssidPrefix,
            addBssid = foundNetwork.additionalBssids
        )
    }

    // для хранения данных последней сети
    private data class CurrentNetworkDetails(
        val id: String,
        val fingerprint: String,
        val ssid: String,
        val name: String,
        val isTrusted: Boolean,
        val subnet: String?,
        val bssidPrefix: String?,
        val addBssid: List<String>?
    )


    private suspend fun getTitleFromHead(urlStr: String): String? = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(urlStr)
            conn = URL(urlStr).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 3000
            conn.readTimeout = 3500
            conn.instanceFollowRedirects = true
            conn.setRequestProperty("User-Agent", "Mozilla/5.0")

            if (conn is HttpsURLConnection) {

                //LocalTrustManager.applyToConnectionIfLocal(conn, url.host)  // ← используем url.host
                LocalTrustManager.applyToConnectionIfTrusted(conn, this@DeviceScanActivity)

            }

            conn.connect()
            if (conn.responseCode in 200..299) {
                val html = conn.inputStream.bufferedReader().use { it.readText() }
                val title = html.substringAfter("<title>", "").substringBefore("</title>", "")
                    .trim().takeIf { it.isNotBlank() && it.length > 1 }
                conn.disconnect()
                return@withContext title
            }
            conn.disconnect()
        } catch (e: Exception) {
            // игнор
        } finally {
            conn?.disconnect()
        }
        return@withContext null
    }

    private fun isTcpPortOpen(ip: String, port: Int, timeoutMs: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeoutMs)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    private data class HeadResult(
        val finalUrl: String,
        val contentType: String?,
        val responseCode: Int
    )


    private suspend fun downloadFavicon(host: String, port: Int? = null): String? =
        withContext(Dispatchers.IO) {
            return@withContext withTimeoutOrNull(5000) { // 5 сек — хватает даже на старые принтеры
                val bases = mutableListOf<String>()
                if (port != null && port != 80) {
                    bases += "http://$host:$port"
                } else {
                    bases += "http://$host"
                    bases += "https://$host"
                }

                for (baseUrl in bases) {
                    // Прямые пути — самые частые
                    val paths = listOf(
                        "/favicon.ico",
                        "/favicon.png",
                        "/apple-touch-icon.png",
                        "/apple-touch-icon-precomposed.png",
                        "/favicon.svg"
                    )
                    for (path in paths) {
                        val full = baseUrl + path
                        val saved = tryDownloadAndSaveFavicon(
                            full,
                            host,
                            path.substringAfterLast("/"),
                            port
                        )
                        if (saved != null) return@withTimeoutOrNull saved
                        yield()
                    }

                    // Парсим HTML только если это обычный порт
                    try {
                        val conn = URL(baseUrl).openConnection() as HttpURLConnection
                        conn.connectTimeout = 3000
                        conn.readTimeout = 3500
                        conn.instanceFollowRedirects = true
                        conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                        if (conn is HttpsURLConnection) {
                            //LocalTrustManager.applyToConnectionIfLocal(conn, URL(baseUrl).host)
                            LocalTrustManager.applyToConnectionIfTrusted(
                                conn,
                                this@DeviceScanActivity
                            )
                        }
                        conn.connect()

                        if (conn.responseCode in 200..299) {
                            val html = conn.inputStream.bufferedReader().use { it.readText() }
                            val doc = Jsoup.parse(html, baseUrl)

                            val selectors = arrayOf(
                                "link[rel~=(?i)icon]",
                                "link[rel=apple-touch-icon]",
                                "link[href$=.ico i], link[href$=.png i], link[href$=.svg i]"
                            )

                            for (sel in selectors) {
                                for (el in doc.select(sel)) {
                                    val href = el.absUrl("href")
                                    if (href.isNotBlank()) {
                                        val fname =
                                            href.substringAfterLast("/").ifEmpty { "favicon" }
                                        val saved =
                                            tryDownloadAndSaveFavicon(href, host, fname, port)
                                        if (saved != null) return@withTimeoutOrNull saved
                                        yield()
                                    }
                                }
                            }
                        }
                        conn.disconnect()
                    } catch (_: Exception) {
                    }
                }
                null
            }
        }


    private fun getLiveIpsFromArpTable(): List<String> {
        val liveIps = mutableListOf<String>()
        try {
            File("/proc/net/arp").useLines { lines ->
                lines.forEach { line ->
                    val parts = line.split(Regex("\\s+"))
                    if (parts.size >= 4) {
                        val ip = parts[0]
                        val flags = parts[3]
                        // 0x2 = COMPLETED (устройство живое и ответило)
                        if (flags == "0x2" && ip.matches(Regex("^\\d+\\.\\d+\\.\\d+\\.\\d+$"))) {
                            if (ip != "0.0.0.0" && !ip.endsWith(".1")) { // иногда роутер дублируется
                                liveIps.add(ip)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("getLiveIpsFromArpTable", "Не удалось прочитать ARP-таблицу", e)
        }
        return liveIps.distinct()
    }


    ///////////////////////////////////


    // ──────────────────────────────────────────────────────────────────
// 1. Получаем финальный рабочий URL (HTTP → HTTPS, редиректы, самоподписанные сертификаты)
    private suspend fun getFinalWorkingUrl(startUrl: String): String? =
        withContext(Dispatchers.IO) {
            var currentUrl = URL(startUrl)


            repeat(4) { // максимум 4 редиректа
                var conn: HttpURLConnection? = null
                val timeout = timeoutEdit.text.toString().toIntOrNull() ?: 1800
                try {
                    conn = currentUrl.openConnection() as HttpURLConnection
                    conn.connectTimeout = timeout
                    conn.readTimeout = timeout
                    conn.instanceFollowRedirects = false
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; DeviceScanner)")


                    if (conn is HttpsURLConnection) {
                        //LocalTrustManager.applyToConnectionIfLocal(conn, currentUrl.host)
                        LocalTrustManager.applyToConnectionIfTrusted(conn, this@DeviceScanActivity)
                    }

                    conn.connect()

                    val code = conn.responseCode
                    Log.wtf("getFinalWorkingUrl", "Redirect: $currentUrl → $code")

                    // Редирект — вручную идём дальше
                    if (code in 300..399) {
                        val location = conn.getHeaderField("Location") ?: return@withContext null
                        currentUrl =
                            URL(currentUrl, location)  // корректно обрабатывает относительные пути
                        conn.disconnect()
                        return@repeat
                    }

                    // Успешный ответ и HTML
                    if (code in 200..299 || code == 401 || code == 407) {
                        val contentType = conn.getHeaderField("Content-Type") ?: ""
                        if (contentType.contains("text/html", ignoreCase = true)) {
                            return@withContext currentUrl.toString()
                        }
                    }

                    conn.disconnect()
                } catch (e: javax.net.ssl.SSLHandshakeException) {
                    // Самоподписанный сертификат — считаем, что веб-УИ есть
                    if (conn is HttpsURLConnection) {
                        //LocalTrustManager.applyToConnectionIfLocal(conn, currentUrl.host)
                        LocalTrustManager.applyToConnectionIfTrusted(conn, this@DeviceScanActivity)
                    }
                } catch (e: Exception) {
                    val reason = when {
                        e is java.net.SocketTimeoutException -> "таймаут"
                        e is java.net.UnknownHostException -> "не найдено"
                        e is javax.net.ssl.SSLHandshakeException -> "SSL-ошибка (сертификат)"
                        else -> e.message ?: "неизвестно"
                    }
                    println("getFinalWorkingUrl → $startUrl → ошибка: $reason")
                    conn?.disconnect()
                    return@withContext null
                }
            }
            return@withContext null
        }

    // ──────────────────────────────────────────────────────────────────
// 2. Получаем название устройства
    private suspend fun getDeviceNameFromUrl(urlString: String): String? =
        withContext(Dispatchers.IO) {
            var conn: HttpURLConnection? = null
            val timeout = timeoutEdit.text.toString().toIntOrNull() ?: 1800
            try {
                val url = URL(urlString)
                conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = timeout
                conn.readTimeout = timeout * 2
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; DeviceScanner)")

                // Проверяем протокол URL, а не переменную conn
                if (conn is HttpsURLConnection) {
                    //LocalTrustManager.applyToConnectionIfLocal(conn, url.host)
                    LocalTrustManager.applyToConnectionIfTrusted(conn, this@DeviceScanActivity)
                }

                conn.connect()

                if (conn.responseCode !in 200..299 && conn.responseCode != 401 && conn.responseCode != 407) {
                    return@withContext null
                }

                val html = conn.inputStream.bufferedReader().use { it.readText() }
                if (html.length < 100) return@withContext null

                val doc = Jsoup.parse(html)
                doc.title()?.trim()?.takeIf { it.length > 3 }?.let { return@withContext it }

                doc.selectFirst("meta[property=og:title], meta[name=og:title]")?.attr("content")
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }?.let { return@withContext it }

                doc.selectFirst("meta[property=og:site_name]")?.attr("content")?.trim()
                    ?.takeIf { it.isNotBlank() }?.let { return@withContext it }

                val headerSelectors =
                    listOf("header", ".header", ".site-name", ".brand", ".logo", "h1", "h2")
                for (selector in headerSelectors) {
                    doc.select(selector).firstOrNull()?.ownText()?.trim()
                        ?.takeIf { it.length > 3 && it.contains(" ") && !it.contains("©") }
                        ?.let { return@withContext it }
                }

                doc.selectFirst("h1")?.text()?.trim()?.takeIf { it.length > 3 }
                    ?.let { return@withContext it }

            } catch (e: Exception) {
                println("Error parsing title from $urlString: ${e.message}")
            } finally {
                conn?.disconnect()
            }
            return@withContext null
        }
    // ──────────────────────────────────────────────────────────────────


    fun getCurrentBssid(): String? {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return null
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return null

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            val wifiInfo = capabilities.transportInfo as? WifiInfo

            return wifiInfo?.bssid
        }


        return null // Не подключен к Wi-Fi
    }


    private suspend fun createDeviceFromUrl2(finalUrl: String, name: String): Device =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(finalUrl)
                val host = url.host
                val port =
                    if (url.port != -1 && url.port != 80 && url.port != 443) url.port else null

                // Скачиваем фавикон с финального хоста
                val faviconPath = downloadFavicon(host, port)

                // Создаем устройство с финальным URL
                Device(name, finalUrl, faviconPath, false, null, null)
            } catch (e: Exception) {
                // Если что-то пошло не так, создаем устройство с оригинальным URL
                Device(name, finalUrl, null, false, null, null)
            }
        }


    private suspend fun createDeviceWithFavicon2(
        ip: String,
        name: String,
        port: Int? = null
    ): Device = withContext(Dispatchers.IO) {
        val faviconPath = downloadFavicon(ip, port)
        // Если указан порт, добавляем его в URL
        val url = if (port != null) "http://$ip:$port/" else "http://$ip/"
        Device(name, url, faviconPath, false, null, null)
    }


    private suspend fun downloadFavicon2(host: String, port: Int? = null): String? =
        withContext(Dispatchers.IO) {


            val baseUrl = if (port != null) "http://$host:$port" else "http://$host"
            Log.d("downloadFavicon2", "=== Поиск фавикона для $baseUrl ===")
            val directPaths = listOf(
                "/favicon.ico",
                "/favicon.png",
                "/apple-touch-icon.png",
                "/apple-touch-icon-precomposed.png"
            )
            for (path in directPaths) {
                val urlStr = baseUrl + path
                Log.d("downloadFavicon2", "Проверяем путь: $urlStr")
                val result =
                    tryDownloadAndSaveFavicon(urlStr, host, path.substringAfterLast("/"), port)
                if (result != null) {
                    Log.d("downloadFavicon2", "Фавикон найден: $path")
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
                    Log.d("downloadFavicon2", "HTML загружен: ${html.length} символов")
                    val doc = Jsoup.parse(html, baseUrl)
                    val relSelectors = listOf(
                        "link[rel~=(?i)^(icon|shortcut icon|apple-touch-icon|apple-touch-icon-precomposed)\$]",
                        "link[href*=.ico], link[href*=.png], link[href*=.svg], link[href*=.jpg], link[href*=.jpeg]"
                    )
                    for (selector in relSelectors) {
                        val links = doc.select(selector)
                        Log.d("downloadFavicon2", "Найдено ${links.size} по: $selector")
                        for (link in links) {
                            val href = link.attr("abs:href")
                            if (href.isNotEmpty()) {
                                Log.d("downloadFavicon2", "Проверяем: $href")
                                // Передаем порт в метод tryDownloadAndSaveFavicon
                                val result = tryDownloadAndSaveFavicon(
                                    href,
                                    host,
                                    href.substringAfterLast("/").takeIf { it.isNotEmpty() }
                                        ?: "favicon",
                                    port)
                                if (result != null) {
                                    Log.d("downloadFavicon2", "Фавикон загружен: $href")
                                    return@withContext result
                                }
                            }
                        }
                    }
                } else {
                    Log.d("downloadFavicon2", "HTML не загружен: HTTP ${conn.responseCode}")
                }
            } catch (e: Exception) {
                Log.e("downloadFavicon2", "Ошибка HTML: ${e.message}")
            }
            Log.d("downloadFavicon2", "Фавикон НЕ найден для $baseUrl")
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
            conn.connectTimeout = 2000
            conn.readTimeout = 2000
            conn.instanceFollowRedirects = true
            conn.setRequestProperty("Accept", "image/*,*/*;q=0.8")
            conn.connect()
            if (conn.responseCode == 200) {
                val contentType = conn.contentType?.lowercase() ?: ""
                if (contentType.contains("text/html")) {
                    Log.d(
                        "tryDownloadAndSaveFavicon",
                        "Пропуск: HTML вместо изображения ($contentType)"
                    )
                    return null
                }
                inputStream = conn.inputStream
                val bytes = inputStream.readBytes()
                if (bytes.isEmpty() || bytes.size > 500_000) {
                    Log.d(
                        "tryDownloadAndSaveFavicon",
                        "Пропуск: пусто или слишком большой файл (${bytes.size})"
                    )
                    return null
                }
                Log.d(
                    "tryDownloadAndSaveFavicon",
                    "Скачано: $urlStr | $contentType | ${bytes.size} байт"
                )
                val ext = when {
                    contentType.contains("svg") -> ".svg"
                    contentType.contains("png") -> ".png"
                    contentType.contains("jpeg") || contentType.contains("jpg") -> ".jpg"
                    contentType.contains("ico") -> ".ico"
                    else -> ".bin"
                }
                val cleanName = filename.replace(Regex("[^a-zA-Z0-9._-]"), "_") + ext

                // ИСПРАВЛЕНИЕ: добавляем порт в имя файла если он указан
                val fileIdentifier = if (port != null) "${host}_$port" else host
                val file = File(getFilesDir(), "favicon_${fileIdentifier}_$cleanName")

                FileOutputStream(file).use { it.write(bytes) }
                if (ext == ".svg") {
                    val pngFile = File(
                        getFilesDir(),
                        "favicon_${fileIdentifier}_${cleanName.substringBeforeLast(".")}.png"
                    )
                    if (renderSvgToPng(file, pngFile)) {
                        file.delete()
                        return pngFile.absolutePath
                    } else {
                        Log.w("tryDownloadAndSaveFavicon", "SVG не отрендерен, сохранён как .svg")
                        return file.absolutePath
                    }
                }
                return file.absolutePath
            } else {
                Log.d("tryDownloadAndSaveFavicon", "HTTP ${conn.responseCode}: $urlStr")
            }
        } catch (e: Exception) {
            Log.d("tryDownloadAndSaveFavicon", "Ошибка загрузки $urlStr: ${e.message}")
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
            val bitmap = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            svg.renderToCanvas(canvas)
            FileOutputStream(pngFile).use { out ->
                bitmap.compress(CompressFormat.PNG, 100, out)
            }
            Log.d("renderSvgToPng", "SVG отрендерен в PNG")
            true
        } catch (e: Exception) {
            Log.e("renderSvgToPng", "Ошибка рендеринга SVG: ${e.message}")
            false
        }
    }


    private fun addDeviceToList(device: Device): Boolean {
        if (allDevices.none { it.url == device.url }) {
            allDevices.add(0, device)
            val itemView = createDeviceItemView(device)


            Log.w(
                "addDeviceToList",
                "Устройство: ${device.name}, url: ${device.url}, network: ${device.networkName}, networkId: ${device.networkId}"
            )

            itemView.setBackgroundResource(R.drawable.new_device_background)


            devicesList.addView(itemView, 0)


            itemView.postDelayed({
                //
                val currentBg = itemView.background?.constantState?.newDrawable()?.mutate()
                itemView.background = currentBg

                // Плавно уменьшаем альфу фона
                val anim = ObjectAnimator.ofInt(currentBg, "alpha", 255, 0)
                anim.duration = 2200  // 2 секунды
                anim.interpolator =
                    android.view.animation.DecelerateInterpolator() // замедление в конце
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        itemView.background = null
                    }
                })
                anim.start()
            }, 5000)





            saveDevices()
            return true
        }
        return false
    }

    private fun loadExistingDevicesToList() {
        //  очищаем контейнер
        devicesList.removeAllViews()

        allDevices.forEach { device ->
            val deviceView = createDeviceItemView(device)

            devicesList.addView(deviceView)
        }
    }


    private fun createDeviceItemView(device: Device): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_device_scan, devicesList, false)
        val iconView = view.findViewById<ImageView>(R.id.icon)
        val nameText = view.findViewById<TextView>(R.id.text_name)
        val urlText = view.findViewById<TextView>(R.id.text_url)
        val editButton = view.findViewById<Button>(R.id.button_edit)
        val deleteBtn = view.findViewById<ImageButton>(R.id.button_delete)
        val openButton = view.findViewById<Button>(R.id.button_open)
        val openInBrowser = view.findViewById<ImageView>(R.id.open_in_browser)
        val network = view.findViewById<TextView>(R.id.device_network)

        view.tag = device.url

        updateDeviceIcon(iconView, device)

        nameText.text = device.name
        urlText.text = device.url
        network.text = device.networkName
        network.visibility = View.VISIBLE

        openButton.setOnClickListener {
            //val intent = Intent(this@DeviceScanActivity, MainActivity::class.java).apply {
            //    putExtra("SELECTED_DEVICE_NAME", device.name)
            //    putExtra("SELECTED_DEVICE_URL", device.url)
            //}
            //startActivity(intent)

            SettingsManager.deviceForOpen = device
            finish()
        }

        editButton.setOnClickListener {
            showEditDeviceDialog(device, view)
        }

        openInBrowser.setOnClickListener {
            AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialog)
                .setTitle("Открыть в браузере?")
                .setMessage("Вы хотите открыть ${device.url} во внешнем браузере?")
                .setPositiveButton("Да, открыть") { _, _ ->
                    //  Да
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, device.url.toUri())
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Нет") { dialog, _ ->
                    // Нет
                    dialog.dismiss()
                }
                .show()
        }


        deleteBtn.setOnClickListener {
            val networkIdToDeleteFrom = device.networkId
            val networkName = device.networkName ?: "неизвестной сети"
            var message = "Вы уверены, что хотите удалить устройство \"<b>${device.name}</b>\""
            if(SettingsManager.isSplitNetworkEnabled)  message +=  " из сети <b>$networkName</b>"


            // Проверяем, есть ли у устройства ID сети. Если нет, это аномалия.
            if (networkIdToDeleteFrom == null) {
                Toast.makeText(this, "Ошибка: не удалось определить сеть устройства", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialogErr)
                .setTitle("Удалить устройство?")
                .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton("Удалить") { _, _ ->

                    // сеть из которой нужно удалить
                    val targetNetwork = NetworkManager.findNetworkById(this, device.networkId)

                    if (targetNetwork != null) {
                        // Загружаем устройства ЭТОЙ сети
                        val devicesInTargetNetwork = deviceManager.loadDevices(targetNetwork.fingerprint)

                        // Удаляем
                        val removed = devicesInTargetNetwork.removeAll { it.url == device.url }

                        if (removed) {
                            // Сохраняем
                            deviceManager.saveDevices( devicesInTargetNetwork, targetNetwork.fingerprint)

                            // Удаляем View
                            devicesList.removeView(view)

                            // Обновляем
                            allDevices.removeAll { it.url == device.url && it.networkId == networkIdToDeleteFrom }

                            Toast.makeText(this, "Устройство удалено из сети '$networkName'", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Не удалось найти устройство для удаления", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this, "Не удалось найти сеть '$networkName'", Toast.LENGTH_SHORT).show()
                    }

                }
                .setNegativeButton("Отмена", null)
                .show()
        }




            return view
    }



    private fun showEditDeviceDialog(device: Device, itemView: View) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_device, null)
        val editName = dialogView.findViewById<EditText>(R.id.edit_manual_name)
        val editUrl = dialogView.findViewById<EditText>(R.id.edit_manual_url)
        val spinnerFavicon = dialogView.findViewById<Spinner>(R.id.spinner_favicon)
        val preview = dialogView.findViewById<ImageView>(R.id.image_favicon_preview)

        editName.setText(device.name)
        editUrl.setText(device.url)


        currentEditDialogPreview = preview
        currentSelectedFaviconPath = device.faviconPath

        data class FaviconItem(val title: String, val resourceName: String?, val filePath: String?)

        val faviconList = mutableListOf<FaviconItem>().apply {
            add(FaviconItem("По умолчанию", null, null))
            add(FaviconItem("Выбрать с телефона...", null, "pick_from_device"))
            add(FaviconItem("Дом", "ic_device_home", null))
            add(FaviconItem("Камера", "ic_device_camera", null))
            add(FaviconItem("Роутер", "ic_device_router", null))
            add(FaviconItem("Настройки", "ic_device_settings", null))
            add(FaviconItem("Телефон", "ic_device_call", null))
            add(FaviconItem("ПК", "ic_device_desktop", null))
            add(FaviconItem("Устройство", "ic_device_device", null))
            add(FaviconItem("Принтер", "ic_device_print_24px", null))
            add(FaviconItem("Ворота", "ic_device_gate_24px", null))
            add(FaviconItem("Шурик", "ic_device_tools_power_drill_24px", null))
            add(FaviconItem("Погода снег", "ic_device_weather_snowy_24px", null))
            add(FaviconItem("Ложка-вилка", "ic_device_flatware_24px", null))
            add(FaviconItem("Потрогай траву", "ic_device_grass_24px", null))
            add(FaviconItem("Душ", "ic_device_shower_24px", null))
            add(FaviconItem("Кровать", "ic_device_bed_24px", null))
            add(FaviconItem("Диван", "ic_device_chair_24px", null))
            add(FaviconItem("Бутер (буржуйский)", "ic_device_lunch_dining_24px", null))
            add(FaviconItem("Спать", "ic_device_hotel_24px", null))
            add(FaviconItem("Щит", "ic_device_shield_24px", null))
            add(FaviconItem("Диск", "ic_device_hard_disk_24px", null))
            add(FaviconItem("Хард драйв", "ic_device_hard_drive_24px", null))
            add(FaviconItem("Мобильный", "ic_device_mobile_24px", null))
            add(FaviconItem("Сканер", "ic_device_scanner_24px", null))
            add(FaviconItem("Розетка", "ic_device_power_24px", null))
            add(FaviconItem("Термостат", "ic_device_device_thermostat_24px", null))
            add(FaviconItem("Часы", "ic_device_chronic_24px", null))
            add(FaviconItem("Машина", "ic_device_car_gear_24px", null))
            add(FaviconItem("Гаечный ключ", "ic_device_build_circle_24px", null))
            add(FaviconItem("Блютус", "ic_device_bluetooth_24px", null))
            add(FaviconItem("Ресивер", "ic_device_audio_video_receiver", null))
            add(FaviconItem("Настройка", "ic_device_power_settings_new_24px", null))
            add(FaviconItem("Настройка 2", "ic_device_settings_remote_24px", null))

            // Загруженные иконки
            filesDir.listFiles()
                ?.filter { it.name.startsWith("favicon_") || it.name.startsWith("custom_favicon_") }
                ?.forEach { file ->
                    val name = file.name
                        .removePrefix("favicon_").removePrefix("custom_favicon_")
                        .substringBeforeLast(".")
                        .replace("_", " ")
                        .replace(Regex("\\b\\w")) { it.value.uppercase() }
                    add(FaviconItem("Загружен: $name", null, file.absolutePath))
                }
        }

        // КАСТОМНЫЙ АДАПТЕР — иконка + текст
        class FaviconSpinnerAdapter(
            context: Context,
            private val items: List<FaviconItem>
        ) : ArrayAdapter<FaviconItem>(context, R.layout.item_favicon_spinner, items) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
                createView(position, convertView, parent)

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View =
                createView(position, convertView, parent)

            private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.item_favicon_spinner, parent, false)

                val item = items[position]
                val icon = view.findViewById<ImageView>(R.id.icon)
                val title = view.findViewById<TextView>(R.id.title)
                title.text = item.title

                when {
                    item.filePath == "pick_from_device" -> {
                        icon.setImageResource(android.R.drawable.ic_menu_gallery)
                    }

                    item.filePath != null -> {
                        BitmapFactory.decodeFile(item.filePath)?.let { icon.setImageBitmap(it) }
                            ?: icon.setImageResource(R.drawable.ic_device_device)
                    }

                    item.resourceName != null -> {
                        val resId = resources.getIdentifier(
                            item.resourceName,
                            "drawable",
                            context.packageName
                        )
                        icon.setImageResource(if (resId != 0) resId else R.drawable.ic_device_device)
                    }

                    else -> icon.setImageResource(R.drawable.ic_device_device)
                }
                return view
            }
        }

        spinnerFavicon.adapter = FaviconSpinnerAdapter(this, faviconList)

        // Показ текущей иконки в превью
        fun updatePreview() {
            val path = currentSelectedFaviconPath
            if (path == null) {
                preview.setImageResource(R.drawable.ic_device_device)
                return
            }
            if (path.startsWith("ic_")) {
                val resId = resources.getIdentifier(path, "drawable", packageName)
                preview.setImageResource(if (resId != 0) resId else R.drawable.ic_device_device)
            } else {
                val file = File(path)
                if (file.exists()) {
                    preview.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                } else {
                    preview.setImageResource(R.drawable.ic_device_device)
                }
            }
        }
        updatePreview()

        // Выбор в спиннере
        spinnerFavicon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = faviconList[position]
                if (item.filePath == "pick_from_device") {
                    pickImageFromGallery()
                    spinnerFavicon.setSelection(0)
                } else {
                    currentSelectedFaviconPath = item.filePath ?: item.resourceName
                    updatePreview()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Устанавливаем текущий пункт
        val currentIndex = faviconList.indexOfFirst {
            it.filePath == device.faviconPath || it.resourceName == device.faviconPath
        }
        if (currentIndex >= 0) spinnerFavicon.setSelection(currentIndex)

        val dialogBuilder =  AlertDialog.Builder(this, R.style.AppTheme_RoundedAlertDialog)
            .setTitle("Редактировать устройство")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = editName.text.toString().trim()
                val newUrlRaw = editUrl.text.toString().trim()
                if (newName.isEmpty() || newUrlRaw.isEmpty()) {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Нормализация URL
                val newUrl = normalizeUrl(newUrlRaw)

                val index = allDevices.indexOfFirst { it.url == device.url }
                if (index != -1) {
                    val isBuiltin =
                        currentSelectedFaviconPath?.startsWith("ic_") == true || currentSelectedFaviconPath == null
                    val newNetworkName = device.networkName
                    val networkId = device.networkId

                    allDevices[index] = Device(
                        name = newName,
                        url = newUrl,
                        faviconPath = currentSelectedFaviconPath,
                        isBuiltinIcon = isBuiltin,
                        networkName = newNetworkName,
                        networkId = networkId
                    )
                    saveDevices()
                    updateSingleDeviceView(itemView, allDevices[index])
                    Toast.makeText(this, "Устройство обновлено", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .setOnDismissListener {
                currentEditDialogPreview = null
                currentSelectedFaviconPath = null
            }
        if (currentNetworkFingerprint != App.ALL_NETWORKS_FINGERPRINT) {
            dialogBuilder.setNeutralButton("Порядок") { _, _ ->
                showReorderDialog()
            }
        }

        dialogBuilder.show()
    }

    private fun normalizeUrl(urlInput: String): String {
        // Обрезаем пробелы в начале/конце и удаляем все пробелы внутри строки
        val trimmedUrl = urlInput.trim().replace(" ", "")
        return when {
            // Если уже начинается с http:// или https:// - оставляем как есть
            trimmedUrl.startsWith("http://") || trimmedUrl.startsWith("https://") -> {
                trimmedUrl
            }
            // Если это IP-адрес (содержит только цифры, точки и возможно порт) - используем http
            trimmedUrl.matches(Regex("""^\d{1,3}(\.\d{1,3}){3}(:\d+)?$""")) -> {
                "http://$trimmedUrl"
            }
            // Если это localhost - используем http
            trimmedUrl.startsWith("localhost") -> {
                "http://$trimmedUrl"
            }
            // Для всех остальных случаев (доменные имена) используем https
            else -> {
                "https://$trimmedUrl"
            }
        }
    }


    private fun updateSingleDeviceView(view: View, device: Device) {
        view.findViewById<TextView>(R.id.text_name).text = device.name
        view.findViewById<TextView>(R.id.text_url).text = device.url
        updateDeviceIcon(view.findViewById(R.id.icon), device)
    }


    private fun updateDeviceIcon(iconView: ImageView, device: Device) {
        if (device.faviconPath != null) {
            if (device.isBuiltinIcon || device.faviconPath.startsWith("ic_")) {
                // Это встроенная иконка из drawable
                val resId = resources.getIdentifier(device.faviconPath, "drawable", packageName)
                if (resId != 0) {
                    iconView.setImageResource(resId)
                } else {
                    iconView.setImageResource(R.drawable.ic_device_device)
                }
            } else {
                // Это скачанный файл
                val file = File(device.faviconPath)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    iconView.setImageBitmap(bitmap)
                } else {
                    iconView.setImageResource(R.drawable.ic_device_device)
                }
            }
        } else {
            iconView.setImageResource(R.drawable.ic_device_device)
        }
    }




    /**
     * Загружает устройства
     */
    private fun loadDevices() {
        allDevices.clear()

        val devicesToShow = when {

            splitNetwork && currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT ->
                deviceManager.loadAllDevicesWithDuplicates()

            splitNetwork ->
                deviceManager.loadDevices(currentNetworkFingerprint)

            else ->
                deviceManager.loadDevices(currentNetworkFingerprint)
        }

        allDevices.addAll(devicesToShow)


        loadExistingDevicesToList()

    }


    /**
     * Загружает устройства для указанной сети, используя DeviceManager.
     * @param networkFingerprint "Отпечаток" сети, для которой нужно загрузить устройства.
     *                           Если null, загружаются все устройства (режим "разделение выключено").
     */
    private fun loadDevices(networkFingerprint: String?) {
        try {

            allDevices.clear()

            val loadedDevices =
                if (networkFingerprint == App.ALL_NETWORKS_FINGERPRINT) {
                    deviceManager.loadAllDevicesWithDuplicates()
                } else if (networkFingerprint == App.DEFAULT_NETWORK_FINGERPRINT) {
                    deviceManager.loadDevices(networkFingerprint)
                } else if (networkFingerprint == null) {
                    Log.e(
                        "loadDevices",
                        "Загружено ${allDevices.size} устройств для fingerprint: null"
                    )

                    return
                } else {
                    deviceManager.loadDevices(networkFingerprint)

                }

            Log.d(
                "loadDevices",
                "Загружено ${loadedDevices.size} устройств для fingerprint: '${networkFingerprint ?: "all"}'"
            )


            allDevices.addAll(loadedDevices)
            loadExistingDevicesToList()


        } catch (e: Exception) {
            Log.e("loadDevices", "Ошибка загрузки устройств", e)
            Toast.makeText(this, "Ошибка загрузки устройств: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }


    /**
     */
    private fun saveDevices() {
        // Нельзя сохранять, если выбраны "Все сети"
        if (splitNetwork && currentNetworkFingerprint == App.ALL_NETWORKS_FINGERPRINT) {
            Log.e("saveDevices", "Нельзя сохранять, если выбрано -  Все сети")
            return
        }


        val fingerprintToSave = when {
            // Если режим разделения включен, используем текущий выбранный fingerprint
            splitNetwork -> currentNetworkFingerprint
            // Если выключен, используем ("Неизвестная сеть")
            else -> App.DEFAULT_NETWORK_FINGERPRINT
        }

        deviceManager.saveDevices(allDevices, fingerprintToSave)
        Log.d("saveDevices", "Сохранено устройств: ${allDevices.size} из сети: $fingerprintToSave ")

        //// если разделение отключено
        //if (fingerprintToSave != null || !splitNetwork) {
        //    deviceManager.saveDevices(allDevices, fingerprintToSave)
//
        //    Log.d("saveDevices", "Сохранено устройств: ${allDevices.size}")
        //}
        //// если разделение включено но отпечаток не определен, сохранять некуда
        //else {
        //    Log.e("", "отпечаток не определен, сохранение не произойдет")
        //}
    }


    private val PICK_IMAGE_REQUEST = 1001

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(
            Intent.EXTRA_MIME_TYPES,
            arrayOf("image/png", "image/jpeg", "image/jpg", "image/webp")
        )
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data?.data != null) {
            val uri = data.data!!
            val savedPath = copyImageToInternalStorage(uri)
            if (savedPath != null) {
                currentSelectedFaviconPath = savedPath
                currentEditDialogPreview?.let { preview ->
                    preview.setImageURI(uri)

                    BitmapFactory.decodeFile(savedPath)?.let { preview.setImageBitmap(it) }
                }
                Toast.makeText(this, "Иконка выбрана!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyImageToInternalStorage(uri: android.net.Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(filesDir, "custom_favicon_${System.currentTimeMillis()}.png")
            inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка сохранения иконки", Toast.LENGTH_SHORT).show()
            null
        }
    }




    private fun generateSpannedTextFor(tooltipType: TooltipType): Spanned {
        val imageGetter = Html.ImageGetter { source ->
            val drawableId = when (source) {
                "wifi_icon"    -> R.drawable.ic_wifi_circle
                "wifi_trust"    -> R.drawable.ic_wifi_circle_trust
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
                val drawable = ContextCompat.getDrawable(this, drawableId)!!
                val iconSize = (15f * resources.displayMetrics.density).toInt()
                drawable.setBounds(0, 0, iconSize, iconSize)
                return@ImageGetter drawable
            }
            return@ImageGetter null
        }

        val htmlString = when (tooltipType) {
            TooltipType.NETWORK_STATUS -> """
                        <b>Статус сети:</b><br>
                        <img src="wifi_icon"/> - wifi, соответствует сети устройства<br>
                        <img src="wifi_trust"/> - wifi, соответствует сети устройства, не проверяем https сертификаты!<br>
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
            TooltipType.PORTS_INFO -> """
                        <b>Порты для сканирования</b><br>
                        Укажите порты, для сканирования через запятую<br>
                        Если все удалить, перед началом сканирования запишутся порты по умолчанию
                    """.trimIndent()
            TooltipType.TIMEOUT_INFO -> """
                        <b>Таймаут сканирования</b><br>
                        в миллисекундах<br>
                        применяется к таймауту пинга, открытия портов и получении имени<br>
                        по умолчанию - 1800мс<br><br>
                        Перед сканированием адрес пингуется с таймаутом/2 но не менее 1 секунды<br>
                        Если ваши устройства не успевают отвечать то имеет смысл увеличить, или сканировать повторно.
                    """.trimIndent()
            TooltipType.QUESTION -> """
                        <b>Настройка сканирования</b><br><br>
                        <b>Таймаут</b> ответа устройства в миллисекундах<br>
                        <b>Порты</b> - применяются при поиске веб интерфейсов. Укажите порты, для сканирования через запятую.
                        Если все удалить, перед началом сканирования запишутся порты по умолчанию.<br> 
                        <b>Порт Home Assistant</b> - обычно 8123<br> 
                        <b>Подсеть</b> - выберите подсеть для сканирования или введите вручную.<br> 
                        <b>Свайп вниз</b> - на области устройств - обновление списка устройств, сети и проверка доступности.

                        
                        
                    """.trimIndent()

        }

        return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY, imageGetter, null)
    }
    private fun createConfiguredBalloon(spannedText: Spanned): Balloon {
        return Balloon.Builder(this)
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
            .setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.spinner_dropdown_background))
            .setBalloonAnimation(BalloonAnimation.FADE)
            .setDismissWhenClicked(true)
            .setLifecycleOwner(this)
            .build()
    }
    fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrColor, typedValue, true)
        return typedValue.data
    }


    private enum class TooltipType {
        NETWORK_STATUS,
        CONNECTION_STATUS,
        PORTS_INFO,
        TIMEOUT_INFO,
        QUESTION

    }
    //////////////////////

    private fun showReorderDialog() {
        val currentNetworkName = if (splitNetwork) {
            knownNetworksList.find { it.fingerprint == currentNetworkFingerprint }?.name ?: "Неизвестная сеть"
        } else {
            null // Не показываем имя, если разделение выключено
        }

        // Создаем неизменяемую копию списка перед передачей
        val dialog = ReorderDialogFragment.newInstance(allDevices.toList(), currentNetworkName)
        dialog.show(supportFragmentManager, ReorderDialogFragment.TAG)
    }








    // Интерфейс для начала перетаскивания при нажатии на иконку
    private interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    // Внутренний класс для адаптера списка устройств
    private class ReorderAdapter(
        private val context: Context,
        private val devices: MutableList<Device>,
        private val dragStartListener: OnStartDragListener
    ) : RecyclerView.Adapter<ReorderAdapter.ItemViewHolder>(), ItemTouchHelperAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reorder_device, parent, false)
            return ItemViewHolder(view)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val device = devices[position]
            holder.deviceName.text = device.name


            // Загрузка иконки
            device.faviconPath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    try {if (path.endsWith(".svg")) {
                        val svg = SVG.getFromInputStream(FileInputStream(file))
                        if (svg.documentWidth != -1f) {
                            // Создаем Bitmap нужного размера
                            val bitmap =
                                createBitmap(svg.documentWidth.toInt(), svg.documentHeight.toInt())
                            // Создаем Canvas для рисования на этом Bitmap
                            val canvas = Canvas(bitmap)
                            // Рендерим SVG на Canvas
                            svg.renderToCanvas(canvas)
                            holder.deviceIcon.setImageBitmap(bitmap)
                        } else {
                            holder.deviceIcon.setImageResource(R.drawable.ic_settings) // Иконка по умолчанию, если SVG некорректен
                        }
                    } else {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        holder.deviceIcon.setImageBitmap(bitmap)
                    }
                    } catch (e: Exception) {
                        Log.e("ReorderAdapter", "Ошибка загрузки иконки для ${device.name}", e)
                        holder.deviceIcon.setImageResource(R.drawable.ic_settings) // Иконка по умолчанию
                    }
                } else {
                    holder.deviceIcon.setImageResource(R.drawable.ic_settings)
                }
            } ?: holder.deviceIcon.setImageResource(R.drawable.ic_settings)



            // Устанавливаем слушатель для начала перетаскивания
            holder.dragHandle.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder)
                }
                false
            }
        }

        override fun getItemCount(): Int = devices.size

        override fun onItemMove(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(devices, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(devices, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }

        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val deviceIcon: ImageView = itemView.findViewById(R.id.iv_device_icon)
            val deviceName: TextView = itemView.findViewById(R.id.tv_device_name)
            val dragHandle: ImageView = itemView.findViewById(R.id.iv_drag_handle)
        }
    }

    // Внутренний класс для обработки жестов перетаскивания
    private class SimpleItemTouchHelperCallback(private val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

        override fun isLongPressDragEnabled(): Boolean = false
        override fun isItemViewSwipeEnabled(): Boolean = false

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // Свайп не используется
        }
    }

    // Интерфейс для адаптера, чтобы ItemTouchHelper мог с ним взаимодействовать
    private interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int)
    }


   // окно изменения порядка
    class ReorderDialogFragment : DialogFragment(), OnStartDragListener {

        private var touchHelper: ItemTouchHelper? = null
        private lateinit var reorderAdapter: ReorderAdapter
        private lateinit var devicesCopy: MutableList<Device>

        companion object {
            const val TAG = "ReorderDialog"
            const val REQUEST_KEY = "reorder_request"
            const val BUNDLE_KEY_DEVICES = "devices_bundle"
            const val BUNDLE_KEY_NETWORK_NAME = "network_name_bundle"

            fun newInstance(devices: List<Device>, networkName: String?): ReorderDialogFragment {
                val args = Bundle().apply {

                    putString(BUNDLE_KEY_DEVICES, Gson().toJson(devices))
                    putString(BUNDLE_KEY_NETWORK_NAME, networkName)
                }
                return ReorderDialogFragment().apply {
                    arguments = args
                }
            }
        }


        override fun onStart() {
            super.onStart()
            // Получаем текущее окно диалога
            dialog?.window?.apply {
                // Устанавливаем ширину на всю ширину экрана
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,)


                setBackgroundDrawable(null)
            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.dialog_reorder_devices, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val devicesJson = requireArguments().getString(BUNDLE_KEY_DEVICES)

            val type = object : TypeToken<List<Device>>() {}.type // <-- Указываем базовый List
            // Сначала получаем как неизменяемый список
            val immutableDevices: List<Device> = Gson().fromJson(devicesJson, type)
            // Затем создаем из него изменяемую копию для работы
            devicesCopy = immutableDevices.toMutableList()



            val networkName = requireArguments().getString(BUNDLE_KEY_NETWORK_NAME)

            val tvNetworkName: TextView = view.findViewById(R.id.tv_network_name)
            val recyclerView: RecyclerView = view.findViewById(R.id.rv_reorder_devices)
            val saveButton: Button = view.findViewById(R.id.btn_save_order)
            val closeButton: Button = view.findViewById(R.id.btn_close_order)

            if (networkName != null) {
                tvNetworkName.text = networkName
                tvNetworkName.visibility = View.VISIBLE
            } else {
                tvNetworkName.visibility = View.GONE
            }


            reorderAdapter = ReorderAdapter(requireContext(), devicesCopy, this)

            recyclerView.layoutManager = LinearLayoutManager(context)

            recyclerView.adapter = reorderAdapter //

            val callback = SimpleItemTouchHelperCallback(reorderAdapter)
            touchHelper = ItemTouchHelper(callback)
            touchHelper?.attachToRecyclerView(recyclerView)

            saveButton.setOnClickListener {
                // Отправляем результат обратно в DeviceScanActivity
                val resultJson = Gson().toJson(devicesCopy)
                setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_DEVICES to resultJson))
                dismiss()
            }
            closeButton.setOnClickListener {
                dismiss()
            }
        }

        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
            touchHelper?.startDrag(viewHolder)
        }
    }












}