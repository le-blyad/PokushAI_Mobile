package com.example.pokushai_mobile
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Path


// Определите модель данных, которая будет использоваться
data class RegisterRequest(
    val username: String,
    val phone: String,
    val email: String,
    val password1: String,
    val password2: String
)
data class RegisterResponse(
    val message: String,
    val userId: Long
)

data class LoginRequest(
    val username: String,
    val password: String
)
data class LoginResponse(
    val message: String, // Ожидаемый формат ответа
    val userId: Long
)

data class Profile(
    val username: String,
    val userId: Long
)

interface ApiService {
    @GET("users/profiles/{id}/")
    fun getUserProfile(@Path("id") id: Long): Call<Profile>

    @POST("register/")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}


