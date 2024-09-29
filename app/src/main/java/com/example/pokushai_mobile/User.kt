package com.example.pokushai_mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class User : AppCompatActivity() {

    val apiService = ApiClient.instance
    private lateinit var imageViewProfile: ImageView
    private var loggedInUserId: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getLong("user_id", -1)

        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)
        imageViewProfile = findViewById(R.id.imageViewProfile)

        // Проверяем, что userId не пустой
        if (loggedInUserId != -1L && loggedInUserId != 0L) {
            // Загружаем профиль пользователя
            apiService.getUserProfile(loggedInUserId!!).enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        val profile = response.body()
                        profile?.let {
                            textViewUsername.text = it.username  // Отображаем имя пользователя
                        }
                    } else {
                        Log.e("User", "Ошибка получения профиля: ${response.errorBody()?.string()}")
                        Toast.makeText(this@User, "Ошибка получения профиля", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.e("User", "Ошибка: ${t.message}")
                    Toast.makeText(this@User, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Если ID пользователя не найден, перенаправляем на экран входа
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        }

        // Логика для кнопки выхода и кнопки назад
        val logOut = findViewById<Button>(R.id.logOut)
        logOut.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}

