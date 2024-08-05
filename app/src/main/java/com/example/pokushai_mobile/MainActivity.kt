package com.example.pokushai_mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Задержка в 3 секунды (3000 миллисекунд)
        Handler().postDelayed({
            // Переход на основную активити
            startActivity(Intent(this, SecondActivity::class.java))
            // Закрываем SplashActivity, чтобы пользователь не мог вернуться к ней
            finish()
        }, 500) // Задержка в миллисекундах
    }
}