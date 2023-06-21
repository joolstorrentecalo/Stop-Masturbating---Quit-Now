package com.example.quitnow

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.quitnow.data.db.user.UserEntity
import com.example.quitnow.databinding.ActivityMainBinding
import com.example.quitnow.util.hide
import com.example.quitnow.util.show
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)

        setupBottomNavMenu(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Timber.d("Current destination ${destination.label}")

            val menuItem: MenuItem = binding.toolbar.menu.findItem(R.id.menu_item_settings)
            binding.toolbar.title = destination.label.toString()

            when (destination.id) {
                R.id.settingsFragment -> {
                    menuItem.isVisible = false
                    binding.bottomNav.hide()
                }
                else -> {
                    menuItem.setOnMenuItemClickListener {
                        navController.navigate(NavGraphDirections.actionGlobalSettingsFragment())
                        true
                    }
                    menuItem.isVisible = true
                    binding.bottomNav.show()
                }
            }
        }

        viewModel.user.observe(this) { user: UserEntity? ->
            Timber.d("User observe activity $user")

            when (user) {
                null -> {
                    navController.navigate(NavGraphDirections.actionGlobalInclusiveSettingsFragment())
                }
                else -> {
                    if (navController.currentDestination?.id == R.id.settingsFragment) {
                        navController.navigate(NavGraphDirections.actionGlobalProgressFragment())
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_actions_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)

        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        binding.bottomNav.setupWithNavController(navController)
    }
}

class SetMinMaxvalue : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val editText = findViewById<EditText>(R.id.text)
        editText.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
    }
    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 1
        private var intMax: Int = 1000
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }
        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return "Please fill in a value between 1 and 1000!"
        }
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }
}