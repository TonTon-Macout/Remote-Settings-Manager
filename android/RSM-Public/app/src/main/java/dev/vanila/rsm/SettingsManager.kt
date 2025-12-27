package dev.vanila.rsm

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONObject

/**
 * Синглтон для централизованного управления всеми настройками приложения.
 * Обеспечивает безопасный доступ к SharedPreferences из любого места в коде.
 *
 * Инициализируется один раз в классе Application.
 * Пример использования:
 *   val theme = SettingsManager.appTheme
 *   SettingsManager.isSnowmanEnabled = true
 */
object SettingsManager {

    private const val PREFS_NAME = "settings_prefs" // Имя файла настроек
    private const val DEFAULT_ICON_COLOR = "#FFFFFFFF"

    private const val KEY_IS_APP_FIRST_RUN = "is_first_run"
    private const val KEY_IS_APP_FIRST_MIGRATE = "is_first_migrate"
    private const val KEY_IS_SNOWMAN_FIRST_RUN = "is_snowman_first_run"

    // --- Ключи для всех настроек ---
    // Общие и UI настройки
    private const val KEY_SNOWMAN = "snowman_enabled"
    private const val KEY_SNOWMAN_ANIMATION = "snowman_animation_enabled"
    private const val KEY_EASTER_EGG = "easter_egg_enabled"
    private const val KEY_BTN_BACK = "btn_back_enabled"
    private const val KEY_ADDRESS_INPUT = "address_input_enabled"
    private const val KEY_CHECK_VERSION = "check_version_enabled"
    private const val KEY_OPEN_LAST_DEVICE = "open_last_device"
    private const val KEY_SOLID_COLOR_ICON = "solid_color_icon"
    private const val KEY_LABEL = "label_enabled"
    private const val KEY_ICON_IN_LABEL = "icon_in_label_enabled"
    private const val KEY_THEME = "app_theme"
    private const val KEY_ICON_COLOR = "icon_color"

    // флаги
    private const val KEY_THEME_WAS_CHANGED = "theme_was_changed"
    private const val KEY_OPEN_DEVICE = "open_device"
    private const val KEY_OPEN_PANEL = "open_panel"

    // Настройки сети и сканирования
    private const val KEY_SPLIT_NETWORK = "split_network_by_ssid"
    private const val KEY_SPLIT_NETWORK_MANUAL = "split_network_manually"
    private const val KEY_SETTINGS_CHECK = "settings_checkbox"
    private const val KEY_WLED_CHECK = "wled_checkbox"
    private const val KEY_HA_CHECK = "ha_checkbox"
    private const val KEY_HA_PORT = "ha_port"
    private const val KEY_SEARCH_UI_CHECK = "search_ui_checkbox"
    private const val KEY_TIMEOUT = "timeout"
    private const val KEY_PORTS = "ports"
    private const val KEY_CHECK_ACTIVE = "check_active"

    private const val KEY_SUBNET_POS = "subnet_position"
    private const val KEY_CURRENT_SUBNET = "current_subnet"
    private const val KEY_CUSTOM_SUBNETS = "custom_subnets"

    private const val KEY_LAST_NETWORK_ID = "last_network"
    private const val KEY_LAST_DEVICE = "last_device"
    private const val KEY_LAST_NETWORK_FINGERPRINT = "last_network_fingerprint"

