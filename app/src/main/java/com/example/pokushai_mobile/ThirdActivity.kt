package com.example.pokushai_mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.view.View
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream
import android.widget.TextView

class ThirdActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val modelFile = assets.openFd("model.tflite")
        val fileDescriptor = modelFile.fileDescriptor
        val startOffset = modelFile.startOffset
        val length = modelFile.length
        val fileChannel = FileInputStream(modelFile.fileDescriptor).channel
        val mappedByteBuffer: MappedByteBuffer =
            fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
        interpreter = Interpreter(mappedByteBuffer)
        val switchValues = intent.getBooleanArrayExtra("switchValues")
        val buttonBack2 = findViewById<ImageButton>(R.id.buttonBack2)
        val result: FloatArray? = intent.getFloatArrayExtra("result")
        val pizda: Float? = result?.maxOrNull()
        var maxIndex = -1
        if (pizda != null && result != null) {
            for (i in result.indices) {
                if (result[i] == pizda) {
                    maxIndex = i
                    break
                }
            }
        }

        val imageButtones = mutableListOf<ImageButton>()
        for (i in 0..49) {
            val imageButtonId = resources.getIdentifier("imageButton$i", "id", packageName)
            val imageButton = findViewById<ImageButton>(imageButtonId)
            val visibility = (i == maxIndex)
            intent.putExtra("visible$i", visibility)
            if (i == maxIndex) {
                imageButton.visibility = View.VISIBLE
            } else {
                imageButton.visibility = View.GONE
            }
            imageButtones.add(imageButton)
        }

        val textViewes = mutableListOf<TextView>()
        for (i in 0..49) {
            val textViewId = resources.getIdentifier("textView$i", "id", packageName)
            val textView = findViewById<TextView>(textViewId)
            val visibility = (i == maxIndex)
            intent.putExtra("visible$i", visibility)
            if (i == maxIndex) {
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
            }
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