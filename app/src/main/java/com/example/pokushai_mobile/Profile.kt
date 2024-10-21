package com.example.pokushai_mobile
import retrofit2.Call
import android.net.Uri
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

data class Step(
    var number: Int,
    var photoUri: Uri? = null,
    var description: String = "",
)



interface ApiService {
    @GET("profiles/{id}")
    fun getUserProfile(@Path("id") id: Long): Call<Profile>

    @POST("register/")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Multipart
    @POST("update_profile/")
    fun uploadProfileImage(
        @Part("userId") userId: Long,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>

    @DELETE("delete_image/{id}/")
    fun deleteProfileImage(@Path("id") id: Long): Call<ResponseBody>

}


