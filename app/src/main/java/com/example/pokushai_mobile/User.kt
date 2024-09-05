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
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory

class User : AppCompatActivity() {

    private lateinit var userDAO: UserDAO
    private lateinit var imageViewProfile: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private var loggedInUserId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userDAO = UserDAO(this)
        val logOut = findViewById<Button>(R.id.logOut)
        val loggedInUser = userDAO.getLoggedInUser()
        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)
        imageViewProfile = findViewById(R.id.imageViewProfile)
        val buttonRemoveImage = findViewById<Button>(R.id.buttonRemoveImage)
        val buttonAddImage = findViewById<Button>(R.id.buttonAddImage)


        textViewUsername.text = "$loggedInUser"

        loggedInUserId = userDAO.getUserIdByUsername(loggedInUser)

        logOut.setOnClickListener {
            Log.d("Logout", "Log out button clicked")
            try {
                userDAO.clearUserLoggedIn()
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            } catch (e: Exception) {
                Log.e("LogoutError", "Error logging out", e)
            }
        }

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        loggedInUserId?.let { userId ->
            val imageBytes = userDAO.getProfileImage(userId)
            if (imageBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageViewProfile.setImageBitmap(bitmap)
            }
        }

        buttonAddImage.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        buttonRemoveImage.setOnClickListener {
            loggedInUserId?.let { userId ->
                userDAO.removeProfileImage(userId)
                imageViewProfile.setImageResource(R.drawable.no_photo) // Установите изображение по умолчанию
            }
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
                userDAO.updateProfileImage(userId, imageBytes)
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

