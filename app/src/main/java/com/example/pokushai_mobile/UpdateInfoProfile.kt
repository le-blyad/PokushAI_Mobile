package com.example.pokushai_mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpdateInfoProfile : AppCompatActivity() {

    private val apiService = ApiClient.instance
    private var loggedInUserId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_info_profile)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getLong("user_id", -1)

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // Инициализация полей ввода
        val inputFieldLogin = findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldNumberPhone = findViewById<EditText>(R.id.inputFieldNumberPhone)
        val inputFieldEmailAddress = findViewById<EditText>(R.id.inputFieldEmailAddress)

        // Инициализация TextInputLayout для отображения ошибок
        val inputFieldLoginLayout = findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
        val inputFieldNumberPhoneLayout = findViewById<TextInputLayout>(R.id.inputFieldNumberPhoneLayout)
        val inputFieldEmailAddressLayout = findViewById<TextInputLayout>(R.id.inputFieldEmailAddressLayout)

        // Установка слушателя на кнопку регистрации
        val buttonRegistration = findViewById<Button>(R.id.buttonRegistration)
        buttonRegistration.setOnClickListener {
            val isValid = validateForm(
                inputFieldLogin,
                inputFieldNumberPhone,
                inputFieldEmailAddress,
                inputFieldLoginLayout,
                inputFieldNumberPhoneLayout,
                inputFieldEmailAddressLayout,
            )
            if (isValid) {
                userUpdateProfile(inputFieldLogin, inputFieldNumberPhone, inputFieldEmailAddress)
            } else {
                Toast.makeText(this, "Пожалуйста, исправьте ошибки в форме", Toast.LENGTH_SHORT).show()
            }
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
        loginLayout: TextInputLayout,
        phoneNumberLayout: TextInputLayout,
        emailLayout: TextInputLayout,
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

        return isValid
    }

    private fun userUpdateProfile(
        login: EditText,
        phoneNumber: EditText,
        email: EditText
    ) {
        val username = login.text.toString()
        val number = phoneNumber.text.toString()
        val emailText = email.text.toString()

        // Создаем запрос
        val userUpdateProfile = userUpdateProfileRequest(
            userId = loggedInUserId!!,
            username = username,
            email = emailText,
            phone = number,
        )


        apiService.userUpdateProfile(userUpdateProfile).enqueue(object : Callback<userUpdateProfileResponse> {
            override fun onResponse(call: Call<userUpdateProfileResponse>, response: Response<userUpdateProfileResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateInfoProfile, "Смена данных успешна!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call: Call<userUpdateProfileResponse>, t: Throwable) {
                Toast.makeText(this@UpdateInfoProfile, "Ошибка", Toast.LENGTH_SHORT).show()
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