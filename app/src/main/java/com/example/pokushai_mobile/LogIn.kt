package com.example.pokushai_mobile

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.widget.LinearLayout


class LogIn : AppCompatActivity() {

    val apiService = ApiClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        val inputFieldLogin = findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldPassword = findViewById<EditText>(R.id.inputFieldPassword)
        val inputFieldLoginLayout = findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
        val inputFieldPasswordLayout = findViewById<TextInputLayout>(R.id.inputFieldPasswordLayout)

        val layout: LinearLayout = findViewById(R.id.topMenu)

        // Проверяем текущую тему приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            layout.setBackgroundResource(R.drawable.bottom_menu_dark)
        } else {
            // Светлая тема
            layout.setBackgroundResource(R.drawable.bottom_menu_light)
        }

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val registration = findViewById<TextView>(R.id.registration)
        registration.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        }

        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        forgotPassword.setOnClickListener {
            val url = "http://192.168.1.34:8000/users/password-reset/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val buttonLogIn = findViewById<TextView>(R.id.buttonLogIn)
        buttonLogIn.setOnClickListener {
            val username = inputFieldLogin.text.toString()
            val password = inputFieldPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                inputFieldLoginLayout.error = "Логин не может быть пустым"
                inputFieldPasswordLayout.error = "Пароль не может быть пустым"
                return@setOnClickListener
            }

            if (isInternetAvailable(this)) {
                // Создание объекта запроса
                val loginRequest = LoginRequest(username, password)

                // Выполнение запроса на сервер
                apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            Log.d("LogIn", "Ответ от сервера: ${loginResponse}") // Логирование
                            loginResponse?.userId?.let { userId ->
                                onLoginSuccess(userId)
                            }
                            Toast.makeText(this@LogIn, "Успешный вход!", Toast.LENGTH_SHORT).show()
                        } else {
                            inputFieldLoginLayout.error = "Логин и/или пароль неверный"
                            inputFieldPasswordLayout.error = "Логин и/или пароль неверный"
                        }
                    }


                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LogIn, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun onLoginSuccess(userId: Long) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPreferences.edit().putLong("user_id", userId).apply()
        Log.d("LogIn", "Сохранён user_id: $userId") // Логирование
        val intent = Intent(this, User::class.java)
        startActivity(intent)
        finish()
    }

}