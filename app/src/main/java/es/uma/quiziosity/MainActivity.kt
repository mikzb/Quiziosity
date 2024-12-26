package es.uma.quiziosity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import es.uma.quiziosity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
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

        sharedPreferences = QuiziosityApp.getSharedPreferences()
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        updateNavigationMenu(binding)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}

private fun updateNavigationMenu(binding: ActivityMainBinding) {
    val navigationView = binding.navView
    val menu = navigationView.menu
    val loginItem = menu.findItem(R.id.nav_login)
    val logoutItem = menu.findItem(R.id.nav_logout)

    if (isUserLoggedIn()) {
        loginItem.isVisible = false
        logoutItem.isVisible = true
    } else {
        loginItem.isVisible = true
        logoutItem.isVisible = false
    }
}

private fun isUserLoggedIn(): Boolean {
    val sharedPreferences = QuiziosityApp.getSharedPreferences()
    val userId = sharedPreferences.getString("user_id", null)
    val username = sharedPreferences.getString("username", null)
    return userId != null && username != null
}