package com.example.pokushai_mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class AllRecipes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_recipes)

        val buttonBack2 = findViewById<Button>(R.id.buttonBack2)

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
        }

        imageButtones[0].setOnClickListener {
            val intent = Intent(this, Belyashi::class.java)
            startActivity(intent)
        }

        imageButtones[10].setOnClickListener {
            val intent = Intent(this, cutlets::class.java)
            startActivity(intent)
        }

    }
}
