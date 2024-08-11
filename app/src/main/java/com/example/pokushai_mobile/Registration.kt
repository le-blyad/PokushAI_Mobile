package com.example.pokushai_mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.text.Editable
import android.text.TextWatcher

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val inputFieldLogin = findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldLoginError = findViewById<TextView>(R.id.inputFieldLoginError)
        inputFieldLoginError.visibility = TextView.GONE

        val inputFieldNumberPhone = findViewById<EditText>(R.id.inputFieldNumberPhone)
        val inputFieldNumberPhoneError = findViewById<TextView>(R.id.inputFieldNumberPhoneError)
        inputFieldNumberPhoneError.visibility = TextView.GONE

        val inputFieldEmailAddress = findViewById<EditText>(R.id.inputFieldEmailAddress)
        val inputFieldEmailAddressError = findViewById<TextView>(R.id.inputFieldEmailAddressError)
        inputFieldEmailAddressError.visibility = TextView.GONE

        val inputFieldPassword = findViewById<EditText>(R.id.inputFieldPassword)
        val inputFieldPasswordError = findViewById<TextView>(R.id.inputFieldPasswordError)
        inputFieldPasswordError.visibility = TextView.GONE

        val inputFieldPasswordRepeat = findViewById<EditText>(R.id.inputFieldPasswordRepeat)
        val inputFieldPasswordRepeatError = findViewById<TextView>(R.id.inputFieldPasswordRepeatError)
        inputFieldPasswordRepeatError.visibility = TextView.GONE


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

        val buttonRegistration = findViewById<Button>(R.id.buttonRegistration)
        buttonRegistration.setOnClickListener {

            fun EditText.isEmpty(): Boolean {
                return this.text.toString().trim().isEmpty()
            }


            if (inputFieldLogin.isEmpty()) {
                inputFieldLoginError.text = "Поле с логином пустое!"
                inputFieldLoginError.visibility = TextView.VISIBLE
            } else {
                inputFieldLoginError.visibility = TextView.GONE
            }


            val phoneNumber = inputFieldNumberPhone.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                inputFieldNumberPhoneError.text = "Поле с номером телефона пустое!"
                inputFieldNumberPhoneError.visibility = TextView.VISIBLE
            } else if (!isValidPhoneNumber(phoneNumber)) {
                inputFieldNumberPhoneError.text = "Неверный формат номера телефона!"
                inputFieldNumberPhoneError.visibility = TextView.VISIBLE
            } else {
                inputFieldNumberPhoneError.visibility = TextView.GONE
                val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
                inputFieldNumberPhone.setText(formattedPhoneNumber) // Форматируем и устанавливаем текст
            }

            val email = inputFieldEmailAddress.text.toString().trim()
            if (email.isEmpty()) {
                inputFieldEmailAddressError.visibility = TextView.GONE
            } else if (!isValidEmail(email)) {
                inputFieldEmailAddressError.text = "Неверно введён E-mail!"
                inputFieldEmailAddressError.visibility = TextView.VISIBLE
            } else {
                inputFieldEmailAddressError.visibility = TextView.GONE
            }

            if (inputFieldPassword.isEmpty()) {
                inputFieldPasswordError.text = "Поле с паролем пустое!"
                inputFieldPasswordError.visibility = TextView.VISIBLE
            } else {
                inputFieldPasswordError.visibility = TextView.GONE
            }

            if (inputFieldPasswordRepeat.isEmpty()) {
                inputFieldPasswordRepeatError.text = "Поле с повторным паролем пустое!"
                inputFieldPasswordRepeatError.visibility = TextView.VISIBLE
            } else if (inputFieldPassword.text.toString() != inputFieldPasswordRepeat.text.toString()) {
                inputFieldPasswordRepeatError.text = "Пароли не совпадают!"
                inputFieldPasswordRepeatError.visibility = TextView.VISIBLE
            } else {
                inputFieldPasswordRepeatError.visibility = TextView.GONE
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
        val emailRegex = "^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        val cleaned = phoneNumber.replace(Regex("[^0-9]"), "") // Удаляем все нецифровые символы

        return if ((cleaned.length == 11 && cleaned.startsWith("7")) || (cleaned.length == 11 && cleaned.startsWith("8"))) {
            "+7 (${cleaned.substring(1, 4)}) ${cleaned.substring(4, 7)}-${cleaned.substring(7, 9)}-${cleaned.substring(9, 11)}"
        } else {
            phoneNumber // Возвращаем исходный номер, если он некорректен
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val cleaned = phoneNumber.replace(Regex("[^0-9]"), "")
        return (cleaned.length == 11 && cleaned.startsWith("7")) || (cleaned.length == 11 && cleaned.startsWith("8"))
    }
}
