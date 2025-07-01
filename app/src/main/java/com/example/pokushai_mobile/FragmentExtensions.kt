package com.example.pokushai_mobile

import android.os.Bundle
import android.view.View
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
        tag: String? = null,
        animations: Animations = defaultAnimations
    ) {
        fragmentManager.commit {
            setCustomAnimations(
                animations.enter,
                animations.exit,
                animations.popEnter,
                animations.popExit
            )
            replace(containerId, targetFragment, tag)
            addToBackStack(tag)
            setReorderingAllowed(true)
        }
    }

    fun navigateBack(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        }
    }

    fun setupBackPressHandler(fragment: Fragment) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack(fragment.parentFragmentManager)
            }
        }
        fragment.requireActivity().onBackPressedDispatcher.addCallback(
            fragment.viewLifecycleOwner,
            callback
        )
    }
}