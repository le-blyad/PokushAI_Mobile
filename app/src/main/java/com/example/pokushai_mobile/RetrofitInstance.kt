package com.example.pokushai_mobile
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.34:8000/api/"  // Замените на URL вашего Django сервера

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: MyApi by lazy {
        retrofit.create(MyApi::class.java)
    }
}