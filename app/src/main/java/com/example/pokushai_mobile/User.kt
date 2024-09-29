package com.example.pokushai_mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class User : AppCompatActivity() {

    private lateinit var imageViewProfile: ImageView
    private lateinit var apiService: ApiService  // Объявляем переменную для ApiService
    private val PICK_IMAGE_REQUEST = 1
    private var loggedInUserId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        apiService = Retrofit.Builder()
            .baseUrl("http://172.20.10.2:8000/")  // Адрес вашего сервера
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getLong("user_id", -1)

        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)
        imageViewProfile = findViewById(R.id.imageViewProfile)

        if (loggedInUserId != -1L && loggedInUserId != 0L) {
            // Загружаем профиль пользователя
            apiService.getUserProfile(loggedInUserId!!).enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        val profile = response.body()
                        profile?.let {
                            textViewUsername.text = it.username  // Отображаем имя пользователя
                        }
                    } else {
                        Log.e("User", "Ошибка получения профиля: ${response.errorBody()?.string()}")
                        Toast.makeText(this@User, "Ошибка получения профиля", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.e("User", "Ошибка: ${t.message}")
                    Toast.makeText(this@User, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Если ID пользователя не найден, перенаправьте на экран входа
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        }

        val logOut = findViewById<Button>(R.id.logOut)
        logOut.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply() // Очистка данных пользователя
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun getBytesFromBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST)
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageViewProfile.setImageURI(imageUri)

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            val imageBytes = getBytesFromBitmap(bitmap)

            loggedInUserId?.let { userId ->
                // Отправка изображения на сервер, если это необходимо
                // Например, вы можете вызвать метод API для загрузки изображения
            }

            imageViewProfile.setImageBitmap(bitmap)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_IMAGE_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        }
    }
}
