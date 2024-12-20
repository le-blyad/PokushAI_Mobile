package com.example.pokushai_mobile

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePassword : AppCompatActivity() {

    private val apiService = ApiClient.instance
    private var loggedInUserId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getLong("user_id", -1)

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

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

        // Инициализация полей ввода
        val inputFieldOldPassword = findViewById<EditText>(R.id.inputFieldOldPassword)
        val inputFieldNewPassword = findViewById<EditText>(R.id.inputFieldNewPassword)
        val inputFieldNewPasswordRepeat = findViewById<EditText>(R.id.inputFieldNewPasswordRepeat)

        // Инициализация TextInputLayout для отображения ошибок
        val inputFieldOldPasswordLayout = findViewById<TextInputLayout>(R.id.inputFieldOldPasswordLayout)
        val inputFieldNewPasswordLayout = findViewById<TextInputLayout>(R.id.inputFieldNewPasswordLayout)
        val inputFieldNewPasswordRepeatLayout = findViewById<TextInputLayout>(R.id.inputFieldNewPasswordRepeatLayout)

        // Установка слушателя на кнопку регистрации
        val buttonRegistration = findViewById<Button>(R.id.buttonRegistration)
        buttonRegistration.setOnClickListener {

            val isValid = validateForm(
                inputFieldOldPassword,
                inputFieldNewPassword,
                inputFieldNewPasswordRepeat,
                inputFieldOldPasswordLayout,
                inputFieldNewPasswordLayout,
                inputFieldNewPasswordRepeatLayout,
            )
            if (isValid) {
                updatePassword(inputFieldNewPassword, inputFieldOldPassword)
            } else {
                Toast.makeText(this, "Пожалуйста, исправьте ошибки в форме", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Функция для проверки корректности введённых данных в форме
    private fun validateForm(
        oldPassword: EditText,
        password: EditText,
        passwordRepeat: EditText,
        oldPasswordLayout: TextInputLayout,
        passwordLayout: TextInputLayout,
        passwordRepeatLayout: TextInputLayout,
    ): Boolean {
        var isValid = true

        // Проверка старого пароля
        val oldPasswordText = oldPassword.text.toString().trim()
        if (oldPasswordText.isEmpty()) {
            oldPasswordLayout.error = "Пароль не может быть пустым"
            isValid = false
        } else {
            oldPasswordLayout.error = null
        }

        // Проверка пароля
        val passwordText = password.text.toString().trim()
        val passwordRepeatText = passwordRepeat.text.toString().trim()
        if (passwordText.isEmpty()) {
            passwordLayout.error = "Пароль не может быть пустым"
            isValid = false
        } else if (passwordText.length < 8 || !passwordText.any { it.isDigit() } || !passwordText.any { it.isLetter() }) {
            passwordLayout.error = "Пароль должен содержать не менее 8 символов, буквы и цифры"
            isValid = false
        } else {
            passwordLayout.error = null
        }

        // Проверка повторного ввода пароля
        if (passwordRepeatText.isEmpty()) {
            passwordRepeatLayout.error = "Повторите пароль"
            isValid = false
        } else if (passwordText != passwordRepeatText) {
            passwordRepeatLayout.error = "Пароли не совпадают"
            isValid = false
        } else {
            passwordRepeatLayout.error = null
        }

        return isValid
    }

    private fun updatePassword(
        password: EditText,
        oldPassword: EditText
    ) {
        val oldPassword = oldPassword.text.toString()
        val newPassword = password.text.toString()

        // Создаем запрос
        val updatePassword = updatePasswordRequest(
            userId = loggedInUserId!!,
            oldPassword = oldPassword,
            password = newPassword
        )

        apiService.userUpdateProfile(updatePassword).enqueue(object : Callback<updatePasswordResponse> {
            override fun onResponse(call: Call<updatePasswordResponse>, response: Response<updatePasswordResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UpdatePassword, "Смена данных успешна!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                } else {
                    Toast.makeText(this@UpdatePassword, "Ошибка", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call: Call<updatePasswordResponse>, t: Throwable) {
                Log.e("Mismath", "Ошибка: ${t.message}")
                Toast.makeText(this@UpdatePassword, "Ошибка  ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}