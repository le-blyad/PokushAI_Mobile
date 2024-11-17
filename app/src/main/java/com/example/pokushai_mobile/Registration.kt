package com.example.pokushai_mobile

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import android.util.Patterns
import android.widget.LinearLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Registration : AppCompatActivity() {

    val apiService = ApiClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        // Инициализация полей ввода
        val inputFieldLogin = findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldNumberPhone = findViewById<EditText>(R.id.inputFieldNumberPhone)
        val inputFieldEmailAddress = findViewById<EditText>(R.id.inputFieldEmailAddress)
        val inputFieldPassword = findViewById<EditText>(R.id.inputFieldPassword)
        val inputFieldPasswordRepeat = findViewById<EditText>(R.id.inputFieldPasswordRepeat)

        // Инициализация TextInputLayout для отображения ошибок
        val inputFieldLoginLayout = findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
        val inputFieldNumberPhoneLayout = findViewById<TextInputLayout>(R.id.inputFieldNumberPhoneLayout)
        val inputFieldEmailAddressLayout = findViewById<TextInputLayout>(R.id.inputFieldEmailAddressLayout)
        val inputFieldPasswordLayout = findViewById<TextInputLayout>(R.id.inputFieldPasswordLayout)
        val inputFieldPasswordRepeatLayout = findViewById<TextInputLayout>(R.id.inputFieldPasswordRepeatLayout)


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

        // Установка слушателя на кнопку регистрации
        val buttonRegistration = findViewById<Button>(R.id.buttonRegistration)
        buttonRegistration.setOnClickListener {
            val isValid = validateForm(
                inputFieldLogin,
                inputFieldNumberPhone,
                inputFieldEmailAddress,
                inputFieldPassword,
                inputFieldPasswordRepeat,
                inputFieldLoginLayout,
                inputFieldNumberPhoneLayout,
                inputFieldEmailAddressLayout,
                inputFieldPasswordLayout,
                inputFieldPasswordRepeatLayout
            )
            if (isValid) {
                registerUser(inputFieldLogin, inputFieldPassword, inputFieldNumberPhone, inputFieldEmailAddress)
            } else {
                Toast.makeText(this, "Пожалуйста, исправьте ошибки в форме", Toast.LENGTH_SHORT).show()
            }
        }

        // Установка слушателя на кнопку возврата
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // Форматирование номера телефона при вводе
        inputFieldNumberPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val formattedPhoneNumber = formatPhoneNumber(s.toString())
                if (s.toString() != formattedPhoneNumber) {
                    inputFieldNumberPhone.removeTextChangedListener(this)
                    inputFieldNumberPhone.setText(formattedPhoneNumber)
                    inputFieldNumberPhone.setSelection(formattedPhoneNumber.length)
                    inputFieldNumberPhone.addTextChangedListener(this)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Функция для проверки корректности введённых данных в форме
    private fun validateForm(
        login: EditText,
        phoneNumber: EditText,
        email: EditText,
        password: EditText,
        passwordRepeat: EditText,
        loginLayout: TextInputLayout,
        phoneNumberLayout: TextInputLayout,
        emailLayout: TextInputLayout,
        passwordLayout: TextInputLayout,
        passwordRepeatLayout: TextInputLayout
    ): Boolean {
        var isValid = true

        // Проверка логина
        val loginText = login.text.toString().trim()
        if (loginText.isEmpty()) {
            loginLayout.error = "Логин не может быть пустым"
            isValid = false
        } else {
            loginLayout.error = null
        }

        // Проверка номера телефона
        val phoneNumberText = phoneNumber.text.toString().trim()
        if (phoneNumberText.isEmpty() || !isValidPhoneNumber(phoneNumberText)) {
            phoneNumberLayout.error = "Введите действительный номер телефона"
            isValid = false
        } else {
            phoneNumberLayout.error = null
        }

        // Проверка email
        val emailText = email.text.toString().trim()
        if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailLayout.error = "Введите действительный адрес электронной почты"
            isValid = false
        } else {
            emailLayout.error = null
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

    // Функция для регистрации пользователя
    private fun registerUser(
        login: EditText,
        password: EditText,
        phoneNumber: EditText,
        email: EditText
    ) {
        val username = login.text.toString()
        val passwordText = password.text.toString()
        val number = phoneNumber.text.toString()
        val emailText = email.text.toString()

        // Создаем запрос
        val registerRequest = RegisterRequest(
            username = username,
            phone = number,
            email = emailText,
            password1 = passwordText,
            password2 = passwordText // Используем один и тот же пароль
        )
        // Отправляем запрос через Retrofit
        apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Registration, "Успешно зарегистрирован!", Toast.LENGTH_SHORT).show()

                    val registerResponse = response.body()
                    // Сохранение информации о пользователе
                    registerResponse?.userId?.let { userId ->
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        sharedPreferences.edit().putLong("user_id", userId).apply() // Используйте userId из ответа
                    }
                    // Перенаправляем на следующую активность
                    val intent = Intent(this@Registration, SecondActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish() // Завершаем текущую активность
                } else {
                    Toast.makeText(this@Registration, "Ошибка в регистрации!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@Registration, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Функция для форматирования номера телефона
    private fun formatPhoneNumber(phoneNumber: String): String {
        val cleaned = phoneNumber.replace(Regex("[^0-9]"), "")
        return if ((cleaned.length == 11 && cleaned.startsWith("7")) || (cleaned.length == 11 && cleaned.startsWith("8"))) {
            "+7 (${cleaned.substring(1, 4)})-${cleaned.substring(4, 7)}-${cleaned.substring(7, 9)}-${cleaned.substring(9, 11)}"
        } else {
            phoneNumber // Возвращаем исходный номер, если он некорректен
        }
    }

    // Функция для проверки корректности номера телефона
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val cleaned = phoneNumber.replace(Regex("[^0-9]"), "")
        return (cleaned.length == 11 && (cleaned.startsWith("7") || cleaned.startsWith("8")))
    }
}
