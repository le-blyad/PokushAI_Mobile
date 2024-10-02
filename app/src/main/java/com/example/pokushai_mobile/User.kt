package com.example.pokushai_mobile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import okhttp3.RequestBody.Companion.toRequestBody
import com.squareup.picasso.Picasso
import okhttp3.*

class User : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    val apiService = ApiClient.instance
    private lateinit var imageViewProfile: ImageView
    private lateinit var buttonAddImage: Button
    private lateinit var buttonRemoveImage: Button
    private var loggedInUserId: Long? = null
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Проверка разрешений на доступ к хранилищу
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST)
        }

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getLong("user_id", -1)

        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)
        imageViewProfile = findViewById(R.id.imageViewProfile)
        buttonAddImage = findViewById(R.id.buttonAddImage)
        buttonRemoveImage = findViewById(R.id.buttonRemoveImage)

        // Логика для кнопки добавления изображения
        buttonAddImage.setOnClickListener {
            openFileChooser()
        }

        // Логика для кнопки удаления изображения
        buttonRemoveImage.setOnClickListener {
            deleteProfileImage()
        }

        // Проверяем, что userId не пустой
        if (loggedInUserId != -1L && loggedInUserId != 0L) {
            // Загружаем профиль пользователя

            apiService.getUserProfile(loggedInUserId!!).enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        val profile = response.body()
                        profile?.let {
                            textViewUsername.text = it.username  // Отображаем имя пользователя
                            // Загружаем изображение профиля
                            val imageUrl = it.userImage // Предполагаем, что это поле есть в объекте Profile

                            if (!imageUrl.isNullOrEmpty()) {
                                Picasso.get()
                                    .load(imageUrl)
                                    .placeholder(R.drawable.no_photo)
                                    .into(imageViewProfile)
                            } else {
                                imageViewProfile.setImageResource(R.drawable.no_photo)
                            }
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
            // Если ID пользователя не найден, перенаправляем на экран входа
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        }

        // Логика для кнопки выхода и кнопки назад
        val logOut = findViewById<Button>(R.id.logOut)
        logOut.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
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


    // Функция для открытия выбора файла
    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    // Обработка результата выбора изображения
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                currentImageUri = it
                imageViewProfile.setImageURI(it) // Устанавливаем изображение в ImageView
                uploadProfileImage(it) // Загружаем изображение на сервер
            }
        }
    }


    fun getCompressedBitmap(uri: Uri): Bitmap? {
        return try {
            val originalBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            Bitmap.createScaledBitmap(originalBitmap, 800, 800, true)
        } catch (e: Exception) {
            Log.e("User", "Ошибка получения битмапа: ${e.message}")
            null
        }
    }


    private fun uploadProfileImage(imageUri: Uri?) {
        imageUri?.let {
            val bitmap = getCompressedBitmap(it) // Получаем сжатый Bitmap
            if (bitmap != null) {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream) // Сжимаем до 85% качества
                val byteArray = stream.toByteArray()

                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaType())
                val imagePart = MultipartBody.Part.createFormData("image", "profile_image.jpg", requestBody)
                val userIdRequestBody = loggedInUserId!!.toString().toRequestBody("text/plain".toMediaType())

                // Выполняем POST запрос для загрузки изображения
                val call = apiService.uploadProfileImage(userIdRequestBody, imagePart)
                Log.d("Upload", "Uploading image for user ID: $loggedInUserId")
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@User, "Изображение загружено", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Ошибка"
                            Log.e("Upload", "Ошибка загрузки: $errorBody")
                            Toast.makeText(this@User, "Ошибка загрузки: $errorBody", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Upload", "Ошибка сети: ${t.message}")
                        Toast.makeText(this@User, "Ошибка сети", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Log.e("Upload", "Ошибка: Полученный Bitmap равен null")
                Toast.makeText(this, "Ошибка: Полученный Bitmap равен null", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun deleteProfileImage() {
        if (loggedInUserId != null) {
            val call = apiService.deleteProfileImage(loggedInUserId!!)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Устанавливаем изображение по умолчанию или выполняем другие действия
                        currentImageUri = null
                        Toast.makeText(this@User, "Изображение удалено", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Ошибка"
                        Toast.makeText(this@User, "Ошибка удаления: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@User, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "ID пользователя не найден", Toast.LENGTH_SHORT).show()
        }
    }

}