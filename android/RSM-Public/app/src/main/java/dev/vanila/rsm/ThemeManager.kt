package dev.vanila.rsm

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity



class ThemeManager {
    companion object {
        private const val PREF_THEME = "app_theme"
        const val THEME_DEFAULT = "default"
        const val THEME_MONO = "mono"
        const val THEME_GREEN = "green"
        const val THEME_BLACK = "black"

        const val THEME_ORANGE = "orange"
        const val THEME_RED = "red"
        const val THEME_PALERED = "palered"
        const val THEME_BLUE = "blue"
        const val THEME_BLUELIGHT = "bluelight"
        const val THEME_PURPLE = "purple"
        const val THEME_PURPLEGRADIENT = "purplegradient"
        const val THEME_YELLOW = "yellow"
        const val THEME_YELLOWGREEN = "yellowgreen"
        const val THEME_COLORGRADIENT = "colorgradient"
        const val THEME_COLORS = "colors"
        const val THEME_GRAY = "gray"

        /**
         * Применяет сохраненную тему к активности
         */
        fun applyTheme(activity: Activity) {

            val theme = SettingsManager.appTheme

            when (theme) {

                THEME_GREEN -> activity.setTheme(R.style.Theme_RSM_GREEN)
                THEME_BLACK -> activity.setTheme(R.style.Theme_RSM_BLACK)
                THEME_DEFAULT -> activity.setTheme(R.style.Theme_RSM_DEFAULT)
                THEME_MONO -> activity.setTheme(R.style.Theme_RSM_MONO)
                THEME_ORANGE -> activity.setTheme(R.style.Theme_RSM_ORANGE)
                THEME_RED -> activity.setTheme(R.style.Theme_RSM_RED)
                THEME_PALERED -> activity.setTheme(R.style.Theme_RSM_PALERED)
                THEME_BLUE -> activity.setTheme(R.style.Theme_RSM_BLUE)
                THEME_BLUELIGHT -> activity.setTheme(R.style.Theme_RSM_BLUELIGHT)
                THEME_PURPLE -> activity.setTheme(R.style.Theme_RSM_PURPLE)
                THEME_PURPLEGRADIENT -> activity.setTheme(R.style.Theme_RSM_PURPLEGRADIENT)
                THEME_YELLOW -> activity.setTheme(R.style.Theme_RSM_YELLOW)
                THEME_YELLOWGREEN -> activity.setTheme(R.style.Theme_RSM_YELLOWGREEN)
                THEME_COLORGRADIENT -> activity.setTheme(R.style.Theme_RSM_COLORGRADIENT)
                THEME_COLORS -> activity.setTheme(R.style.Theme_RSM_COLORS)
                THEME_GRAY -> activity.setTheme(R.style.Theme_RSM_GRAY)


                else -> activity.setTheme(R.style.Theme_RSM_DEFAULT)
            }
        }

        /**
         * Применяет тему и немедленно пересоздает активность
         */
        fun applyThemeWithRecreate(activity: AppCompatActivity, themeName: String) {
            setTheme(activity, themeName)
            activity.recreate()
        }

        /**
         * Устанавливает тему в настройках
         */
        fun setTheme(context: Context, themeName: String) {
            SettingsManager.appTheme = themeName
        }

        /**
         * Возвращает текущую тему
         */
        fun getCurrentTheme(context: Context): String {
            return SettingsManager.appTheme
        }

        /**
         * Возвращает читаемое название темы
         */
        fun getThemeDisplayName(context: Context): String {
            return when (getCurrentTheme(context)) {
                THEME_GREEN -> context.getString(R.string.theme_green)
                THEME_MONO -> context.getString(R.string.theme_mono)

                THEME_BLACK -> context.getString(R.string.theme_black)
                THEME_DEFAULT -> context.getString(R.string.theme_default)

                THEME_ORANGE -> context.getString(R.string.theme_orange)
                THEME_RED -> context.getString(R.string.theme_red)
                THEME_PALERED -> context.getString(R.string.theme_palered)
                THEME_BLUE -> context.getString(R.string.theme_blue)
                THEME_BLUELIGHT -> context.getString(R.string.theme_bluelight)
                THEME_PURPLE -> context.getString(R.string.theme_purple)
                THEME_PURPLEGRADIENT -> context.getString(R.string.theme_purplegradient)
                THEME_YELLOW -> context.getString(R.string.theme_yellow)
                THEME_YELLOWGREEN -> context.getString(R.string.theme_yellowgreen)
                THEME_COLORGRADIENT -> context.getString(R.string.theme_colorgradient)
                THEME_COLORS -> context.getString(R.string.theme_colors)
                THEME_GRAY -> context.getString(R.string.theme_gray)

                else -> context.getString(R.string.theme_default)
            }
        }

        /**
         * Возвращает позицию темы в спиннере
         */
        fun getThemeSpinnerPosition(context: Context): Int {
            return when (getCurrentTheme(context)) {
                THEME_DEFAULT -> 0
                THEME_MONO -> 1
                THEME_GREEN -> 2
                THEME_BLACK -> 3
                THEME_ORANGE -> 4
                THEME_RED -> 5
                THEME_PALERED -> 6
                THEME_BLUE -> 7
                THEME_BLUELIGHT -> 8
                THEME_PURPLE -> 9
                THEME_PURPLEGRADIENT -> 10

                THEME_YELLOWGREEN -> 11
                THEME_COLORGRADIENT -> 12
                THEME_COLORS -> 13
                THEME_GRAY -> 14
                THEME_YELLOW -> 15
                else -> 0
            }
        }

        /**
         * Возвращает имя темы по позиции в спиннере
         */
        fun getThemeNameByPosition(position: Int): String {
            return when (position) {
                0 -> THEME_DEFAULT
                1 -> THEME_MONO
                2 -> THEME_GREEN
                3 -> THEME_BLACK
                4 -> THEME_ORANGE
                5 -> THEME_RED
                6 -> THEME_PALERED
                7 -> THEME_BLUE
                8 -> THEME_BLUELIGHT
                9 -> THEME_PURPLE
                10 -> THEME_PURPLEGRADIENT

                11 -> THEME_YELLOWGREEN
                12 -> THEME_COLORGRADIENT
                13 -> THEME_COLORS
                14 -> THEME_GRAY
                15 -> THEME_YELLOW
                else -> THEME_DEFAULT
            }
        }

        /**
         * Проверяет, является ли тема темной
         */
        fun isDarkTheme(context: Context): Boolean {
            return getCurrentTheme(context) == THEME_BLACK
        }

        /**
         * Сбрасывает тему к значению по умолчанию
         */
        fun resetToDefaultTheme(context: Context) {
            setTheme(context, THEME_DEFAULT)
        }
    }
}