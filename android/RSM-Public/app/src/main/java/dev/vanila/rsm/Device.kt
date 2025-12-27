package dev.vanila.rsm

import org.json.JSONObject

/**
 * устройство
 */
data class Device(
    val name: String,
    val url: String,
    val faviconPath: String?,
    val isBuiltinIcon: Boolean = false,
    val networkName: String?,
    val networkId: String?,
    // Эти поля не сохраняются в JSON, они используются (неиспользу во время выполнения
    @Transient var isActive: Boolean = false
) {
    /**
     * Преобразует объект Device в JSONObject для сохранения.
     */
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("name", name)
            put("url", url)
            put("faviconPath", faviconPath ?: JSONObject.NULL)
            put("isBuiltinIcon", isBuiltinIcon)
            put("networkName", networkName ?: JSONObject.NULL)
            put("networkId", networkId ?: JSONObject.NULL)
        }
    }

    companion object {
        /**
         * Создает объект Device из JSONObject.
         */
        fun fromJsonObject(jsonObj: JSONObject): Device {
            return Device(
                name            = jsonObj.getString("name"),
                url             = jsonObj.getString("url"),
                faviconPath     = jsonObj.optString("faviconPath").takeIf { it != "null" },
                isBuiltinIcon   = jsonObj.optBoolean("isBuiltinIcon", false),
                networkName     = jsonObj.optString("networkName", "без сети"),
                networkId       = jsonObj.optString("networkId", "null")
            )
        }
    }
}