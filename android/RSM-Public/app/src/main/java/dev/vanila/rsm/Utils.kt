package dev.vanila.rsm

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.wifi.ScanResult
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.red
import androidx.core.graphics.toColorInt
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.net.URL

/**
 * Конвертирует значение в dp в пиксели.
 */
fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

@Deprecated(
    "СканРезульт попал в немилость, выпилить!"
)
private fun ScanResult.getSsidString(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && this.wifiSsid != null) {
        // Современный способ для Android 13+
        this.wifiSsid.toString().removeSurrounding("\"")
    } else {
        // Устаревший, но рабочий способ для старых версий
        @Suppress("DEPRECATION")
        this.SSID
    }
}


/**
 * Показывает диалог с просьбой включить геолокацию.
 * @param activity Активность, из которой вызывается диалог.
 * @param locationSettingsLauncher Лаунчер для открытия настроек
 * @param onStateChanged Лямбда для обратной связи об изменении настроек в диалоге.
 */
fun showLocationDialog(
    activity: Activity,
    locationSettingsLauncher: ActivityResultLauncher<Intent>,
    onStateChanged: (splitEnabled: Boolean?, manualMode: Boolean?) -> Unit
) {

    if (SettingsManager.isDontShowLocation) {
        Log.e("showLocationDialog", "Геолокации нет, но пользователь запретил сообщать об этом!")
        onStateChanged(null, null)
        return
    } else Log.v("showLocationDialog", "Геолокации нет, сообщаем об этом!")

    val message = """Включен автоматический режим разделения устройств по сетям.<br>
               Для определения Wi-Fi сети необходимо включить геолокацию в настройках системы.<br><br>
               ℹ️ <small><i><b>RSM</b> не собирает и не использует данные о вашем местоположении. Геолокация нужна только для определения имени и параметров WiFi сети.</i></small>
               """.trimIndent()

    val checkBoxLayout = LinearLayout(activity).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        val horizontalPadding = 24.dpToPx()
        val verticalPadding = 12.dpToPx()
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
    }

    val checkBoxDontShow = CheckBox(activity).apply {
        isChecked = SettingsManager.isDontShowLocation
        setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.isDontShowLocation = isChecked
            Log.d("CheckBox", "Состояние 'Больше не показывать' изменено на: $isChecked")
        }
    }

    val checkBoxLabel = TextView(activity).apply {
        text = "Больше не показывать это окно"
        setOnClickListener {
            checkBoxDontShow.toggle()
        }
    }
    checkBoxLayout.addView(checkBoxDontShow)
    checkBoxLayout.addView(checkBoxLabel)

    AlertDialog.Builder(activity, R.style.AppTheme_RoundedAlertDialogErr)
        .setTitle("Включите геолокацию!")
        .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
        .setView(checkBoxLayout)
        .setPositiveButton("Включить") { _, _ ->
            onStateChanged(true, false)

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            locationSettingsLauncher.launch(intent)
        }
        .setNegativeButton("Отключить разделение сетей") { _, _ ->
            SettingsManager.isSplitNetworkEnabled = false
            Toast.makeText(activity, "Режим разделения сетей отключен.", Toast.LENGTH_LONG).show()
            onStateChanged(false, false) // Сообщаем об изменении
            ActivityCompat.recreate(activity) // Пересоздаем активность
        }
        .setNeutralButton("Определять сеть вручную") { _, _ ->
            SettingsManager.isSplitNetworkEnabled = true
            SettingsManager.isSplitNetworkManualEnabled = true
            Toast.makeText(
                activity,
                "Автоматическое определение сетей - отключено.",
                Toast.LENGTH_LONG
            ).show()
            onStateChanged(true, true) // Сообщаем об изменении
            ActivityCompat.recreate(activity) // Пересоздаем активность
        }.setOnDismissListener {
            // Этот блок сработает, если пользователь нажал "Назад" или тапнул вне диалога.
            // Также вызываем коллбэк!
            Log.d(
                "showLocationDialog",
                "Диалог геолокации закрыт (dismiss), вызываем onStateChanged."
            )
            onStateChanged(null, null) //
        }
        .show()
}


/**
 * Диалог с запросом разрешений.
 * @param activity Активность, из которой вызывается диалог.
 * @param permissionsLauncher Лаунчер для запроса разрешений
 * @param permissions Список разрешений для запроса.
 */
