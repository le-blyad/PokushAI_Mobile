package com.example.pokushai_mobile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

class LogIn : AppCompatActivity() {

    private lateinit var userDAO: UserDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        userDAO = UserDAO(this)

        val inputFieldLogin = findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldPassword = findViewById<EditText>(R.id.inputFieldPassword)
        val inputFieldLoginLayout = findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
        val inputFieldPasswordLayout = findViewById<TextInputLayout>(R.id.inputFieldPasswordLayout)

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
            val isValid = userDAO.checkUser(username, password)

            if (isInternetAvailable(this)) {
                if (isValid) {
                    userDAO.setUserLoggedIn(username) // Установка пользователя как вошедшего
                    val intent = Intent(this, SecondActivity::class.java)
                    // Устанавливаем флаги для Intent
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                    Toast.makeText(this, "Успешный вход в аккаунт!", Toast.LENGTH_SHORT).show()
                } else {
                    inputFieldLoginLayout.error = "Логин и/или пароль неверный"
                    inputFieldPasswordLayout.error = "Логин и/или пароль неверный"
                }

            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }
    }
}