    private const val KEY_DONT_SHOW_LOCATION_DIALOG = "dont_show_location_dialog"


    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }



    /**
     * Загружает и сохраняет объект последнего устройства.
     * Возвращает null, если ни одно устройство еще не было сохранено.
     *
     * Пример использования:
     * val currentDevice = SettingsManager.lastDevice
     * currentDevice?.let { println(it.name) }
     *
     * SettingsManager.lastDevice = Device("My PC", "192.168.1.10", null, false, "wifi_ssid", "1")
     */
    @Deprecated(
        "Последнее устройство теперь сохраняется и получается через NetworkManager\n" +
                "NetworkManager.updateLastDeviceByFingerprint(context, fingerprint, device)\n" +
                "NetworkManager.getLastDeviceByFingerprint()\n"  +
                "NetworkManager.getLastDeviceByID(context, id, device)\n" +
                "NetworkManager.updateLastDeviceByID()",
        ReplaceWith("")
    )
    var lastDevice: Device?
        get() {
            val jsonString = prefs.getString(KEY_LAST_DEVICE, null)
            return jsonString?.let {
                try {
                    Device.fromJsonObject(JSONObject(it))
                } catch (e: Exception) {
                    // В случае ошибки парсинга JSON, возвращаем null
                    null
                }
            }
        }
        set(value) {
            if (value != null) {
                // Преобразуем объект в JSONObject, а затем в строку
                val jsonString = value.toJsonObject().toString()
                prefs.edit { putString(KEY_LAST_DEVICE, jsonString) }
            } else {
                // Если передали null, удаляем ключ из настроек
                prefs.edit { remove(KEY_LAST_DEVICE) }
            }
        }


    /**
     * Первый ли запуск.
     * @return `true` только при самом первом запуске приложения, в остальных случаях `false`.
     */
    fun isFirstRun(): Boolean {
        val isFirstRun = prefs.getBoolean(KEY_IS_APP_FIRST_RUN, true)
        return isFirstRun
    }
    fun setFirstRun() {
        prefs.edit()
            .putBoolean(KEY_IS_APP_FIRST_RUN, false)
            .commit() // ИСПОЛЬЗУЕМ commit() ВМЕСТО apply()
    }
    /**
     * Нужно ли показывать анимацию СНЕГОВИКА
     * @return `true`, если анимацию нужно показать, иначе `false`.
     */
    fun isFirstRunMigrate(): Boolean {
        val isFirstRun = prefs.getBoolean(KEY_IS_APP_FIRST_MIGRATE, true)
        if (isFirstRun) {
            prefs.edit { putBoolean(KEY_IS_APP_FIRST_MIGRATE, false) }

        }
        return isFirstRun
    }

    /**
     * Нужно ли показывать анимацию СНЕГОВИКА
     * @return `true`, если анимацию нужно показать, иначе `false`.
     */
    fun isFirstRunSnowMan(): Boolean {
        val isFirstRun = prefs.getBoolean(KEY_IS_SNOWMAN_FIRST_RUN, true)
        if (isFirstRun) {
            prefs.edit { putBoolean(KEY_IS_SNOWMAN_FIRST_RUN, false) }
            
        }
        return isFirstRun
    }




    // Общие и UI настройки
    var isSnowmanEnabled: Boolean
        get() = prefs.getBoolean(KEY_SNOWMAN, false)
        set(value) = prefs.edit { putBoolean(KEY_SNOWMAN, value) }

    var isSnowmanAnimationEnabled: Boolean
        get() = prefs.getBoolean(KEY_SNOWMAN_ANIMATION, false)
        set(value) = prefs.edit { putBoolean(KEY_SNOWMAN_ANIMATION, value) }

    var isEasterEggEnabled: Boolean
        get() = prefs.getBoolean(KEY_EASTER_EGG, false)
        set(value) = prefs.edit { putBoolean(KEY_EASTER_EGG, value) }

    var isBackButtonEnabled: Boolean
        get() = prefs.getBoolean(KEY_BTN_BACK, true)
        set(value) = prefs.edit { putBoolean(KEY_BTN_BACK, value) }

    var isAddressInputEnabled: Boolean
        get() = prefs.getBoolean(KEY_ADDRESS_INPUT, true)
        set(value) = prefs.edit { putBoolean(KEY_ADDRESS_INPUT, value) }

    var isCheckNewVersionEnabled: Boolean
        get() = prefs.getBoolean(KEY_CHECK_VERSION, true)
        set(value) = prefs.edit { putBoolean(KEY_CHECK_VERSION, value) }

    var isOpenLastDeviceEnabled: Boolean
        get() = prefs.getBoolean(KEY_OPEN_LAST_DEVICE, true)
        set(value) = prefs.edit { putBoolean(KEY_OPEN_LAST_DEVICE, value) }

    var isSolidColorIconEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOLID_COLOR_ICON, false)
        set(value) = prefs.edit { putBoolean(KEY_SOLID_COLOR_ICON, value) }

    var isLabelEnabled: Boolean
        get() = prefs.getBoolean(KEY_LABEL, true)
        set(value) = prefs.edit { putBoolean(KEY_LABEL, value) }

    var isIconInLabelEnabled: Boolean
        get() = prefs.getBoolean(KEY_ICON_IN_LABEL, true)
        set(value) = prefs.edit { putBoolean(KEY_ICON_IN_LABEL, value) }

    var appTheme: String
        get() = prefs.getString(KEY_THEME, "default") ?: "default"
        set(value) = prefs.edit { putString(KEY_THEME, value) }

    var iconColor: String
        get() = prefs.getString(KEY_ICON_COLOR, DEFAULT_ICON_COLOR) ?: DEFAULT_ICON_COLOR
        set(value) = prefs.edit { putString(KEY_ICON_COLOR, value) }

    var themeWasChanged: Boolean
        get() = prefs.getBoolean(KEY_THEME_WAS_CHANGED, false)
        set(value) = prefs.edit { putBoolean(KEY_THEME_WAS_CHANGED, value) }

    var openPanel: Boolean
        get() = prefs.getBoolean(KEY_OPEN_PANEL, false)
        set(value) = prefs.edit { putBoolean(KEY_OPEN_PANEL, value) }

    /**
     * Сохраняет и загружает объект Device, который нужно открыть.
     * Используется для передачи устройства между активностями
     * без использования статических полей в классе Application.
     * Возвращает null, если устройство для открытия не установлено.
     */
    var deviceForOpen: Device?
        get() {
            //  строка из shared
            val jsonString = prefs.getString(KEY_OPEN_DEVICE, null)
            return jsonString?.let {
                try {
                    // строка обратно в объект Device
                    Device.fromJsonObject(JSONObject(it))
                } catch (e: Exception) {
                    // ошибка
                    null
                }
            }
        }
        set(value) {
            if (value != null) {
                // объект в JSONObject, а затем в строку
                val jsonString = value.toJsonObject().toString()
                prefs.edit { putString(KEY_OPEN_DEVICE, jsonString) }
            } else {
                // передали null, удаляем ключ
                prefs.edit { remove(KEY_OPEN_DEVICE) }
            }
        }

    // Настройки сети и сканирования
    var isSplitNetworkEnabled: Boolean
        get() = prefs.getBoolean(KEY_SPLIT_NETWORK, false)
        set(value) = prefs.edit { putBoolean(KEY_SPLIT_NETWORK, value) }

    var isSplitNetworkManualEnabled: Boolean
        get() = prefs.getBoolean(KEY_SPLIT_NETWORK_MANUAL, false)
        set(value) = prefs.edit { putBoolean(KEY_SPLIT_NETWORK_MANUAL, value) }

    var isSettingsScanEnabled: Boolean
        get() = prefs.getBoolean(KEY_SETTINGS_CHECK, true)
        set(value) = prefs.edit { putBoolean(KEY_SETTINGS_CHECK, value) }

    var isWledScanEnabled: Boolean
        get() = prefs.getBoolean(KEY_WLED_CHECK, true)
        set(value) = prefs.edit { putBoolean(KEY_WLED_CHECK, value) }

    /**
     * пинговать устройства
     */
    var isPingDevicesEnabled: Boolean
        get() = prefs.getBoolean(KEY_CHECK_ACTIVE, true)
        set(value) = prefs.edit { putBoolean(KEY_CHECK_ACTIVE, value) }



    var isHaScanEnabled: Boolean
        get() = prefs.getBoolean(KEY_HA_CHECK, true)
        set(value) = prefs.edit { putBoolean(KEY_HA_CHECK, value) }

    var homeAssistantPort: String
        get() = prefs.getString(KEY_HA_PORT, "8123") ?: "8123"
        set(value) = prefs.edit { putString(KEY_HA_PORT, value) }

    var isSearchUiEnabled: Boolean
        get() = prefs.getBoolean(KEY_SEARCH_UI_CHECK, true)
        set(value) = prefs.edit { putBoolean(KEY_SEARCH_UI_CHECK, value) }

    var timeout: String
        get() = prefs.getString(KEY_TIMEOUT, "1800") ?: "1800"
        set(value) = prefs.edit { putString(KEY_TIMEOUT, value) }

    var ports: String
        get() = prefs.getString(KEY_PORTS, "80, 443, 8080, 81, 8888") ?: "80, 443, 8080, 81, 8888"
        set(value) = prefs.edit { putString(KEY_PORTS, value) }

    var subnetSpinnerPosition: Int
        get() = prefs.getInt(KEY_SUBNET_POS, 0)
        set(value) = prefs.edit { putInt(KEY_SUBNET_POS, value) }

    var currentSubnet: String?
        get() = prefs.getString(KEY_CURRENT_SUBNET, null)
        set(value) = prefs.edit { putString(KEY_CURRENT_SUBNET, value) }

    var isDontShowLocation: Boolean
        get() = prefs.getBoolean(KEY_DONT_SHOW_LOCATION_DIALOG, false)
        set(value) = prefs.edit { putBoolean(KEY_DONT_SHOW_LOCATION_DIALOG, value) }


    /**
     * Принимает и возвращает ID последней сети
     */
    var lastNetwork: String?
        get() = prefs.getString(KEY_LAST_NETWORK_ID, null)
        set(value) = prefs.edit { putString(KEY_LAST_NETWORK_ID, value) }
    /**
     * Принимает и возвращает fingerprint последней сети
     */
    var lastNetworkFingerprint: String?
        get() = prefs.getString(KEY_LAST_NETWORK_FINGERPRINT, null)
        set(value) = prefs.edit { putString(KEY_LAST_NETWORK_FINGERPRINT, value) }

    

    var customSubnets: Set<String>
        get() = prefs.getStringSet(KEY_CUSTOM_SUBNETS, emptySet()) ?: emptySet()
        set(value) = prefs.edit { putStringSet(KEY_CUSTOM_SUBNETS, value) }
}