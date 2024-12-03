package com.example.pokushai_mobile
import android.icu.text.CaseMap.Title
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

data class userUpdateProfileRequest(
    val userId: Long,
    val username: String,
    val email: String,
    val phone: String
)

data class userUpdateProfileResponse(
    val message: String
)

data class updatePasswordRequest(
    val userId: Long,
    val password: String,
    val oldPassword: String
)

data class updatePasswordResponse(
    val message: String
)

data class aboutPost(
    val author: String,
    val title: String,
    val des: String,
    val image: String?,
    val created_at: String,
    val update_at: String,
    val views: String,
    val likes: String
)

data class aboutUser(
    val user_image: String?
)

data class usersPostsGetResponse(
    val data: List<aboutPost>,
    val user_data: List<aboutUser>,
)

interface ApiService {
    @GET("profiles/{id}")
    fun getUserProfile(@Path("id") id: Long): Call<Profile>

    @POST("register/")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Multipart
    @POST("update_profile_pic/")
    fun uploadProfileImage(
        @Part("userId") userId: Long,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>

    @DELETE("delete_image/{id}/")
    fun deleteProfileImage(@Path("id") id: Long): Call<ResponseBody>

    @POST("update_profile/")
    fun userUpdateProfile(@Body request: userUpdateProfileRequest): Call<userUpdateProfileResponse>

    @POST("update_password/")
    fun userUpdateProfile(@Body request: updatePasswordRequest): Call<updatePasswordResponse>

    @GET("users_posts/")
    fun usersPostsGet(@Query("userId") userId: Long): Call<usersPostsGetResponse>

}


