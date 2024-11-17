package com.example.pokushai_mobile.ui.slideshow

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pokushai_mobile.ApiClient
import com.example.pokushai_mobile.LoginRequest
import com.example.pokushai_mobile.LoginResponse
import com.example.pokushai_mobile.R
import com.example.pokushai_mobile.Registration
import com.example.pokushai_mobile.User
import com.example.pokushai_mobile.databinding.FragmentSlideshowBinding
import com.example.pokushai_mobile.isInternetAvailable
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    val apiService = ApiClient.instance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val inputFieldLogin = root.findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldPassword = root.findViewById<EditText>(R.id.inputFieldPassword)
        val inputFieldLoginLayout = root.findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
        val inputFieldPasswordLayout = root.findViewById<TextInputLayout>(R.id.inputFieldPasswordLayout)

        val registration = root.findViewById<TextView>(R.id.registration)
        registration.setOnClickListener {
            val intent = Intent(requireContext(), Registration::class.java)  // Используем requireContext()
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)  // Анимация в Activity
        }

        val forgotPassword = root.findViewById<TextView>(R.id.forgotPassword)
        forgotPassword.setOnClickListener {
            val url = "http://192.168.1.34:8000/users/password-reset/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val buttonLogIn = root.findViewById<TextView>(R.id.buttonLogIn)
        buttonLogIn.setOnClickListener {
            val username = inputFieldLogin.text.toString()
            val password = inputFieldPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                inputFieldLoginLayout.error = "Логин не может быть пустым"
                inputFieldPasswordLayout.error = "Пароль не может быть пустым"
                return@setOnClickListener
            }

            if (isInternetAvailable(requireContext())) {  // Используем requireContext()
                val loginRequest = LoginRequest(username, password)

                apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            Log.d("LogIn", "Ответ от сервера: ${loginResponse}")
                            loginResponse?.userId?.let { userId ->
                                onLoginSuccess(userId)
                            }
                            Toast.makeText(requireContext(), "Успешный вход!", Toast.LENGTH_SHORT).show()
                        } else {
                            inputFieldLoginLayout.error = "Логин и/или пароль неверный"
                            inputFieldPasswordLayout.error = "Логин и/или пароль неверный"
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireContext(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onLoginSuccess(userId: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putLong("user_id", userId).apply()
        Log.d("LogIn", "Сохранён user_id: $userId")

        val intent = Intent(requireContext(), User::class.java)
        startActivity(intent)
        requireActivity().finish()  // Закрываем активность
    }
}
