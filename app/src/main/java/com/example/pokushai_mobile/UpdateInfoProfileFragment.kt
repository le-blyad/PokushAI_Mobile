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

class UpdateInfoProfileFragment : Fragment() {

    private val apiService = ApiClient.instance
    private var loggedInUserId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_update_info_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ID пользователя
        val sharedPrefs = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        loggedInUserId = sharedPrefs.getLong("user_id", -1L)

        // Кнопка "назад"
        view.findViewById<ImageButton>(R.id.buttonBack).setOnClickListener {
            parentFragmentManager.popBackStack("user", 0)
        }

        // Тема
        val layout = view.findViewById<LinearLayout>(R.id.topMenu)
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        layout.setBackgroundResource(
            if (nightMode == Configuration.UI_MODE_NIGHT_YES)
                R.drawable.bottom_menu_dark
            else
                R.drawable.bottom_menu_light
        )

        // Поля формы
        val etLogin = view.findViewById<EditText>(R.id.inputFieldLogin)
        val etPhone = view.findViewById<EditText>(R.id.inputFieldNumberPhone)
        val etEmail = view.findViewById<EditText>(R.id.inputFieldEmailAddress)

        val layoutLogin = view.findViewById<TextInputLayout>(R.id.inputFieldLoginLayout)
        val layoutPhone = view.findViewById<TextInputLayout>(R.id.inputFieldNumberPhoneLayout)
        val layoutEmail = view.findViewById<TextInputLayout>(R.id.inputFieldEmailAddressLayout)

        // Форматирование номера
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val formatted = formatPhoneNumber(s.toString())
                if (s.toString() != formatted) {
                    etPhone.removeTextChangedListener(this)
                    etPhone.setText(formatted)
                    etPhone.setSelection(formatted.length)
                    etPhone.addTextChangedListener(this)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        // Кнопка сохранить
        view.findViewById<Button>(R.id.buttonRegistration).setOnClickListener {
            if (validateForm(etLogin, etPhone, etEmail, layoutLogin, layoutPhone, layoutEmail)) {
                userUpdateProfile(etLogin, etPhone, etEmail)
            } else {
                Toast.makeText(requireContext(),
                    "Пожалуйста, исправьте ошибки в форме",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateForm(
        login: EditText,
        phone: EditText,
        email: EditText,
        loginLayout: TextInputLayout,
        phoneLayout: TextInputLayout,
        emailLayout: TextInputLayout
    ): Boolean {
        var valid = true

        if (login.text.toString().trim().isEmpty()) {
            loginLayout.error = "Логин не может быть пустым"
            valid = false
        } else loginLayout.error = null

        val phoneText = phone.text.toString().trim()
        if (phoneText.isEmpty() || !isValidPhoneNumber(phoneText)) {
            phoneLayout.error = "Введите действительный номер телефона"
            valid = false
        } else phoneLayout.error = null

        val emailText = email.text.toString().trim()
        if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailLayout.error = "Введите действительный адрес электронной почты"
            valid = false
        } else emailLayout.error = null

        return valid
    }

    private fun userUpdateProfile(
        login: EditText,
        phone: EditText,
        email: EditText
    ) {
        // Формируем запрос
        val req = userUpdateProfileRequest(
            userId   = loggedInUserId,
            username = login.text.toString().trim(),
            email    = email.text.toString().trim(),
            phone    = phone.text.toString().trim()
        )

        apiService.userUpdateProfile(req)
            .enqueue(object : Callback<userUpdateProfileResponse> {
                override fun onResponse(
                    call: Call<userUpdateProfileResponse>,
                    response: Response<userUpdateProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Смена данных успешна!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateBackToUser()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Ошибка сервера: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                override fun onFailure(
                    call: Call<userUpdateProfileResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка сети: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun navigateBackToUser() {
        val fm = parentFragmentManager

        // 1. Очистка back‑stack
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        // 2. Ставим MainFragment как корень
        fm.commit {
            replace(R.id.fragment_container, MainFragment())
        }

        // 3. Ставим UserFragment поверх, добавляя в back‑stack
        fm.commit {
            setCustomAnimations(
                R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out
            )
            replace(R.id.fragment_container, UserFragment())
            addToBackStack(null)
        }
    }

    private fun formatPhoneNumber(raw: String): String {
        val digits = raw.replace(Regex("[^0-9]"), "")
        return if (digits.length == 11 && (digits.startsWith("7") || digits.startsWith("8"))) {
            "+7 (${digits.substring(1, 4)})-${digits.substring(4, 7)}-" +
                    "${digits.substring(7, 9)}-${digits.substring(9, 11)}"
        } else raw
    }

    private fun isValidPhoneNumber(raw: String): Boolean {
        val digits = raw.replace(Regex("[^0-9]"), "")
        return digits.length == 11 && (digits.startsWith("7") || digits.startsWith("8"))
    }
}
