package dev.vanila.rsm

import android.util.Log
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {


    private var wasFirstRunInThisSession = false

    init {
        if (SettingsManager.isFirstRun()) {
            wasFirstRunInThisSession = true
            Log.v("MainViewModel", "Обнаружен первый запуск приложения в этой сессии.")
        }
    }

    /**
     * Этот метод вызывается, когда ViewModel уничтожается,
     * (когда закрыли приложение)
     */
    override fun onCleared() {
        super.onCleared()
        Log.v("MainViewModel", "onCleared вызван.")

// сбросить
        if (wasFirstRunInThisSession) {

            SettingsManager.setFirstRun()
            Log.v("MainViewModel", "Флаг первого запуска сброшен в onCleared.")
        }
    }
}