fun showPermissionsDialog(
    activity: Activity,
    permissionsLauncher: ActivityResultLauncher<Array<String>>,
    permissions: Array<String>,
    onStateChanged: (splitEnabled: Boolean?, manualMode: Boolean?) -> Unit
) {
    val message = "Вы включили режим разделения устройств по сетям.<br>" +
            "Для определения имени Wi-Fi сети приложению нужно разрешение на доступ к геолокации.<br><br>" +
            "ℹ\uFE0F <small><i><b>RSM</b> не собирает и не использует данные о вашем местоположении. " +
            "Геолокация нужна только для определения имени и параметров WiFi сети.</i></small>"

    AlertDialog.Builder(activity, R.style.AppTheme_RoundedAlertDialogErr)
        .setTitle("Требуются разрешения")
        .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
        .setPositiveButton("Продолжить") { _, _ ->
            // Запускаем системный запрос разрешений
            permissionsLauncher.launch(permissions)
        }
        .setNegativeButton("Отключить разделение сетей") { _, _ ->
            SettingsManager.isSplitNetworkEnabled = false
            Toast.makeText(activity, "Режим разделения сетей отключен.", Toast.LENGTH_LONG).show()
            ActivityCompat.recreate(activity)
        }
        .setNeutralButton("Определять сеть вручную") { _, _ ->
            SettingsManager.isSplitNetworkEnabled = true
            SettingsManager.isSplitNetworkManualEnabled = true
            Toast.makeText(activity, "Определение сетей - отключено.", Toast.LENGTH_LONG).show()
            ActivityCompat.recreate(activity)
        }
        .setOnDismissListener {
            onStateChanged(null, null)
            Log.d(
                "showLocationDialog",
                "Диалог разрешений закрыт (dismiss), вызываем onStateChanged."
            )
        }
        .show()
}




/**
 * Показывает диалог для конфигурации и сохранения новой сети.
 * @param context Контекст для создания диалога.
 *  * @param ssid Изначальное имя сети.
 *  * @param bssidPrefix
 *  * @param subnet * @param onSave Лямбда-функция
 */
fun showTrustNetworkDialog(
    context: Context,
    ssid: String,
    bssidPrefix: String,
    subnet: String?,
    onSave: (ssid: String, newNetworkName: String, trust: Boolean, newSubnet: String?) -> Unit,
    onDismiss: () -> Unit
) {
//    --- Подготовка ---
    val isCellular =
        bssidPrefix == FingerprintGenerator.extractBssidPrefix(App.CELLULAR_NETWORKS_FINGERPRINT)
    val defaultName = if (isCellular) "Сотовая сеть" else ssid

//    вертикальный контейнер
    val dialogLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        val padding = 20.dpToPx()
        setPadding(padding, padding, padding, padding)
    }

