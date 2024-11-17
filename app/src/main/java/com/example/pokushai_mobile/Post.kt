package com.example.pokushai_mobile

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class Post: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post)


        val postBackground: LinearLayout = findViewById(R.id.postBackground)

        // Проверяем текущую тему приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            postBackground.setBackgroundResource(R.drawable.shape_dark)
        } else {
            // Светлая тема
            postBackground.setBackgroundResource(R.drawable.shape_light)
        }

    }
}
