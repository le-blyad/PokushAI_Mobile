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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePasswordFragment : Fragment() {

    private val apiService = ApiClient.instance
    private var loggedInUserId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_update_password, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем сохранённый user_id
        val sharedPrefs = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        loggedInUserId = sharedPrefs.getLong("user_id", -1L)

        // Кнопка "назад" просто возвращала на предыдущий экран — оставим её
        view.findViewById<ImageButton>(R.id.buttonBack).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Установка цвета меню по теме
        val layout = view.findViewById<LinearLayout>(R.id.topMenu)
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        layout.setBackgroundResource(
            if (nightMode == Configuration.UI_MODE_NIGHT_YES)
                R.drawable.bottom_menu_dark
            else
                R.drawable.bottom_menu_light
        )

        // Поля ввода
        val etOld = view.findViewById<EditText>(R.id.inputFieldOldPassword)
        val etNew = view.findViewById<EditText>(R.id.inputFieldNewPassword)
        val etRepeat = view.findViewById<EditText>(R.id.inputFieldNewPasswordRepeat)

        val layoutOld = view.findViewById<TextInputLayout>(R.id.inputFieldOldPasswordLayout)
        val layoutNew = view.findViewById<TextInputLayout>(R.id.inputFieldNewPasswordLayout)
        val layoutRepeat = view.findViewById<TextInputLayout>(R.id.inputFieldNewPasswordRepeatLayout)

        // Кнопка "Сменить пароль"
        view.findViewById<Button>(R.id.buttonRegistration).setOnClickListener {
            if (validateForm(etOld, etNew, etRepeat, layoutOld, layoutNew, layoutRepeat)) {
                updatePassword(etNew, etOld)
            } else {
                Toast.makeText(requireContext(),
                    "Пожалуйста, исправьте ошибки в форме",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateForm(
        oldPassword: EditText,
        newPassword: EditText,
        repeatPassword: EditText,
        oldLayout: TextInputLayout,
        newLayout: TextInputLayout,
        repeatLayout: TextInputLayout
    ): Boolean {
        var valid = true

        if (oldPassword.text.toString().trim().isEmpty()) {
            oldLayout.error = "Пароль не может быть пустым"
            valid = false
        } else oldLayout.error = null

        val newText = newPassword.text.toString().trim()
        if (newText.isEmpty()) {
            newLayout.error = "Пароль не может быть пустым"
            valid = false
        } else if (newText.length < 8 ||
            !newText.any { it.isDigit() } ||
            !newText.any { it.isLetter() }) {
            newLayout.error = "Пароль должен содержать не менее 8 символов, буквы и цифры"
            valid = false
        } else newLayout.error = null

        val repeatText = repeatPassword.text.toString().trim()
        if (repeatText.isEmpty()) {
            repeatLayout.error = "Повторите пароль"
            valid = false
        } else if (repeatText != newText) {
            repeatLayout.error = "Пароли не совпадают"
            valid = false
        } else repeatLayout.error = null

        return valid
    }

    private fun updatePassword(
        newPassword: EditText,
        oldPassword: EditText
    ) {
        val req = updatePasswordRequest(
            userId      = loggedInUserId,
            oldPassword = oldPassword.text.toString(),
            password    = newPassword.text.toString()
        )

        apiService.userUpdateProfile(req)
            .enqueue(object : Callback<updatePasswordResponse> {
                override fun onResponse(
                    call: Call<updatePasswordResponse>,
                    response: Response<updatePasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(),
                            "Смена пароля успешна!",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack("user", 0)
                    } else {
                        Toast.makeText(requireContext(),
                            "Ошибка сервера: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<updatePasswordResponse>,
                    t: Throwable
                ) {
                    Log.e("UpdatePwd", "Ошибка: ${t.message}")
                    Toast.makeText(requireContext(),
                        "Ошибка сети: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun navigateBackToUser() {
        val fm = parentFragmentManager

        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        fm.commit {
            replace(R.id.fragment_container, MainFragment())
        }

        fm.commit {
            setCustomAnimations(
                R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out
            )
            replace(R.id.fragment_container, UserFragment())
            addToBackStack(null)
        }
    }
}
