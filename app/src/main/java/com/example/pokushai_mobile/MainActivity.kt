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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomMenu()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .commit()
        }
    }

    fun onLanguageButtonClick(view: View) {
        val currentLang = LocaleHelper.getLanguage(this)
        val newLang = if (currentLang == "ru") "en" else "ru"

        LocaleHelper.setLocale(this, newLang)
        recreateActivity()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(newBase))
    }

    private fun initBottomMenu() {
        val feedButton = findViewById<ImageButton>(R.id.feed)
        val allRecipesButton = findViewById<ImageButton>(R.id.allRecipes)
        val userButton = findViewById<ImageButton>(R.id.user)

        feedButton.setOnClickListener {
            if (isInternetAvailable(this)) {
                Toast.makeText(this, "Feed Fragment (в разработке)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }

        allRecipesButton.setOnClickListener {
            navigateToFragment(ResultRecipesFragment())
        }

        userButton.setOnClickListener {
            if (isInternetAvailable(this)) {
                Toast.makeText(this, "User Fragment (в разработке)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.language_menu, menu)
        return true
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

    private fun toggleLanguage() {
        val currentLang = LocaleHelper.getLanguage(this)
        val newLang = if (currentLang == "ru") "en" else "ru"

        LocaleHelper.setLocale(this, newLang)
        recreateActivity()
    }

    private fun recreateActivity() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun navigateToFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment?.javaClass == fragment.javaClass) return

        FragmentNavigator.navigateForward(
            supportFragmentManager,
            R.id.fragment_container,
            fragment
        )
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}