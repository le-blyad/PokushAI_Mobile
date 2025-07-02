package com.example.pokushai_mobile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import okhttp3.RequestBody.Companion.toRequestBody

class UserFragment : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private val apiService = ApiClient.instance
    private lateinit var imageViewProfile: ImageView
    private lateinit var buttonAddImage: ImageButton
    private lateinit var buttonRemoveImage: Button
    private lateinit var updateInfoProfile: Button
    private lateinit var buttonUpdatePassword: Button
    private var loggedInUserId: Long? = null
    private var currentImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            imageViewProfile.setImageURI(it)
            uploadProfileImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewUsername = view.findViewById<TextView>(R.id.textViewUsername)
        val layout = view.findViewById<LinearLayout>(R.id.topMenu)
        imageViewProfile = view.findViewById(R.id.imageViewProfile)
        buttonAddImage = view.findViewById(R.id.buttonAddImage)
        buttonRemoveImage = view.findViewById(R.id.buttonRemoveImage)
        updateInfoProfile = view.findViewById(R.id.updateInfoProfile)
        buttonUpdatePassword = view.findViewById(R.id.buttonUpdatePassword)
        val logOut = view.findViewById<ImageButton>(R.id.logOut)
        val buttonBack = view.findViewById<ImageButton>(R.id.buttonBack)

        // Проверка разрешений
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST)
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getLong("user_id", -1)

        // Проверка темы
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            val color = getColor(requireContext(), R.color.nutritionalValueDark)
            layout.setBackgroundResource(R.drawable.bottom_menu_dark)
            buttonAddImage.setBackgroundResource(R.drawable.photo_add_background_dark)
            buttonRemoveImage.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
            updateInfoProfile.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
            buttonUpdatePassword.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
        } else {
            val color = getColor(requireContext(), R.color.nutritionalValueLight)
            layout.setBackgroundResource(R.drawable.bottom_menu_light)
            buttonAddImage.setBackgroundResource(R.drawable.photo_add_background)
            buttonRemoveImage.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
            updateInfoProfile.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
            buttonUpdatePassword.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
        }

        buttonAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        buttonRemoveImage.setOnClickListener {
            deleteProfileImage()
        }

        updateInfoProfile.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, UpdateInfoProfileFragment())
                addToBackStack(null)
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            }
        }

        if (loggedInUserId != -1L && loggedInUserId != 0L) {
            apiService.getUserProfile(loggedInUserId!!).enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        val profile = response.body()
                        profile?.let {
                            textViewUsername.text = it.username
                            val imageUrl = it.userImage

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
                        Toast.makeText(requireContext(), "Ошибка получения профиля", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.e("User", "Ошибка: ${t.message}")
                    Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, LoginFragment())
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            }
        }

        logOut.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            parentFragmentManager.commit {
                replace(R.id.fragment_container, LoginFragment())
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            }
        }

        buttonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        buttonUpdatePassword.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, UpdatePasswordFragment())
                addToBackStack(null)
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            }
        }
    }

    private fun getCompressedBitmap(uri: Uri): Bitmap? {
        return try {
            val originalBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            Bitmap.createScaledBitmap(originalBitmap, 800, 800, true)
        } catch (e: Exception) {
            Log.e("User", "Ошибка получения битмапа: ${e.message}")
            null
        }
    }

    private fun uploadProfileImage(imageUri: Uri?) {
        imageUri?.let {
            val bitmap = getCompressedBitmap(it)
            if (bitmap != null) {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                val byteArray = stream.toByteArray()

                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaType())
                val imagePart = MultipartBody.Part.createFormData("image", "profile_image.jpg", requestBody)
                val userIdRequestBody = loggedInUserId!!

                val call = apiService.uploadProfileImage(userIdRequestBody, imagePart)
                Log.d("Upload", "Uploading image for user ID: $loggedInUserId")
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Изображение загружено", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Ошибка"
                            Log.e("Upload", "Ошибка загрузки: $errorBody")
                            Toast.makeText(requireContext(), "Ошибка загрузки: $errorBody", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Upload", "Ошибка сети: ${t.message}")
                        Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Log.e("Upload", "Ошибка: Полученный Bitmap равен null")
                Toast.makeText(requireContext(), "Ошибка: Полученный Bitmap равен null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteProfileImage() {
        if (loggedInUserId != null) {
            val call = apiService.deleteProfileImage(loggedInUserId!!)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        currentImageUri = null
                        imageViewProfile.setImageResource(R.drawable.no_photo)
                        Toast.makeText(requireContext(), "Изображение удалено", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Ошибка"
                        Toast.makeText(requireContext(), "Ошибка удаления: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "ID пользователя не найден", Toast.LENGTH_SHORT).show()
        }
    }
}