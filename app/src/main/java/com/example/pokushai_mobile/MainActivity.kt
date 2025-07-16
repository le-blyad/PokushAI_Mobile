package com.example.pokushai_mobile

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {

    private val hiddenAllergens = mutableSetOf<Int>()

    fun getHiddenAllergens(): Set<Int> = hiddenAllergens

    fun setHiddenAllergens(allergens: Set<Int>) {
        hiddenAllergens.clear()
        hiddenAllergens.addAll(allergens)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isUserLoggedIn()) {
            loadFragment(UserFragment())
        } else {
            loadFragment(LoginFragment())
        }

        initBottomMenu()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .commit()
        }

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(newBase))
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1) != -1L
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun navigateToFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment::class.java == currentFragment?.javaClass) {
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun initBottomMenu() {
        val feedButton = findViewById<ImageButton>(R.id.feed)
        val allRecipesButton = findViewById<ImageButton>(R.id.allRecipes)
        val userButton = findViewById<ImageButton>(R.id.user)

        feedButton.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is MainFragment) return@setOnClickListener // Проверка

            if (isInternetAvailable(this)) {
                navigateToFragment(MainFragment())
            } else {
                Toast.makeText(this, "${getString(R.string.noconn)}", Toast.LENGTH_SHORT).show()
            }
        }

        allRecipesButton.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is ResultRecipesFragment) return@setOnClickListener // Проверка

            navigateToFragment(ResultRecipesFragment())
        }

        userButton.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

            if (isUserLoggedIn()) {
                if (currentFragment is UserFragment) return@setOnClickListener // Проверка
            } else {
                if (currentFragment is LoginFragment) return@setOnClickListener // Проверка
            }

            if (isInternetAvailable(this)) {
                if (isUserLoggedIn()) {
                    navigateToFragment(UserFragment())
                } else {
                    navigateToFragment(LoginFragment())
                }
            } else {
                Toast.makeText(this, "${getString(R.string.noconn)}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.language_menu, menu)
        return true
    }

    fun onLanguageButtonClick(view: View) {
        val currentLang = LocaleHelper.getLanguage(this)
        val newLang = if (currentLang == "ru") "en" else "ru"

        LocaleHelper.setLocale(this, newLang)
        recreateActivity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_language -> {
                toggleLanguage()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun recreateActivity() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun toggleLanguage() {
        val currentLang = LocaleHelper.getLanguage(this)
        val newLang = if (currentLang == "ru") "en" else "ru"

        LocaleHelper.setLocale(this, newLang)
        recreateActivity()
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }*/
}