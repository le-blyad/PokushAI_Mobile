package com.example.pokushai_mobile
import retrofit2.Call
import retrofit2.http.*
import okhttp3.*

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
    val message: String,
    val userId: Long
)

data class Profile(
    val username: String,
    val userId: Long,
    val userImage: String?
)

data class ProfileResponse(
    val message: String,
    val success: Boolean // Или другие поля, которые вам нужны
)


interface ApiService {
    @GET("users/profiles/{id}/")
    fun getUserProfile(@Path("id") id: Long): Call<Profile>

    @POST("register/")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Multipart
    @POST("/users/profile")
    fun uploadProfileImage(
        @Part("user_id") userId: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>

    @DELETE("media/profile_pics/{id}/delete-image/")
    fun deleteProfileImage(@Path("id") id: Long): Call<ResponseBody>

}


