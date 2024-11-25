package com.example.pokushai_mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.SearchView
import android.view.View
import android.content.Intent
import android.content.res.Configuration
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class SecondActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val switches = mutableListOf<Switch>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnClickListener {
            searchView.isIconified = false
        }


        val layout: LinearLayout = findViewById(R.id.bottomMenu)

        // Проверяем текущую тему приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            layout.setBackgroundResource(R.drawable.bottom_menu_dark)
        } else {
            // Светлая тема
            layout.setBackgroundResource(R.drawable.bottom_menu_light)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterSwitches(newText)
                return true
            }
        })

        val feed = findViewById<ImageButton>(R.id.feed)
        feed.setOnClickListener {
            val intent = Intent(this, Feed::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val allRecipes = findViewById<ImageButton>(R.id.allRecipes)
        allRecipes.setOnClickListener {
            val intent = Intent(this, AllRecipes::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val user = findViewById<ImageButton>(R.id.user)
        user.setOnClickListener {
            if (isInternetAvailable(this)) {
                val intent = Intent(this, User::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }



        //val imageButtonCutlets = findViewById<ImageButton>(R.id.imageButtonCutlets)
        //imageButtonCutlets.setOnClickListener {
        //    val intent = Intent(this, cutlets::class.java)
        //    startActivity(intent)
        //}


        val modelFile = assets.openFd("model.tflite")
        val startOffset = modelFile.startOffset
        val length = modelFile.length
        val fileChannel = FileInputStream(modelFile.fileDescriptor).channel
        val mappedByteBuffer: MappedByteBuffer =
            fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
        interpreter = Interpreter(mappedByteBuffer)

        for (i in 1..96) {
            val switchId = resources.getIdentifier("switch$i", "id", packageName)
            val switch = findViewById<Switch>(switchId)
            switches.add(switch)
        }

        val deleteCheck = findViewById<Button>(R.id.deleteCheck)

        deleteCheck.setOnClickListener {
            for (switch in switches) {
                switch.isChecked = false
            }
        }

        //закомментил хуи)

        /*val nav = findViewById<Button>(R.id.nav)
        nav.setOnClickListener {
            val intent = Intent(this, Nav_menu::class.java)
            startActivity(intent)
        }

        val recip = findViewById<Button>(R.id.recip)
        recip.setOnClickListener {
            val intent = Intent(this, RecipeDesigner::class.java)
            startActivity(intent)
        }*/

        val buttonNext2 = findViewById<Button>(R.id.buttonNext2)

        buttonNext2.setOnClickListener {
            val switchValues = switches.map { it.isChecked }
            val inputArray = Array(1) { FloatArray(switchValues.size) }
            for (i in switchValues.indices) {
                inputArray[0][i] = if (switchValues[i]) 1.0f else 0.0f
            }
            val outputArray = Array(1) { FloatArray(50) }
            interpreter.run(inputArray, outputArray)
            val result = outputArray[0]
            val maxIndex = result.indexOfMax()
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("switchValues", switchValues.toBooleanArray())
            intent.putExtra("result", result)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        filterSwitches(null)

    }

    private fun filterSwitches(query: String?) {
        switches.forEach { switch ->
            val switchText = switch.text.toString().toLowerCase()
            if (query.isNullOrEmpty() || switchText.contains(query.toLowerCase())) {
                switch.visibility = View.VISIBLE
            } else {
                switch.visibility = View.GONE
            }
        }
    }

    fun FloatArray.indexOfMax(): Int {
        var maxIndex = 0
        for (i in 1 until this.size) {
            if (this[i] > this[maxIndex]) {
                maxIndex = i
            }
        }
        return maxIndex
    }
}

