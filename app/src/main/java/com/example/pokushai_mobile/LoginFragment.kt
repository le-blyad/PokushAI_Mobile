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
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private val apiService by lazy { ApiClient.instance }
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        // Обработка системной кнопки «Назад»
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack("login", 0)
        }

        // UI
        val etLogin  = view.findViewById<EditText>(R.id.inputFieldLogin)
        val etPass   = view.findViewById<EditText>(R.id.inputFieldPassword)
        val layout   = view.findViewById<LinearLayout>(R.id.topMenu)
        val btnBack  = view.findViewById<ImageButton>(R.id.buttonBack)
        val tvReg    = view.findViewById<TextView>(R.id.registration)
        val tvForgot = view.findViewById<TextView>(R.id.forgotPassword)
        val btnLogIn = view.findViewById<TextView>(R.id.buttonLogIn)
        val tilLogin = view.findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
        val tilPass  = view.findViewById<TextInputLayout>(R.id.inputFieldPasswordLayout)

        // Тема
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        layout.setBackgroundResource(
            if (mode == Configuration.UI_MODE_NIGHT_YES)
                R.drawable.bottom_menu_dark
            else
                R.drawable.bottom_menu_light
        )

        // Встроенная кнопка "Назад" в тулбаре
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack("login", 0)
        }

        // Переход на регистрацию
        tvReg.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, RegistrationFragment())
                addToBackStack(null)
                setCustomAnimations(
                    R.anim.fade_in, R.anim.fade_out,
                    R.anim.fade_in, R.anim.fade_out
                )
            }
        }

        // Забыли пароль
        tvForgot.setOnClickListener {
            Intent(Intent.ACTION_VIEW).also {
                it.data = Uri.parse("https://pokushai.pythonanywhere.com/users/password-reset/")
                startActivity(it)
            }
        }

        // Логин
        btnLogIn.setOnClickListener {
            val login = etLogin.text.toString()
            val pass  = etPass.text.toString()

            if (login.isEmpty() || pass.isEmpty()) {
                tilLogin.error = "Логин не может быть пустым"
                tilPass.error  = "Пароль не может быть пустым"
                return@setOnClickListener
            }
            if (!isInternetAvailable(requireContext())) {
                Toast.makeText(requireContext(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            apiService.login(LoginRequest(login, pass))
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.userId?.let { onLoginSuccess(it) }
                            Toast.makeText(requireContext(), "Успешный вход!", Toast.LENGTH_SHORT).show()
                        } else {
                            tilLogin.error = "Неверный логин или пароль"
                            tilPass.error  = "Неверный логин или пароль"
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun onLoginSuccess(userId: Long) {
        sessionManager.saveUserId(userId)
        Log.d("LogIn", "Сохранён user_id: $userId")

        val fm = parentFragmentManager
        // 1) Заменяем Login -> Main без back-stack
        fm.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, MainFragment())
        }
        // 2) Поверх Main -> User с back-stack именем "user"
        fm.commit {
            setReorderingAllowed(true)
            setCustomAnimations(
                R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out
            )
            replace(R.id.fragment_container, UserFragment())
            addToBackStack("user")
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null && ni.isConnected
    }
}