// --- имея сети ---
    val nameRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
    }
    val nameLabel = TextView(context).apply {
        text = "Имя сети:"
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginEnd = 8.dpToPx() }
    }
    val nameInput = EditText(context).apply {
        setText(defaultName)
        hint = "Название сети"
        layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f // Занимает все оставшееся место
        )
    }
    nameRow.addView(nameLabel)
    nameRow
        .addView(nameInput)// --- Строка для подсети ---
    val subnetRow = LinearLayout(context).apply   {
        orientation = LinearLayout.HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        // Отступ сверху
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = 16.dpToPx() }
    }
    val subnetLabel = TextView(context).apply {
        text = "Подсеть:"
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginEnd = 8.dpToPx() }
    }
    val subnetInput = EditText(context).apply {
        setText(subnet ?: "")
        hint = "192.168.1.0/24"
        layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f // Занимает все оставшееся место

        )
    }
    subnetRow.addView(subnetLabel)
    subnetRow.addView(subnetInput)



    // --- Чекбокс для доверия ---
    val trustCheckbox = CheckBox(context).apply {
        text = "Не проверять https сертификаты"
        isChecked = false


        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = 16.dpToPx() }
    }

    val warningText = TextView(context).apply {
        text = "⚠️ Потенциально не безопасная настройка!"
        setTextColor("#ff0000".toColorInt())
        visibility = View.GONE
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 4.dpToPx()
            marginStart = 8.dpToPx()
        }
    }



    trustCheckbox.setOnCheckedChangeListener { _, isChecked ->
        // Меняем видимость текста в зависимости от состояния чекбокса
        warningText.visibility = if (isChecked) View.VISIBLE else View.GONE
    }


    // --- Сборка Layout ---
    dialogLayout.addView(nameRow)
    dialogLayout.addView(subnetRow)
    dialogLayout.addView(trustCheckbox)
    dialogLayout.addView(warningText)


    // --- диалог ---
    AlertDialog.Builder(context, R.style.AppTheme_RoundedAlertDialog)
        .setTitle("Новая сеть")
        .setView(dialogLayout)
        .setCancelable(false)
        .setPositiveButton("Сохранить") { _, _ ->
            val newName = nameInput.text.toString().trim()
            val newSubnet =
                subnetInput.text.toString().trim().let { it.ifEmpty { null } }
            val shouldTrust = trustCheckbox.isChecked

            if (newName.isNotEmpty()) {
                onSave(ssid, newName, shouldTrust, newSubnet) // Возвращаем результат
            } else {
                Toast.makeText(context, "Имя сети не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        }
        .setNegativeButton("Отмена", null) // Просто закрывает диалог
        .setOnDismissListener {
            onDismiss()
        }
        .show()
}







/**
 * Показывает диалог для добавления новой сети вручную.
 * @param onNetworkCreated Лямбда-функция, которая будет вызвана после успешного создания сети.
 */
fun showAddNetworkDialog(context: Context, onNetworkCreated: (KnownNetwork) -> Unit) {

    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_network, null)

    val dialog = Dialog(context).apply {
        setTitle("Добавить сеть вручную")
        setContentView(dialogView)
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    // Элементы UI
    val etNetworkName = dialogView.findViewById<EditText>(R.id.et_network_name)
    val layoutNetworkName = dialogView.findViewById<TextInputLayout>(R.id.layout_network_name)
    val etNetworkSsid = dialogView.findViewById<EditText>(R.id.et_network_ssid)
    val layoutNetworkSsid = dialogView.findViewById<TextInputLayout>(R.id.layout_network_ssid)
    val cbIsTrusted = dialogView.findViewById<CheckBox>(R.id.cb_is_trusted)
    val etSubnet = dialogView.findViewById<EditText>(R.id.et_subnet)
    val layoutSubnet = dialogView.findViewById<TextInputLayout>(R.id.layout_subnet)

    val btnCreate = dialogView.findViewById<MaterialButton>(R.id.btn_create_network)
    val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btn_cancel)
    val btnAutoDetect = dialogView.findViewById<MaterialButton>(R.id.btn_auto_detect_subnet)

    // Отмена
    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    // Автоопределение подсети (временно отключено, но структура сохранена)
    btnAutoDetect.setOnClickListener {

        Toast.makeText(context, "Автоопределение временно недоступно", Toast.LENGTH_LONG).show()
    }

    // Создание сети
// Создание сети
    btnCreate.setOnClickListener {
        val name = etNetworkName.text.toString().trim()
        val ssid = etNetworkSsid.text.toString().trim()
        val isTrusted = cbIsTrusted.isChecked
        val subnetInput = etSubnet.text.toString().trim()
        val nameNetwork = etNetworkName.text.toString().trim()

        var hasError = false

        //  Проверка имени
        if (name.isBlank()) {
            layoutNetworkName.error = "Имя сети не может быть пустым"
            hasError = true
        } else {
            layoutNetworkName.error = null
        }
        if(ssid.isBlank()){
            layoutNetworkSsid.error = "SSID не может быть пустым"
            hasError = true
        } else {
            layoutNetworkSsid.error = null
        }

        //  Проверка подсети
        if (subnetInput.isBlank()) {
            layoutSubnet.error = "Подсеть не может быть пустой"
            hasError = true
        } else if (!isValidSubnet(subnetInput)) {
            layoutSubnet.error = "Неправильный формат (пример: 192.168.1.0/24)"
            hasError = true
        } else {
            layoutSubnet.error = null
        }

        if (hasError) {
            return@setOnClickListener
        }

        // === ПРОВЕРКА НА ДУБЛИКАТ ===
        val existingNetworks = NetworkManager.getKnownNetworks(context)
        val duplicateNetwork = existingNetworks.find { known ->
            known.ssid == ssid && known.subnet == subnetInput
        }

        if (duplicateNetwork != null) {
            // Сеть с таким же именем и подсетью уже существует
            AlertDialog.Builder(context, R.style.AppTheme_RoundedAlertDialog)
                .setTitle("Сеть уже существует")
                .setMessage("Сеть \"${duplicateNetwork.name}\" с подсетью ${duplicateNetwork.subnet} уже добавлена.\n\nДобавить как отдельную сеть или отменить?")
                .setPositiveButton("Добавить отдельно") { _, _ ->
                    // Принудительно добавляем (с новым случайным BSSID)
                    val newNetwork = NetworkManager.addManualNetwork(
                        context = context,
                        ssid = ssid,
                        name = name,
                        subnet = subnetInput,
                        isTrusted = isTrusted
                    )
                    Toast.makeText(context, "Сеть '${newNetwork.name}' добавлена как отдельная", Toast.LENGTH_SHORT).show()
                    onNetworkCreated(newNetwork)
                    dialog.dismiss()
                }
                .setNegativeButton("Отмена", null)
                .setNeutralButton("Объединить (добавить точку)") { _, _ ->
                    // Находим сеть с таким же именем и подсетью
                    val existingNetwork = NetworkManager.getKnownNetworks(context)
                        .find { it.ssid == name && it.subnet == subnetInput }

                    if (existingNetwork != null) {
                        // Генерируем случайный BSSID (как в addManualNetwork)
                        val randomBssid = (1..6).joinToString(":") {
                            (0..255).random().toString(16).padStart(2, '0')
                        }
                        val newBssidPrefix = randomBssid.split(':').take(5).joinToString(":")

                        // Добавляем его как дополнительный
                        val added = NetworkManager.addBssidToNetwork(context, existingNetwork.id, newBssidPrefix)

                        if (added) {
                            Toast.makeText(
                                context,
                                "Точка доступа добавлена в сеть \"${existingNetwork.name}\"",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(context, "Этот BSSID уже есть в сети", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Это не должно происходить, но на всякий случай
                        Toast.makeText(context, "Сеть не найдена", Toast.LENGTH_SHORT).show()
                    }

                    dialog.dismiss()
                }
                .show()
            return@setOnClickListener
        }

        // Если дубликата нет — создаём как обычно
        val newNetwork = NetworkManager.addManualNetwork(
            context = context,
            ssid = ssid,
            name = name,
            subnet = subnetInput,
            isTrusted = isTrusted
        )

        Toast.makeText(context, "Сеть '${newNetwork.name}' добавлена", Toast.LENGTH_SHORT).show()
        onNetworkCreated(newNetwork)
        dialog.dismiss()
    }
    dialog.show()
}
private fun isValidSubnet(subnet: String): Boolean {
    val regex = Regex("""^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})/(\d{1,2})$""")
    if (!regex.matches(subnet)) return false

    val parts = subnet.split("/")
    val ip = parts[0]
    val prefix = parts[1].toIntOrNull() ?: return false

    if (prefix !in 8..30) return false  // Разумные границы для домашней сети

    val octets = ip.split(".").map { it.toIntOrNull() ?: return false }
    return octets.all { it in 0..255 } && octets[0] != 0  // Не 0.0.0.0
}





fun showAddDeviceDialog(context: Context, currentNetworkFingerprint: String?, allDevices: List<Device>, currentUrl: String?, onDeviceCreated: (newDevice: Device) -> Unit) {


    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_device, null)
    val editName = dialogView.findViewById<EditText>(R.id.edit_manual_name)
    val editUrl = dialogView.findViewById<EditText>(R.id.edit_manual_url)


    if (currentUrl != null && currentUrl.isNotEmpty() && currentUrl != "about:blank") {
        editUrl.setText(currentUrl)

        // Попробуем извлечь имя устройства из URL
        try {
            val url = URL(currentUrl)
            var host = url.host

            // 1. Убираем "www." если оно есть
            if (host.startsWith("www.")) {
                host = host.substring(4)
            }

            // 2. Находим индекс первой точки
            val firstDotIndex = host.indexOf('.')
            if (firstDotIndex != -1) {
                // 3. Если точка найдена, обрезаем хост до нее
                host = host.take(firstDotIndex)
            }

            // 4. Устанавливаем имя с заглавной первой буквой
            editName.setText(host.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })

        } catch (e: Exception) {
            // Если не удалось разобрать URL, оставляем имя пустым

        }
    }
    var nameNetwork : String? = null
    var message: String
    if(currentNetworkFingerprint != null) {
        val network = NetworkManager.findNetworkByFingerprint(context, currentNetworkFingerprint)
        nameNetwork = network?.name
    }
    message = if(nameNetwork != null && nameNetwork.isNotEmpty() && SettingsManager.isSplitNetworkEnabled) {
        """<br>В сеть: <b>$nameNetwork</b>
               """.trimIndent()
    }
    else {
        ""
    }


    AlertDialog.Builder(context, R.style.AppTheme_RoundedAlertDialog)
        .setTitle("Добавить устройство")
        .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
        .setView(dialogView)
        .setPositiveButton("Добавить") { _, _ ->



            val name = editName.text.toString().trim()
            val addressInput = editUrl.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(context, "Введите имя", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            if (addressInput.isEmpty()) {
                Toast.makeText(context, "Введите адрес", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Нормализация URL
            val normalizedUrl = normalizeUrl(addressInput)

            // Проверяем, нет ли уже такого устройства
            if (allDevices.any { it.url == normalizedUrl }) {
                Toast.makeText(context, "Устройство уже есть", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }


            val curreentNetwork = currentNetworkFingerprint?.let {
                NetworkManager.findNetworkByFingerprint(context, it)
            }
            val newDevice = Device(name, normalizedUrl, null, false, curreentNetwork?.name, curreentNetwork?.id, false)

            onDeviceCreated(newDevice)
            return@setPositiveButton

        }
        .setNegativeButton("Отмена", null)
        .show()
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

