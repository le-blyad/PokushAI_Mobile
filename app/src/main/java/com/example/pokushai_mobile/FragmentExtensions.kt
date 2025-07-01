package com.example.pokushai_mobile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit

/**
 * Навигация между фрагментами с гарантированной анимацией
 */
object FragmentNavigator {

    // 1. Функция для перехода вперед
    fun navigateForward(
        fragmentManager: FragmentManager,
        containerId: Int,
        targetFragment: Fragment,
        tag: String? = null
    ) {
        fragmentManager.commit {
            // Устанавливаем анимации для перехода вперед и назад
            setCustomAnimations(
                R.anim.fade_in,   // Анимация входа нового фрагмента
                R.anim.fade_out,  // Анимация выхода текущего фрагмента
                R.anim.fade_in,   // Анимация входа при возврате (popEnter)
                R.anim.fade_out   // Анимация выхода при возврате (popExit)
            )
            replace(containerId, targetFragment, tag)
            addToBackStack(tag)
            setReorderingAllowed(true)  // Важно для корректной работы анимаций
        }
    }

    // 2. Функция для возврата назад
    fun navigateBack(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
    }
}

/**
 * Расширения для упрощения навигации
 */
fun Fragment.navigateForward(
    containerId: Int,
    targetFragment: Fragment,
    tag: String? = null
) {
    FragmentNavigator.navigateForward(
        requireActivity().supportFragmentManager,
        containerId,
        targetFragment,
        tag
    )
}

fun Fragment.navigateBack() {
    FragmentNavigator.navigateBack(requireActivity().supportFragmentManager)
}

/**
 * Настройка кнопки "Назад" с гарантированной работой анимации
 */
fun Fragment.setupBackButton(backButton: View) {
    backButton.setOnClickListener {
        // Используем popBackStackImmediate для немедленного выполнения
        if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
            FragmentNavigator.navigateBack(requireActivity().supportFragmentManager)
        } else {
            activity?.finish()
        }
    }
}