package es.uma.quiziosity

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import es.uma.quiziosity.utils.LocaleHelper

open class BaseActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    protected lateinit var sharedPreferences: SharedPreferences

    override fun attachBaseContext(newBase: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase)
        val language = sharedPreferences.getString("language", "en") ?: "en"
        val context = LocaleHelper.setLocale(newBase, language)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        applySettings()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "language") {
            recreate() // Restart the activity to apply the new language
        }
        else {
            applySettings()
    }
}

    private fun applySettings() {
        // Apply dark theme
        val darkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        // Apply volume
        val volume = sharedPreferences.getInt("volume", 50)
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        // If disabled, mute the volume
        if (!sharedPreferences.getBoolean("sound_effects", false)) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        }
    }
}