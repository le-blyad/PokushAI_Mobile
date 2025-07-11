package com.example.pokushai_mobile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import androidx.fragment.app.Fragment

class FilterFragment : Fragment(R.layout.fragment_filter) {

    private val visibilityStates = BooleanArray(50) { true }
    private lateinit var communicator: FilterCommunicator

    interface FilterCommunicator {
        fun updateSwitchVisibility(positions: List<Int>, isVisible: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Use targetFragment instead of parentFragment
        val target = targetFragment
        if (target is FilterCommunicator) {
            communicator = target
        } else {
            throw RuntimeException("${targetFragment} must implement FilterCommunicator")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = view.findViewById<LinearLayout>(R.id.filter_switches_container)

        // Создаем 50 свитчей-фильтров
        for (i in 0 until 50) {
            val switch = Switch(requireContext()).apply {
                text = "Ингредиент ${i + 1}"
                isChecked = true
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(20, 10, 20, 10)
                }
            }
            container.addView(switch)
        }

        view.findViewById<Button>(R.id.apply_filters).setOnClickListener {
            // Применяем все фильтры сразу
            for (i in 0 until 50) {
                val switch = container.getChildAt(i) as Switch
                visibilityStates[i] = switch.isChecked
                communicator.updateSwitchVisibility(listOf(i), switch.isChecked)
            }
            parentFragmentManager.popBackStack()
        }
    }
}