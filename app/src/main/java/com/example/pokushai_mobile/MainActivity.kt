package com.example.pokushai_mobile

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация нижнего меню
        initBottomMenu()

        // Загрузка главного фрагмента при запуске
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .commit()
        }
    }

    private fun initBottomMenu() {
        val feedButton = findViewById<ImageButton>(R.id.feed)
        val allRecipesButton = findViewById<ImageButton>(R.id.allRecipes)
        val userButton = findViewById<ImageButton>(R.id.user)

        feedButton.setOnClickListener {
            if (isInternetAvailable(this)) {
                // Заглушка для UserFragment
                Toast.makeText(this, "Feed Fragment (в разработке)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }

        allRecipesButton.setOnClickListener {
            navigateToFragment(AllRecipesFragment())
        }

        userButton.setOnClickListener {
            if (isInternetAvailable(this)) {
                // Заглушка для UserFragment
                Toast.makeText(this, "User Fragment (в разработке)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Функция проверки интернета
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}