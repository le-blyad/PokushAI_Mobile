package com.example.pokushai_mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class AllRecipes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_recipes)

        val buttonBack2 = findViewById<ImageButton>(R.id.buttonBack2)

        val imageButtones = mutableListOf<ImageButton>()
        for (i in 0..49) {
            val imageButtonId = resources.getIdentifier("imageButton$i", "id", packageName)
            val imageButton = findViewById<ImageButton>(imageButtonId)
            imageButtones.add(imageButton)
        }


        val textViewes = mutableListOf<TextView>()
        for (i in 0..49) {
            val textViewId = resources.getIdentifier("textView$i", "id", packageName)
            val textView = findViewById<TextView>(textViewId)
            textViewes.add(textView)
        }

        buttonBack2.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        imageButtones[0].setOnClickListener {
            val intent = Intent(this, Belyashi::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        imageButtones[10].setOnClickListener {
            val intent = Intent(this, cutlets::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

    }
}
