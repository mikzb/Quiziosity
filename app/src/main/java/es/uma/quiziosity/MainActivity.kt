package es.uma.quiziosity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import es.uma.quiziosity.QuiziosityApp.Companion.getSharedPreferences
import es.uma.quiziosity.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "user_id" || key == "username") {
            updateNavigationMenu(binding)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        // Add click listener for the settings button
        val settingsButton: ImageButton = findViewById(R.id.btn_settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


        sharedPreferences = getSharedPreferences()
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        updateNavigationMenu(binding)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_login, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> {
                    menuItem.isChecked = true
                    drawerLayout.closeDrawers()
                    navController.navigate(menuItem.itemId)
                    true
                }
            }
        }
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.remove("user_id")
        editor.remove("username")
        editor.apply()

        Snackbar.make(binding.root, R.string.message_logout, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

// Check if user is logged in
private fun isUserLoggedIn(): Boolean {
    val sharedPreferences = getSharedPreferences()
    val userId = sharedPreferences.getString("user_id", null)
    val username = sharedPreferences.getString("username", null)
    return userId != null && username != null
}


// Update the navigation menu (including username in header)
private fun updateNavigationMenu(binding: ActivityMainBinding) {
    val navigationView = binding.navView
    val menu = navigationView.menu
    val loginItem = menu.findItem(R.id.nav_login)
    val logoutItem = menu.findItem(R.id.nav_logout)

    // Update the username and best score in the header
    val sharedPreferences = getSharedPreferences()
    val username = sharedPreferences.getString("username", null)
    val bestScore = sharedPreferences.getString("best_score", "0")
    val headerView = navigationView.getHeaderView(0)
    val usernameTextView: TextView = headerView.findViewById(R.id.nav_header_username_textview)
    val bestScoreTextView: TextView = headerView.findViewById(R.id.nav_header_best_score_textview)

    if (username != null) {
        usernameTextView.text = username
        bestScoreTextView.text = QuiziosityApp.getContext().getString(R.string.best_score, bestScore)
        bestScoreTextView.visibility = View.VISIBLE
    } else {
        usernameTextView.text = QuiziosityApp.getContext().getString(R.string.guest)  // Default if no username is found
        bestScoreTextView.visibility = View.GONE
    }

    // Handle visibility of login/logout items
    if (isUserLoggedIn()) {
        loginItem.isVisible = false
        logoutItem.isVisible = true
    } else {
        loginItem.isVisible = true
        logoutItem.isVisible = false
    }
}