package es.uma.quiziosity

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import es.uma.quiziosity.utils.UserUtils
import kotlinx.coroutines.launch

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.action_settings)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Go back to the previous activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            when (preference.key) {
                "reset_score" -> {
                    showConfirmationDialog(
                        title = getString(R.string.reset_score),
                        message = getString(R.string.reset_score_confirmation),
                        onConfirm = { lifecycleScope.launch { resetScore() } }
                    )
                    return true
                }
                "delete_account" -> {
                    showConfirmationDialog(
                        title = getString(R.string.delete_account),
                        message = getString(R.string.delete_account_confirmation),
                        onConfirm = {
                            lifecycleScope.launch { deleteAccount() }
                            QuiziosityApp.getSharedPreferences().edit().clear().apply()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    )
                    return true
                }
            }
            return super.onPreferenceTreeClick(preference)
        }

        private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ -> onConfirm() }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        private suspend fun resetScore() {
            QuiziosityApp.getUserRepository().updateScore(UserUtils.getUsername()!!, 0)
        }

        private suspend fun deleteAccount() {
            QuiziosityApp.getUserRepository().deleteUserByUsername(UserUtils.getUsername()!!)
        }
    }
}