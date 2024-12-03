package com.example.pokushai_mobile

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class AllRecipes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_recipes)

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

        val buttonBack2 = findViewById<ImageButton>(R.id.buttonBack)

        val imageButtones = mutableListOf<ImageButton>()
        for (i in 0..49) {
            val imageButtonId = resources.getIdentifier("imageButton$i", "id", packageName)
            val imageButton = findViewById<ImageButton>(imageButtonId)
            imageButtones.add(imageButton)
            val id = i + 1
            imageButtones[i].setOnClickListener {
                val intent = Intent(this, TestRecip::class.java)
                intent.putExtra("recipe_id", id)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }


        val textViewes = mutableListOf<TextView>()
        for (i in 0..49) {
            val textViewId = resources.getIdentifier("textView$i", "id", packageName)
            val textView = findViewById<TextView>(textViewId)
            textViewes.add(textView)
        }

        buttonBack2.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

    }
}
