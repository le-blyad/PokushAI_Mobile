package com.example.pokushai_mobile

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePasswordFragment : Fragment() {

    private val apiService = ApiClient.instance
    private var loggedInUserId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getLong("user_id", -1)

        val buttonBack = view.findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val layout = view.findViewById<LinearLayout>(R.id.topMenu)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            layout.setBackgroundResource(R.drawable.bottom_menu_dark)
        } else {
            layout.setBackgroundResource(R.drawable.bottom_menu_light)
        }

        val inputFieldOldPassword = view.findViewById<EditText>(R.id.inputFieldOldPassword)
        val inputFieldNewPassword = view.findViewById<EditText>(R.id.inputFieldNewPassword)
        val inputFieldNewPasswordRepeat = view.findViewById<EditText>(R.id.inputFieldNewPasswordRepeat)

        val inputFieldOldPasswordLayout = view.findViewById<TextInputLayout>(R.id.inputFieldOldPasswordLayout)
        val inputFieldNewPasswordLayout = view.findViewById<TextInputLayout>(R.id.inputFieldNewPasswordLayout)
        val inputFieldNewPasswordRepeatLayout = view.findViewById<TextInputLayout>(R.id.inputFieldNewPasswordRepeatLayout)

        val buttonRegistration = view.findViewById<Button>(R.id.buttonRegistration)
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
                Toast.makeText(requireContext(), "Пожалуйста, исправьте ошибки в форме", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(
        oldPassword: EditText,
        password: EditText,
        passwordRepeat: EditText,
        oldPasswordLayout: TextInputLayout,
        passwordLayout: TextInputLayout,
        passwordRepeatLayout: TextInputLayout,
    ): Boolean {
        var isValid = true

        val oldPasswordText = oldPassword.text.toString().trim()
        if (oldPasswordText.isEmpty()) {
            oldPasswordLayout.error = "Пароль не может быть пустым"
            isValid = false
        } else {
            oldPasswordLayout.error = null
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

    private fun updatePassword(
        password: EditText,
        oldPassword: EditText
    ) {
        val oldPasswordText = oldPassword.text.toString()
        val newPassword = password.text.toString()

        val updatePassword = updatePasswordRequest(
            userId = loggedInUserId!!,
            oldPassword = oldPasswordText,
            password = newPassword
        )

        apiService.userUpdateProfile(updatePassword).enqueue(object : Callback<updatePasswordResponse> {
            override fun onResponse(call: Call<updatePasswordResponse>, response: Response<updatePasswordResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Смена данных успешна!", Toast.LENGTH_SHORT)
                        .show()
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call: Call<updatePasswordResponse>, t: Throwable) {
                Log.e("Mismath", "Ошибка: ${t.message}")
                Toast.makeText(requireContext(), "Ошибка  ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}