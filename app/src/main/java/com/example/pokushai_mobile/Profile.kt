package com.example.pokushai_mobile
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path

// Определите модель данных, которая будет использоваться
data class Profile(
    val id: Int,
    val name: String,
    val description: String
)

interface MyApi {
    @GET("profiles/")
    fun getProfiles(): Call<List<Profile>>

    @GET("profiles/{id}/")
    fun getProfile(@Path("id") id: Int): Call<Profile>

    @POST("profiles/")
    fun createProfile(@Body profile: Profile): Call<Profile>
}

