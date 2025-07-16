package com.example.pokushai_mobile

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationFragment : Fragment() {

    private val apiService = ApiClient.instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputFieldLogin = view.findViewById<EditText>(R.id.inputFieldLogin)
        val inputFieldNumberPhone = view.findViewById<EditText>(R.id.inputFieldNumberPhone)
        val inputFieldEmailAddress = view.findViewById<EditText>(R.id.inputFieldEmailAddress)
        val inputFieldPassword = view.findViewById<EditText>(R.id.inputFieldPassword)
        val inputFieldPasswordRepeat = view.findViewById<EditText>(R.id.inputFieldPasswordRepeat)

        val inputFieldLoginLayout = view.findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
        val inputFieldNumberPhoneLayout = view.findViewById<TextInputLayout>(R.id.inputFieldNumberPhoneLayout)
        val inputFieldEmailAddressLayout = view.findViewById<TextInputLayout>(R.id.inputFieldEmailAddressLayout)
        val inputFieldPasswordLayout = view.findViewById<TextInputLayout>(R.id.inputFieldPasswordLayout)
        val inputFieldPasswordRepeatLayout = view.findViewById<TextInputLayout>(R.id.inputFieldPasswordRepeatLayout)

        val layout = view.findViewById<LinearLayout>(R.id.topMenu)

        // Проверка темы
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            layout.setBackgroundResource(R.drawable.bottom_menu_dark)
        } else {
            layout.setBackgroundResource(R.drawable.bottom_menu_light)
        }

        val buttonBack = view.findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val buttonRegistration = view.findViewById<Button>(R.id.buttonRegistration)
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
                Toast.makeText(requireContext(), "Пожалуйста, исправьте ошибки в форме", Toast.LENGTH_SHORT).show()
            }
        }

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

        val loginText = login.text.toString().trim()
        if (loginText.isEmpty()) {
            loginLayout.error = "Логин не может быть пустым"
            isValid = false
        } else {
            loginLayout.error = null
        }

        val phoneNumberText = phoneNumber.text.toString().trim()
        if (phoneNumberText.isEmpty() || !isValidPhoneNumber(phoneNumberText)) {
            phoneNumberLayout.error = "Введите действительный номер телефона"
            isValid = false
        } else {
            phoneNumberLayout.error = null
        }

        val emailText = email.text.toString().trim()
        if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailLayout.error = "Введите действительный адрес электронной почты"
            isValid = false
        } else {
            emailLayout.error = null
        }

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

        val registerRequest = RegisterRequest(
            username = username,
            phone = number,
            email = emailText,
            password1 = passwordText,
            password2 = passwordText
        )

        apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Успешно зарегистрирован!", Toast.LENGTH_SHORT).show()
                    response.body()?.userId?.let { userId ->
                        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putLong("user_id", userId).apply()
                    }

                    parentFragmentManager.commit {
                        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        replace(R.id.fragment_container, UserFragment())
                        setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка в регистрации!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        val cleaned = phoneNumber.replace(Regex("[^0-9]"), "")
        return if ((cleaned.length == 11 && cleaned.startsWith("7")) || (cleaned.length == 11 && cleaned.startsWith("8"))) {
            "+7 (${cleaned.substring(1, 4)})-${cleaned.substring(4, 7)}-${cleaned.substring(7, 9)}-${cleaned.substring(9, 11)}"
        } else {
            phoneNumber
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val cleaned = phoneNumber.replace(Regex("[^0-9]"), "")
        return (cleaned.length == 11 && (cleaned.startsWith("7") || cleaned.startsWith("8")))
    }
}