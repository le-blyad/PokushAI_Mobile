package com.example.pokushai_mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.ImageButton
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

class Registration : AppCompatActivity() {

    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        userDAO = UserDAO(this)

        val inputFieldLogin = findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldNumberPhone = findViewById<EditText>(R.id.inputFieldNumberPhone)
        val inputFieldEmailAddress = findViewById<EditText>(R.id.inputFieldEmailAddress)
        val inputFieldPassword = findViewById<EditText>(R.id.inputFieldPassword)
        val inputFieldPasswordRepeat = findViewById<EditText>(R.id.inputFieldPasswordRepeat)

        var (validLogin, validNumberPhone, validEmailAddress, validPassword, validPasswordRepeat) = List(5) { false }

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

        val textError = findViewById<TextView>(R.id.textError)
        textError.visibility = TextView.INVISIBLE

        //Кнопка регистрации
        val buttonRegistration = findViewById<Button>(R.id.buttonRegistration)
        buttonRegistration.setOnClickListener {


            val inputFieldLoginLayout = findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
            val inputFieldNumberPhoneLayout = findViewById<TextInputLayout>(R.id.inputFieldNumberPhoneLayout)
            val inputFieldEmailAddressLayout = findViewById<TextInputLayout>(R.id.inputFieldEmailAddressLayout)
            val inputFieldPasswordLayout = findViewById<TextInputLayout>(R.id.inputFieldPasswordLayout)
            val inputFieldPasswordRepeatLayout = findViewById<TextInputLayout>(R.id.inputFieldPasswordRepeatLayout)

            if (isInternetAvailable(this)) {


                fun EditText.isEmpty(): Boolean {
                    return this.text.toString().trim().isEmpty()
                }

                //Валидация логина
                if (inputFieldLogin.isEmpty()) {
                    inputFieldLoginLayout.error = "Поле с логином пустое!"
                } else {
                    inputFieldLoginLayout.error = null
                    validLogin = true
                }

                //Валидация номера телефона
                val phoneNumber = inputFieldNumberPhone.text.toString().trim()
                if (phoneNumber.isEmpty()) {
                    inputFieldNumberPhoneLayout.error = "Поле с номером телефона пустое!"
                } else if (!isValidPhoneNumber(phoneNumber)) {
                    inputFieldNumberPhoneLayout.error = "Неверный формат номера телефона!"
                } else {
                    val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
                    inputFieldNumberPhoneLayout.error = null
                    inputFieldNumberPhone.setText(formattedPhoneNumber)
                    validNumberPhone = true
                }

                // Валидация E-mail
                val email = inputFieldEmailAddress.text.toString().trim()

                if (email.isEmpty()) {
                    inputFieldEmailAddressLayout.error = null
                    validEmailAddress = true
                } else if (!isValidEmail(email)) {
                    // Если E-mail неверного формата
                    inputFieldEmailAddressLayout.error = "Неверно введён E-mail!"
                    validEmailAddress = false
                } else {
                    // Если E-mail валидный
                    inputFieldEmailAddressLayout.error = null
                    validEmailAddress = true
                }


                var countNumbers = 0
                var countLetters = 0

                //Условия существования пароля
                for (i in inputFieldPassword.text.indices) {
                    val char = inputFieldPassword.text[i]
                    if (char.isLetter()) {
                        countLetters += 1
                    } else if (char.isDigit()) {
                        countNumbers += 1
                    }
                }

                //Валидация пароля
                if (inputFieldPassword.isEmpty()) {
                    inputFieldPasswordLayout.error = "Поле с паролем пустое!"
                } else if (inputFieldPassword.text.length < 8) {
                    inputFieldPasswordLayout.error = "Пароль меньше 8 символов!"
                } else if (countNumbers == 0) {
                    inputFieldPasswordLayout.error = "Пароль должен иметь цифры!"
                } else if (countLetters==0) {
                    inputFieldPasswordLayout.error = "Пароль должен иметь буквы!"
                } else {
                    inputFieldPasswordLayout.error = null
                    validPassword = true
                }


                //Валидация повторного ввода пароля
                if (inputFieldPasswordRepeat.isEmpty()) {
                    inputFieldPasswordRepeatLayout.error = "Поле с повторным паролем пустое!"
                } else if (inputFieldPassword.text.toString() != inputFieldPasswordRepeat.text.toString()) {
                    inputFieldPasswordRepeatLayout.error = "Пароли не совпадают!"
                } else {
                    inputFieldPasswordRepeatLayout.error = null
                    validPasswordRepeat = true
                }

                val allValid = listOf(validLogin, validNumberPhone, validEmailAddress, validPassword, validPasswordRepeat).all { it }

                if (allValid) {

                    val username = inputFieldLogin.text.toString()
                    val password = inputFieldPassword.text.toString()
                    val number = inputFieldNumberPhone.text.toString()
                    val email = inputFieldEmailAddress.text.toString()
                    val result = userDAO.addUser(username, password, number, email)
                    if (result != -1L) {
                        Toast.makeText(this, "Успешно зарегистрирован!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Ошибка в регистрации!", Toast.LENGTH_SHORT).show()
                    }
                    val isValid = userDAO.checkUser(username, password)
                    if (isValid) {
                        userDAO.setUserLoggedIn(username) // Установка пользователя как вошедшего
                        val intent = Intent(this, SecondActivity::class.java)
                        startActivity(intent)
                    }
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }


            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
            }

        }

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        // Регулярное выражение для проверки формата электронной почты
        val emailRegex = "^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        val cleaned = phoneNumber.replace(Regex("[^0-9]"), "") // Удаляем все нецифровые символы

        return if ((cleaned.length == 11 && cleaned.startsWith("7")) || (cleaned.length == 11 && cleaned.startsWith("8"))) {
            "+7 (${cleaned.substring(1, 4)})-${cleaned.substring(4, 7)}-${cleaned.substring(7, 9)}-${cleaned.substring(9, 11)}"
        } else {
            phoneNumber // Возвращаем исходный номер, если он некорректен
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val cleaned = phoneNumber.replace(Regex("[^0-9]"), "")
        return (cleaned.length == 11 && cleaned.startsWith("7")) || (cleaned.length == 11 && cleaned.startsWith("8"))
    }
}