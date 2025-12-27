package dev.vanila.rsm

import android.content.Context
import android.util.Log

import org.json.JSONArray
import java.io.File
import java.security.MessageDigest

/**
 * Управляет сохранением и загрузкой списков устройств.
 */
class DeviceManager(private val context: Context) {

    companion object {
        // Константа для имени файла, когда разделение сетей выключено.
        // Устройства из этой группы попадают в "Неизвестную сеть".
        //  const val UNKNOWN_NETWORK_FINGERPRINT = "default"
    }

    /**
     * Преобразует fingerprint в MD5.
     */
    fun getSafeFileName(fingerprint: String?): String {
        Log.v("DeviceManager", "getSafeFileName для сети: $fingerprint")

        val effectiveFingerprint = fingerprint ?: App.DEFAULT_NETWORK_FINGERPRINT
        // Преобразуем fingerprint в MD5 хеш
        val md5 = MessageDigest.getInstance("MD5")
            .digest(effectiveFingerprint.toByteArray())
            .joinToString("") { "%02x".format(it) } // Преобразуем байты в hex-строку

        return "devices_$md5.json"
    }

    /**
     * Загружает список устройств для указанной сети.
     * @param fingerprint "Отпечаток" сети. Если null, используется App.DEFAULT_NETWORK_FINGERPRINT.
     * @return MutableList<Device> Список устройств.
     */
    fun loadDevices(fingerprint: String?): MutableList<Device> {

        val fileName = getSafeFileName(fingerprint)
        Log.v("DeviceManager", "ЗАГРУЗКА: Попытка чтения из файла: $fileName")
        val file = File(context.filesDir, fileName)
        val devices = mutableListOf<Device>()

        if (file.exists()) {
            try {
                val jsonStr = file.readText()
                val jsonArray = JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    devices.add(Device.fromJsonObject(jsonArray.getJSONObject(i)))
                }
            } catch (e: Exception) {
                e.printStackTrace() // Логируем ошибку
            }
        } else {
            Log.e("DeviceManager", "ЗАГРУЗКА: Файл: $fileName не найден!")
        }
        return devices
    }

    /**
     * Сохраняет список устройств для указанной сети.
     * @param devices Список устройств для сохранения.
     * @param fingerprint "Отпечаток" сети. Если null, используется App.DEFAULT_NETWORK_FINGERPRINT.
     */
    fun saveDevices(devices: List<Device>, fingerprint: String?) {
        //val effectiveFingerprint = fingerprint ?: App.DEFAULT_NETWORK_FINGERPRINT
        //val fileName = "devices_$effectiveFingerprint.json"
        val fileName = getSafeFileName(fingerprint)
        val file = File(context.filesDir, fileName)
        val jsonArray = JSONArray()

        Log.v("DeviceManager", "СОХРАНЕНИЕ: ${devices.size} устройств в файл: $fileName")

        devices.forEach { device ->
            jsonArray.put(device.toJsonObject())
        }

        try {
            file.writeText(jsonArray.toString(2)) // Сохраняем с форматированием
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Сохраняет одно устройство для указанной сети, добавляя его к существующим.
     * @param device устройство для сохранения.
     * @param fingerprint "Отпечаток" сети.
     */
    fun saveDevice(device: Device, fingerprint: String) {
        Log.v("DeviceManager", "ДОБАВЛЕНО Устройство ${device.name} в сеть $fingerprint")

        // Загружаем текущий список устройств для этой сети
        val devices = loadDevices(fingerprint)

        // Добавляем новое устройство в список
        devices.add(device)

        // Сохраняем весь обновленный список обратно.
        saveDevices(devices.distinctBy { it.url }, fingerprint)
    }

    /**
     * Загружает устройства из ВСЕХ сетей удаляя дубликаты по урл.
     * @return MutableList<Device> Объединенный список уникальных устройств.
     */
    fun loadAllDevices(): MutableList<Device> {
        Log.v("DeviceManager", "ЗАГРУЗКА: Загружаем ВСЕ устройства без дубликатов по урл.")
        val allDevices = mutableListOf<Device>()
        // Находим все файлы с устройствами
        val files =
            context.filesDir.listFiles { _, name -> name.startsWith("devices_") && name.endsWith(".json") }

        files?.forEach { file ->
            // Напрямую читаем и парсим КАЖДЫЙ найденный файл
            try {
                val jsonStr = file.readText()
                val jsonArray = JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    allDevices.add(Device.fromJsonObject(jsonArray.getJSONObject(i)))
                }
            } catch (e: Exception) {
                Log.e(
                    "DeviceManager",
                    "Ошибка чтения файла ${file.name} при загрузке всех устройств",
                    e
                )
            }
        }

        // Возвращаем список с удаленными дубликатами (по URL)
        return allDevices.distinctBy { it.url }.toMutableList()
    }

    /**
     * Загружает устройства из ВСЕХ сетей не проверяя на дубликаты.
     * @return MutableList<Device> Объединенный список устройств.
     */
    fun loadAllDevicesWithDuplicates(): MutableList<Device> {
        Log.v("DeviceManager", "ЗАГРУЗКА: Загружаем все устройства с дубликатами.")
        val allDevices = mutableListOf<Device>()
        // Находим все файлы с устройствами
        val files =
            context.filesDir.listFiles { _, name -> name.startsWith("devices_") && name.endsWith(".json") }

        files?.forEach { file ->
            // читаем и парсим найденные файлы
            try {
                val jsonStr = file.readText()
                val jsonArray = JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    allDevices.add(Device.fromJsonObject(jsonArray.getJSONObject(i)))
                }
            } catch (e: Exception) {
                Log.e(
                    "DeviceManager",
                    "Ошибка чтения файла ${file.name} при загрузке всех устройств с дубликатами",
                    e
                )
            }
        }

        Log.v("DeviceManager", "ЗАГРУЗИЛИ: ${allDevices.size} устройств.")
        return allDevices
    }

    // Возвращает Map, где ключ - это сеть (или null для "Неизвестной"), а значение - список устройств.
    fun loadGroupedDevices(): Map<KnownNetwork?, List<Device>> {
        val groupedDevices = mutableMapOf<KnownNetwork?, MutableList<Device>>()
        val allKnownNetworks = NetworkManager.getKnownNetworks(context)

        //  Загружаем устройства для каждой известной сети
        for (network in allKnownNetworks) {
            val devicesForNetwork = loadDevices(network.fingerprint)
            if (devicesForNetwork.isNotEmpty()) {
                groupedDevices.getOrPut(network) { mutableListOf() }.addAll(devicesForNetwork)
            }
        }

        //  Загружаем устройства без сети ("Неизвестная сеть")
        // val unknownDevices = loadDevices(null)
        // if (unknownDevices.isNotEmpty()) {
        //     groupedDevices[null] = unknownDevices.toMutableList()
        // }

        return groupedDevices
    }

    /**
     * Возвращает общее количество устройств во всех сетях.
     * @return Int - количество устройств.
     */
    fun getTotalDeviceCount(): Int {
        return loadAllDevicesWithDuplicates().size
    }

    /**
     * Возвращает количество устройств для КОНКРЕТНОЙ сети.
     * @param fingerprint "Отпечаток" сети. Если null, вернет количество для DEFAULT_NETWORK_FINGERPRINT.
     * @return Int - количество устройств в указанной сети.
     */
    fun getDeviceCountForNetwork(fingerprint: String?): Int {

        return loadDevices(fingerprint).size
    }


    /**
     * Обновляет имя сети для всех устройств, принадлежащих указанной сети.
     *     * @param fingerprint "Отпечаток" сети, для которой нужно обновить имя.
     * @param newNetworkName Новое имя сети, которое будет установлено устройствам.
     */
    fun renameNetworkDevices(fingerprint: String?, newNetworkName: String) {
        if (fingerprint == null) {
            Log.e("DeviceManager", "renameNetworkForDevices: Попытка переименовать сеть с null отпечатком. Операция отменена.")
            return
        }
        if (fingerprint == App.ALL_NETWORKS_FINGERPRINT) {
            Log.e("DeviceManager", "renameNetworkForDevices: Попытка переименовать сеть с ${App.ALL_NETWORKS_FINGERPRINT} отпечатком. Операция отменена.")
            return
        }

        Log.v("DeviceManager", "Переименование сети с отпечатком '$fingerprint' в '$newNetworkName'.")


        val devicesToUpdate = loadDevices(fingerprint)


        if (devicesToUpdate.isEmpty()) {
            Log.v("DeviceManager", "renameNetworkForDevices: В сети '$fingerprint' нет устройств для переименования.")
            return
        }


        val updatedDevices = devicesToUpdate.map { device ->
            device.copy(networkName = newNetworkName)
        }

        saveDevices(updatedDevices, fingerprint)

        Log.v("DeviceManager", "renameNetworkForDevices: Имя сети для ${updatedDevices.size} устройств было обновлено на '$newNetworkName'.")
    }



    /**
     * Перемещает все устройства из одной сети в другую.
     * После успешного перемещения список устройств исходной сети будет удален.
     *
     * @param sourceFingerprint "Отпечаток" сети, ИЗ которой перемещаются устройства.
     * @param targetFingerprint "Отпечаток" сети, В которую перемещаются устройства.
     * @return true, если перемещение прошло успешно, false в ином случае.
     */
    fun moveDevices(sourceFingerprint: String?, targetFingerprint: String?): Boolean {
        // Нельзя переместить сеть в саму себя.
        if (sourceFingerprint == targetFingerprint) {
            Log.e(
                "DeviceManager",
                "moveDevices: Попытка переместить устройства в ту же самую сеть. Операция отменена."
            )
            return false
        }

        Log.v(
            "DeviceManager",
            "Начало перемещения устройств из сети '$sourceFingerprint' в сеть '$targetFingerprint'."
        )

        //  Загружаем все устройства из исходной сети.
        val devicesToMove = loadDevices(sourceFingerprint)

        // Если в исходной сети нет устройств, делать нечего.
        if (devicesToMove.isEmpty()) {
            Log.v(
                "DeviceManager",
                "moveDevices: В исходной сети '$sourceFingerprint' нет устройств для перемещения. Операция завершена."
            )
            // Считаем это успешным завершением, так как цель достигнута.
            // Можно также удалить пустой файл, если он существует.
            clearDevices(sourceFingerprint)
            return true
        }

        //  Загружаем устройства, которые уже есть в целевой сети.
        val targetDevices = loadDevices(targetFingerprint)

        //  Объединяем списки.
        // Добавляем устройства к целевому списку.
        targetDevices.addAll(devicesToMove)

        // Удаляем дубликаты по URL, чтобы избежать повторений.
        // Устройства из targetDevices будут иметь приоритет, если найдутся дубликаты.
        val mergedUniqueDevices = targetDevices.distinctBy { it.url }

        // Сохраняем объединенный и очищенный список в целевую сеть.
        saveDevices(mergedUniqueDevices, targetFingerprint)
        Log.v(
            "DeviceManager",
            "moveDevices: ${devicesToMove.size} устройств было добавлено в сеть '$targetFingerprint'."
        )

        // Удаляем файл с устройствами из исходной сети.
        clearDevices(sourceFingerprint)
        Log.v(
            "DeviceManager",
            "moveDevices: Список устройств для исходной сети '$sourceFingerprint' был удален."
        )

        return true
    }

    /**
     * Удаляет все устройства для указанной сети.
     * @param fingerprint "Отпечаток" сети. Если null, удаляются устройства "Сети по умолчанию".
     */
    fun clearDevices(fingerprint: String?) {
        //val effectiveFingerprint = fingerprint ?: App.DEFAULT_NETWORK_FINGERPRINT
        // val fileName = "devices_$effectiveFingerprint.json"

        val fileName = getSafeFileName(fingerprint)
        File(context.filesDir, fileName).delete()
    }

    /**
     * Полностью очищает все сохраненные списки устройств.
     */
    fun clearAllDeviceLists() {
        val files =
            context.filesDir.listFiles { _, name -> name.startsWith("devices_") && name.endsWith(".json") }
        files?.forEach { it.delete() }
    }
}