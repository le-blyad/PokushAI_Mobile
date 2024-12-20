package com.example.pokushai_mobile

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Feed : AppCompatActivity() {

    val apiService = ApiClient.instance
    private var loggedInUserId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getLong("user_id", -1)

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val buttonRecipeDesigner = findViewById<ImageButton>(R.id.buttonRecipeDesigner)
        buttonRecipeDesigner.setOnClickListener {
            val intent = Intent(this, RecipeDesigner::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val posts: RecyclerView = findViewById(R.id.posts)
        posts.layoutManager = LinearLayoutManager(this)

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

        val call = apiService.usersPostsGet(loggedInUserId!!)
        call.enqueue(object: Callback<usersPostsGetResponse>{
            override fun onResponse(call: Call<usersPostsGetResponse>, response: Response<usersPostsGetResponse>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(this@Feed, "Работает?", Toast.LENGTH_SHORT).show()
                    val samplePosts = response.body()?.data ?: emptyList()
                    val adapter = PostAdapter(samplePosts)
                    posts.adapter = adapter
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Не работает("
                    Log.e("Upload", "Error: $errorBody")
                    Toast.makeText(this@Feed, "Вот так - $errorBody", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<usersPostsGetResponse>, t: Throwable) {
                Log.e("Upload", "Errors: ${t.message}")
                Toast.makeText(this@Feed, "Xyeta: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}