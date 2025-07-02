package com.example.pokushai_mobile

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private val apiService by lazy { ApiClient.instance }
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        val inputFieldLogin = view.findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldPassword = view.findViewById<EditText>(R.id.inputFieldPassword)
        val inputFieldLoginLayout = view.findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
        val inputFieldPasswordLayout = view.findViewById<TextInputLayout>(R.id.inputFieldPasswordLayout)

        val layout: LinearLayout = view.findViewById(R.id.topMenu)

        // Проверяем текущую тему приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            layout.setBackgroundResource(R.drawable.bottom_menu_dark)
        } else {
            // Светлая тема
            layout.setBackgroundResource(R.drawable.bottom_menu_light)
        }

        val buttonBack = view.findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val registration = view.findViewById<TextView>(R.id.registration)
        registration.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, RegistrationFragment())
                addToBackStack(null)
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            }
        }

        val forgotPassword = view.findViewById<TextView>(R.id.forgotPassword)
        forgotPassword.setOnClickListener {
            val url = "http://192.168.1.34:8000/users/password-reset/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val buttonLogIn = view.findViewById<TextView>(R.id.buttonLogIn)
        buttonLogIn.setOnClickListener {
            val username = inputFieldLogin.text.toString()
            val password = inputFieldPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                inputFieldLoginLayout.error = "Логин не может быть пустым"
                inputFieldPasswordLayout.error = "Пароль не может быть пустым"
                return@setOnClickListener
            }

            if (isInternetAvailable(requireContext())) {
                // Создание объекта запроса
                val loginRequest = LoginRequest(username, password)

                // Выполнение запроса на сервер
                apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            Log.d("LogIn", "Ответ от сервера: ${loginResponse}") // Логирование
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
    }

    private fun onLoginSuccess(userId: Long) {
        sessionManager.saveUserId(userId)
        Log.d("LogIn", "Сохранён user_id: $userId")

        parentFragmentManager.commit {
            replace(R.id.fragment_container, UserFragment())
            setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            // Очищаем back stack, чтобы пользователь не мог вернуться к экрану входа
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}