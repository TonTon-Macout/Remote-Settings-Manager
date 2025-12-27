package dev.vanila.rsm

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean

object UpdateChecker {

    private const val TAG = "UpdateChecker"

    // AtomicBoolean гарантирует, что даже в многопоточной среде
    // значение будет меняться корректно.
    private val hasChecked = AtomicBoolean(false)

    // suspend-функция для проверки
    suspend fun check(context: Context) {

        if (hasChecked.compareAndSet(false, true)) {
            Log.d(TAG, "Начало проверки обновлений")
            // Выполняем в фоновом потоке
            withContext(Dispatchers.IO) {
                val currentVersion = BuildConfig.VERSION_NAME
                Log.d(TAG, "Текущая версия приложения: $currentVersion")
                try {
                    Log.d(TAG, "Загрузка информации о релизах с GitHub...")
                    val response = URL("https://api.github.com/repos/TonTon-Macout/Remote-Settings-Manager/releases").readText()
                    Log.d(TAG, "Получен ответ от GitHub API (длина: ${response.length})")

                    val releaseRegex = Regex(""""tag_name":\s*"([^"]*)".*?"assets":\s*\[(.*?)]""", RegexOption.DOT_MATCHES_ALL)

                    var latestVersion = Regex(""""tag_name":\s*"([^"]*)"""").find(response)?.groupValues?.get(1)
                    val releases = releaseRegex.findAll(response)

                    Log.d(TAG, "Найдено релизов: ${releases.count()}")

                    var latestApkVersion: String? = null
                    for (release in releases) {
                        val tagName = release.groupValues[1]  //
                        val assetsJson = release.groupValues[2] // Это кусок JSON с файлами [ {...}, {...} ]

                        Log.d(TAG, "Проверка релиза $tagName, assets: $assetsJson")

                        // Проверяем, есть ли в списке файлов (.assetsJson) упоминание ".apk"
                        if (assetsJson.contains(".apk")) {
                            // Нашли! Это самый свежий релиз с APK (потому что API отдает их от нового к старому)
                            latestApkVersion = tagName
                            Log.d(TAG, "Найден релиз с APK: $latestApkVersion")

                            break
                        }
                    }
                    latestVersion = latestApkVersion

                    //latestVersion = "v2.2.0-test"

                    Log.d(TAG, "Последняя версия с APK: $latestVersion")

                    if (latestVersion != null && isNewerVersion(currentVersion, latestVersion)) {
                        Log.d(TAG, "Доступно обновление! Показываем диалог")
                        // Важно! Для показа UI переключаемся на главный поток
                        withContext(Dispatchers.Main) {
                            if (context is MainActivity) {
                                context.checkPermissionAndShowUpdate(latestVersion)
                            }
                        }
                    } else {
                        Log.d(TAG, "Обновление не требуется")
                    }


                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при проверке обновлений", e)

                }
            }
        } else {
            Log.d(TAG, "Проверка обновлений уже выполнялась")
        }
    }



    private fun isNewerVersion(currentVersion: String, latestVersion: String): Boolean {
        // Убираем префиксы "v" для корректного сравнения
        val cleanCurrent = currentVersion.removePrefix("v").split('.').mapNotNull { it.toIntOrNull() }
        val cleanLatest = latestVersion.removePrefix("v").split('.').mapNotNull { it.toIntOrNull() }

        val maxParts = maxOf(cleanCurrent.size, cleanLatest.size)

        for (i in 0 until maxParts) {
            val currentPart = cleanCurrent.getOrElse(i) { 0 }
            val latestPart = cleanLatest.getOrElse(i) { 0 }

            if (latestPart > currentPart) {
                Log.d(TAG, "Новая версия обнаружена: $latestVersion > $currentVersion")
                return true // Найдена более новая часть версии
            }
            if (latestPart < currentPart) {
                Log.d(TAG, "Серверная версия старее: $latestVersion < $currentVersion")
                return false // Версия на сервере старее
            }
        }
        // Все части равны
        Log.d(TAG, "Версии равны: $currentVersion == $latestVersion")
        return false
    }
}