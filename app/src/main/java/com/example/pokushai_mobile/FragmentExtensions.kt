package com.example.pokushai_mobile


import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit

object FragmentNavigator {
    private val defaultAnimations = Animations(
        enter = R.anim.fade_in,
        exit = R.anim.fade_out,
        popEnter = R.anim.fade_in,
        popExit = R.anim.fade_out
    )

    data class Animations(
        val enter: Int,
        val exit: Int,
        val popEnter: Int,
        val popExit: Int
    )

    fun navigateForward(
        fragmentManager: FragmentManager,
        containerId: Int,
        targetFragment: Fragment,
        tag: String? = targetFragment::class.java.simpleName,
        addToBackStack: Boolean = true,
        animations: Animations = defaultAnimations
    ) {
        val currentFragment = fragmentManager.findFragmentById(containerId)

        // Не заменяем фрагмент, если он уже отображается
        if (currentFragment?.javaClass == targetFragment.javaClass) {
            return
        }

        fragmentManager.commit {
            setCustomAnimations(
                animations.enter,
                animations.exit,
                animations.popEnter,
                animations.popExit
            )
            replace(containerId, targetFragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
            setReorderingAllowed(true)
        }
    }

    fun navigateBack(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            // Если стек пуст, можно закрыть активность или выполнить другое действие
            fragmentManager.primaryNavigationFragment?.activity?.finish()
        }
    }

    fun setupBackPressHandler(
        fragment: Fragment,
        customAction: (() -> Unit)? = null
    ) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                customAction?.invoke() ?: navigateBack(fragment.parentFragmentManager)
            }
        }

        fragment.requireActivity().onBackPressedDispatcher.addCallback(
            fragment.viewLifecycleOwner,
            callback
        )
    }
}