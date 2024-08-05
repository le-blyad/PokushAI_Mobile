package com.example.pokushai_mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import android.content.res.Configuration

class Belyashi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_belyashi)

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }

        val layout: LinearLayout = findViewById(R.id.allSteps)

// Проверяем текущую тему приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            layout.setBackgroundResource(R.drawable.shape_dark)
        } else {
            // Светлая тема
            layout.setBackgroundResource(R.drawable.shape_light)
        }

        val textViewCalories = findViewById<TextView>(R.id.textViewCalories)
        textViewCalories.text = "Калории\n699 ККАЛ"

        val textViewSquirrels = findViewById<TextView>(R.id.textViewSquirrels)
        textViewSquirrels.text = "Белки\n31 грамм"

        val textViewFats = findViewById<TextView>(R.id.textViewFats)
        textViewFats.text = "Жиры\n16 грамм"

        val textViewCarbohydrates = findViewById<TextView>(R.id.textViewCarbohydrates)
        textViewCarbohydrates.text = "Углеводы\n110 грамм"

    }
}