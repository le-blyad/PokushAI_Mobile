package com.example.pokushai_mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class LogIn : AppCompatActivity() {

    private lateinit var userDAO: UserDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        userDAO = UserDAO(this)

        val inputFieldLogin = findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldPassword = findViewById<EditText>(R.id.inputFieldPassword)

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

        val buttonLogIn = findViewById<TextView>(R.id.buttonLogIn)
        buttonLogIn.setOnClickListener {
            val username = inputFieldLogin.text.toString()
            val password = inputFieldPassword.text.toString()
            val isValid = userDAO.checkUser(username, password)

            if (isInternetAvailable(this)) {
                if (isValid) {
                    userDAO.setUserLoggedIn(username) // Установка пользователя как вошедшего
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

                    Toast.makeText(this, "Успешный вход в аккаунт!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Логин и/или пароль неверный", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

