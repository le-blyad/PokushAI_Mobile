package com.example.pokushai_mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView

class User : AppCompatActivity() {

    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userDAO = UserDAO(this)
        val logOut = findViewById<Button>(R.id.logOut)
        val loggedInUser = userDAO.getLoggedInUser()
        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)

        textViewUsername.text = "$loggedInUser"

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



    }

}